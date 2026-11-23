package com.knoux.ai.cloud

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Source-contract tests for the Supabase schema prep (P5). No local Postgres
 * exists in this environment, so these tests pin the SECURITY-CRITICAL
 * properties of the SQL files themselves: RLS on every table, owner-only
 * policies, consent-enforced inserts, cascade erasure, deny-by-default audit,
 * and zero secrets. Deployment to the live project remains
 * CONFIGURATION_REQUIRED and is NOT claimed here.
 */
class SupabaseSchemaContractTest {

    private val root: java.io.File by lazy {
        val wd = java.io.File(System.getProperty("user.dir")).absoluteFile
        listOf(wd, wd.parentFile, wd.parentFile?.parentFile)
            .map { java.io.File(it, "database/supabase").absoluteFile }
            .first { it.isDirectory }
    }

    private fun sql(rel: String): String =
        java.io.File(root, rel).takeIf { it.isFile }?.readText()
            ?: error("Missing schema file: database/supabase/$rel")

    private val core = sql("001_core.sql")
    private val rls = sql("002_rls.sql")
    private val erasure = sql("003_account_erasure.sql")
    private val tests = sql("tests/001_rls_structure.sql")

    private val appTables = listOf(
        "ka_profiles", "ka_devices", "ka_scan_sessions", "ka_operation_requests",
        "ka_operation_item_outcomes", "ka_storage_snapshots", "ka_battery_samples",
    )

    @Test
    fun everyApplicationTableIsCreatedAndUserOwned() {
        appTables.forEach { table ->
            assertTrue("001 must create $table", core.contains("public.$table"))
        }
        // Every non-profile table must be owned by auth.users with cascade.
        // ka_operation_item_outcomes is owned through its parent operation
        // (verified separately in itemOutcomesAreOwnedThroughTheirParentOperation).
        appTables.forEach { table ->
            val owned = core.contains("create table if not exists public.$table") &&
                (table == "ka_profiles" || table == "ka_operation_item_outcomes" ||
                    Regex("public\\.$table[\\s\\S]*?user_id\\s+uuid not null references auth\\.users \\(id\\) on delete cascade")
                        .containsMatchIn(core))
            assertTrue("$table must cascade from auth.users on delete", owned)
        }
        assertTrue(core.contains("references auth.users (id) on delete cascade"))
    }

    @Test
    fun rlsIsEnabledOnEveryApplicationTable() {
        appTables.forEach { table ->
            assertTrue("002 must enable RLS on $table",
                Regex("alter table public\\.$table\\s+enable row level security").containsMatchIn(rls))
        }
        assertTrue("erasure audit table must also be RLS-locked",
            Regex("alter table public\\.ka_erasure_audit\\s+enable row level security").containsMatchIn(erasure))
    }

    @Test
    fun everyTableHasOwnerPoliciesScopedToAuthUid() {
        appTables.forEach { table ->
            listOf("select", "insert", "update", "delete").forEach { op ->
                if (table == "ka_operation_requests" && op == "insert") return@forEach // consent-only
                assertTrue("002 must define owner $op policy on $table",
                    Regex("create policy ka_owner_$op on public\\.$table\\b").containsMatchIn(rls))
            }
        }
        // All policies must reference auth.uid().
        val policyBodies = rls.substringAfter("-- Owner-only CRUD")
        assertTrue(policyBodies.contains("auth.uid() = user_id"))
        assertFalse("no policy may allow anon role", rls.contains("to anon"))
    }

    @Test
    fun operationInsertsRequireRealConsent() {
        assertTrue(Regex("create policy ka_op_consented_insert on public\\.ka_operation_requests\\b").containsMatchIn(rls))
        assertTrue(rls.contains("consent_given = true"))
        assertTrue(rls.contains("consent_at is not null"))
        // No plain owner insert on the consent-gated table.
        assertFalse(Regex("create policy ka_owner_insert on public\\.ka_operation_requests\\b").containsMatchIn(rls))
        // Table-level CHECK as second line of defense.
        assertTrue(core.contains("consent_given = false or consent_at is not null"))
    }

    @Test
    fun itemOutcomesAreOwnedThroughTheirParentOperation() {
        assertTrue(rls.contains("ka_operation_item_outcomes"))
        assertTrue(rls.contains("r.user_id = auth.uid()"))
    }

    @Test
    fun erasureFunctionIsUserScopedAndAuditable() {
        assertTrue(erasure.contains("auth.uid()"))
        assertTrue(erasure.contains("security invoker"))
        assertTrue(erasure.contains("raise exception") && erasure.contains("authenticated user"))
        assertTrue(erasure.contains("delete from public.ka_profiles where id = v_user"))
        assertTrue(erasure.contains("ka_erasure_audit"))
        assertTrue("erasure must not be callable by anon",
            erasure.contains("revoke all on function public.ka_erase_my_account() from public"))
        assertTrue(erasure.contains("grant execute on function public.ka_erase_my_account() to authenticated"))
        // Audit table deny-by-default: RLS enabled, no policies created for it.
        val auditSection = erasure.substringAfter("ka_erasure_audit")
        assertFalse("no policy may be created on the audit table",
            Regex("create policy[\\s\\S]*ka_erasure_audit").containsMatchIn(auditSection))
    }

    @Test
    fun behavioralTestScriptCoversTwoRealUsersAndConsentAndCrossOwner() {
        listOf("B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8").forEach { step ->
            assertTrue("tests must include $step", tests.contains("$step."))
        }
        assertTrue(tests.contains("consent_given = false"))
        assertTrue(tests.contains("consent_at = null"))
        assertTrue(tests.contains("user JWTs") || tests.contains("Bearer"))
        assertTrue(tests.contains("401"))
    }

    @Test
    fun noSecretsOrServiceRoleKeysAnywhereInSchema() {
        val all = listOf(core, rls, erasure, tests,
            java.io.File(root, "functions/report-summary/index.ts").readText())
            .joinToString("\n")
        // A real secret looks like a JWT (eyJ...) or an sb_*_<random> key.
        assertFalse("no JWT-shaped secret", Regex("eyJ[A-Za-z0-9_-]{10,}").containsMatchIn(all))
        assertFalse("no sb_ secret key", Regex("sb_(secret|service_role)_[A-Za-z0-9]{8,}").containsMatchIn(all))
        // No assignment of a long literal to a key/secret/token variable.
        assertFalse("no hardcoded credential assignment",
            Regex("(?i)(key|secret|token)\\s*[:=]\\s*['\"][A-Za-z0-9+/_-]{20,}['\"]").containsMatchIn(all))
    }

    @Test
    fun reportSummaryFunctionVerifiesJwtAndUsesRlsClient() {
        val fn = java.io.File(root, "functions/report-summary/index.ts").readText()
        assertTrue(fn.contains("auth.getUser(jwt)"))
        assertTrue(fn.contains("status: 401"))
        // Data reads must go through the user-JWT client, not a service client.
        assertTrue(fn.contains("Authorization: `Bearer ${'$'}{jwt}`"))
        assertTrue(fn.contains("ka_scan_sessions"))
        assertTrue(fn.contains("ka_operation_requests"))
        assertTrue(fn.contains("ka_storage_snapshots"))
    }
}
