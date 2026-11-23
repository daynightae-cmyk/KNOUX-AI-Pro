package com.knoux.ai

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P7 — cross-layer integration contracts, pinned by JVM tests:
 *  1. No secrets anywhere in app sources/gradle (service-role keys, JWT-shaped
 *     strings, provider key literals). The only allowed credential is the
 *     publishable (anon) key.
 *  2. Supabase endpoint in BuildConfig points at the manifest's project ref.
 *  3. Room stays at schema v2 with an exported schema, and the Slice-1 atomic
 *     repository overloads remain (backward compatibility).
 *  4. The five UI journal call-site swaps are still in place.
 */
class IntegrationContractJvmTest {

    private val appDir: java.io.File by lazy {
        val wd = java.io.File(System.getProperty("user.dir")).absoluteFile
        listOf(wd, wd.parentFile)
            .map { java.io.File(it, "app").absoluteFile }
            .first { it.isDirectory }
    }

    private val rootDir: java.io.File get() = appDir.parentFile.absoluteFile

    private fun mainSources(): List<java.io.File> =
        java.io.File(appDir, "src/main").walkTopDown()
            .filter { it.isFile && (it.extension == "kt" || it.extension == "kts" || it.extension == "xml" || it.extension == "properties") }
            .toList()

    @Test
    fun noSecretsInAppSourcesOrBuildScripts() {
        val secretPatterns = listOf(
            Regex("sb_secret_[A-Za-z0-9]+"),
            Regex("(?i)service[_-]?role[_-]?key\\s*[:=]\\s*['\"][^'\"]+"),
            Regex("eyJ[A-Za-z0-9_-]{20,}"), // JWT-shaped string
            Regex("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*['\"][A-Za-z0-9+/_.-]{24,}['\"]"),
        )
        val offenders = mutableListOf<String>()
        (mainSources() + listOf(
            java.io.File(rootDir, "app/build.gradle.kts"),
            java.io.File(rootDir, "build.gradle.kts"),
            java.io.File(rootDir, "gradle.properties"),
        ).filter { it.isFile }).forEach { file ->
            val text = file.readText()
            secretPatterns.forEach { pattern ->
                if (pattern.containsMatchIn(text)) {
                    offenders += "${file.relativeTo(rootDir)} matches ${pattern.pattern.take(24)}"
                }
            }
        }
        assertTrue("Secret-like strings found:\n${offenders.joinToString("\n")}", offenders.isEmpty())
    }

    @Test
    fun supabaseEndpointMatchesManifestProjectAndKeyIsPublishableOnly() {
        val gradle = java.io.File(rootDir, "app/build.gradle.kts").readText()
        assertTrue("BuildConfig must expose SUPABASE_URL", gradle.contains("SUPABASE_URL"))
        assertTrue("BuildConfig must expose the publishable key", gradle.contains("sb_publishable_") || gradle.contains("PUBLISHABLE_KEY"))
        assertFalse("BuildConfig must never reference a service-role key", gradle.contains("service_role", ignoreCase = true) || gradle.contains("sb_secret"))
        // The manifest records project tduofkizwkjtqcbklfah — the host must match.
        val manifest = java.io.File(rootDir, "CORE_INTEGRATION_MANIFEST.md").readText()
        assertTrue(manifest.contains("tduofkizwkjtqcbklfah"))
        val hostInGradle = Regex("https://([a-z0-9]+)\\.supabase\\.co").find(gradle)?.groupValues?.get(1)
        assertTrue("app/build.gradle.kts must point at the same project host", hostInGradle == "tduofkizwkjtqcbklfah")
    }

    @Test
    fun roomSchemaStaysV2WithExportedSchema() {
        val db = java.io.File(appDir, "src/main/java/com/knoux/ai/database/AppDatabase.kt").readText()
        assertTrue("schema version must remain 2", Regex("version\\s*=\\s*2\\b").containsMatchIn(db))
        val schemasDir = java.io.File(appDir, "schemas/com.knoux.ai.database.AppDatabase")
        assertTrue("exported schema for v2 must exist", java.io.File(schemasDir, "2.json").isFile)
    }

    @Test
    fun repositoryKeepsBackwardCompatibleAtomicOverloads() {
        val db = java.io.File(appDir, "src/main/java/com/knoux/ai/database/AppDatabase.kt").readText()
        // Old single-purpose signatures still exist alongside the atomic ones.
        assertTrue(db.contains("suspend fun completeScanSession("))
        assertTrue(Regex("completeScanSession\\([\\s\\S]*?items: List<ObservedItemEntity>").containsMatchIn(db))
        assertTrue(db.contains("suspend fun recordItemOutcomes("))
        assertTrue(Regex("recordItemOutcomes\\([\\s\\S]*?finalStatus").containsMatchIn(db))
    }

    @Test
    fun uiJournalCallSiteSwapsStillInPlace() {
        // recordItemOutcomes pipelines must carry finalStatus (atomic DAO call);
        // SectionServiceViewModel completes scans via the atomic items overload.
        val expectations = mapOf(
            "src/main/java/com/knoux/ai/ui/v2/StorageAnalyzerViewModel.kt" to "finalStatus =",
            "src/main/java/com/knoux/ai/ui/v2/DuplicateWorkspaceViewModel.kt" to "finalStatus =",
            "src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt" to "finalStatus =",
            "src/main/java/com/knoux/ai/ui/v2/MoreScreensV2.kt" to "finalStatus =",
            "src/main/java/com/knoux/ai/ui/v2/SectionServiceViewModel.kt" to "items = mediaItems + appItems",
        )
        expectations.forEach { (path, marker) ->
            val text = java.io.File(appDir, path).readText()
            assertTrue("$path must contain the atomic journal call ('$marker')", text.contains(marker))
        }
    }
}
