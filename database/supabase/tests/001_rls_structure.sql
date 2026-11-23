-- ============================================================================
-- KNOUX AI Pro — RLS structure + behavioral tests (staging only)
-- Run PART A with a PRIVILEGED staging SQL user (service_role / postgres).
-- PART B must be executed through authenticated client JWTs (publishable key
-- + two real test users, user A and user B) — never with the service key.
-- Expected: every PART A check returns ok = true; every PART B foreign-access
-- attempt returns 0 rows or an error; the no-consent insert is rejected.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- PART A — structural checks (privileged). Each SELECT must return ok=true.
-- ----------------------------------------------------------------------------

-- A1. RLS is enabled on every ka_* application table.
select t.tablename,
       t.rowsecurity as rls_enabled,
       (t.rowsecurity) as ok
from pg_tables t
where t.schemaname = 'public'
  and t.tablename like 'ka\_%'
order by t.tablename;
-- Expect: rls_enabled = true for ka_profiles, ka_devices, ka_scan_sessions,
-- ka_operation_requests, ka_operation_item_outcomes, ka_storage_snapshots,
-- ka_battery_samples, ka_erasure_audit.

-- A2. Every application table has owner policies for select/insert/update/delete
--     scoped to auth.uid() = user_id (insert on ka_operation_requests is the
--     consent-only policy instead — verified in A4).
select p.tablename, p.cmd, count(*) as policies,
       bool_and(p.qual like '%auth.uid()%') as scoped_to_auth_uid
from pg_policies p
where p.schemaname = 'public' and p.tablename like 'ka\_%'
group by p.tablename, p.cmd
order by p.tablename, p.cmd;
-- Expect: for each table and cmd in {SELECT, INSERT, UPDATE, DELETE} exactly
-- one policy containing auth.uid(), except ka_operation_requests INSERT which
-- must appear only as the consent policy (A4).

-- A3. All user-owned tables cascade from auth.users on delete (account erasure).
select con.conrelid::regclass as table_name,
       (con.confdelaction = 'c') as cascades_from_auth_users
from pg_constraint con
where con.contype = 'f'
  and con.confrelid = 'auth.users'::regclass
  and con.conrelid::regclass::text like 'public.ka\_%'
order by 1;
-- Expect: every user-owned ka_* table (ka_profiles directly, the others via
-- user_id; ka_operation_item_outcomes cascades via its parent operation)
-- shows cascades_from_auth_users = true.

-- A4. The consent enforcement: ka_operation_requests has NO plain owner insert;
--     inserts exist only through ka_op_consented_insert which requires
--     consent_given = true AND consent_at IS NOT NULL, plus a table-level CHECK.
select policyname, cmd, qual, with_check
from pg_policies
where schemaname = 'public' and tablename = 'ka_operation_requests' and cmd = 'INSERT';
-- Expect exactly one row: ka_op_consented_insert with both consent conditions.
select conname, pg_get_constraintdef(oid)
from pg_constraint
where conrelid = 'public.ka_operation_requests'::regclass and contype = 'c';
-- Expect ka_op_consent_timestamp: consent_given = false OR consent_at IS NOT NULL.

-- A5. ka_erasure_audit has RLS enabled and ZERO policies (deny-by-default).
select t.rowsecurity as rls_enabled,
       coalesce(count(p.policyname), 0) as policy_count
from pg_tables t
left join pg_policies p
  on p.schemaname = t.schemaname and p.tablename = t.tablename
where t.schemaname = 'public' and t.tablename = 'ka_erasure_audit'
group by t.rowsecurity;
-- Expect: rls_enabled = true, policy_count = 0.

-- A6. No policy anywhere grants access to unauthenticated roles on ka_* tables.
select p.tablename, p.policyname, p.roles
from pg_policies p
where p.schemaname = 'public' and p.tablename like 'ka\_%'
  and 'anon' = any(p.roles);
-- Expect: zero rows.

-- ----------------------------------------------------------------------------
-- PART B — two-real-user behavioral test (run via REST with user JWTs).
-- Setup: create user A and user B through Supabase Auth (email sign-in).
-- Let $A and $B be their access tokens; base URL is the project REST root with
-- apikey = publishable key and Authorization: Bearer $A / $B.
--
-- B1. User A inserts own profile and one device:
--     POST /ka_profiles  (id = A's uid, email, display_name)        -> 201
--     POST /ka_devices   (user_id = A's uid, device_label='pixel-a') -> 201
-- B2. User B tries to read A's data — expect 0 rows:
--     GET /ka_profiles?id=eq.<A-uid>          with $B -> []
--     GET /ka_devices?user_id=eq.<A-uid>      with $B -> []
--     GET /ka_scan_sessions?user_id=eq.<A-uid> with $B -> []
-- B3. User B tries to insert into A's space — expect 403/0 rows:
--     POST /ka_scan_sessions (user_id = A's uid) with $B -> denied (WITH CHECK)
--     POST /ka_devices (user_id = A's uid)       with $B -> denied
-- B4. Cross-owner references are rejected:
--     A posts /ka_scan_sessions with device_id = B's device id -> denied
--     (FK + RLS: the referenced device is not A's; policy WITH CHECK fails).
-- B5. No-consent operation insert is rejected even for the owner:
--     A posts /ka_operation_requests (user_id = A, consent_given = false)
--       -> denied by ka_op_consented_insert and by the CHECK constraint.
--     A posts with consent_given = true but consent_at = null -> denied.
--     A posts with consent_given = true AND consent_at set      -> 201.
-- B6. User B tries update/delete on A's rows — expect 0 rows affected:
--     PATCH /ka_profiles?id=eq.<A-uid>  with $B -> {"count":0} / 200 empty
--     DELETE /ka_scan_sessions?user_id=eq.<A-uid> with $B -> 0 rows
-- B7. Erasure: A calls RPC ka_erase_my_account with $A -> returns audit counts;
--     afterwards GET /ka_profiles?id=eq.<A-uid> with $A -> [] (profile gone,
--     cascades removed the rest); B still cannot read A's audit row:
--     GET /ka_erasure_audit with $B or $A -> [] (deny-by-default).
-- B8. Expired/revoked session: revoke A's session, retry B1 with the old
--     token -> 401. (Session expiry enforced by GoTrue; RLS sees auth.uid()=null
--     and every policy denies.)
-- ============================================================================
