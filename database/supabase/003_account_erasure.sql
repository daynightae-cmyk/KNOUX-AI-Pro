-- ============================================================================
-- KNOUX AI Pro — Supabase migration 003 (account erasure)
-- Apply AFTER 002_rls.sql, owner-approved, on staging first.
--
-- Design rules:
--  * Erasure is initiated by the authenticated user only (auth.uid()).
--  * All ka_* rows cascade from auth.users(id) ON DELETE CASCADE; deleting
--    the caller's ka_profiles row therefore removes devices, scan sessions,
--    operation requests, item outcomes, storage snapshots and battery samples
--    through the FK cascades. The explicit deletes below are belt-and-braces
--    and make the erasure auditable (returned row counts = audit receipt).
--  * Deleting the auth.users row itself requires the service_role key and is
--    intentionally NOT done from the app or from an anon-key RPC. The owner
--    completes it server-side (Supabase Admin API / dashboard / edge function
--    running with the service role). This function only erases app data.
--  * The function is SECURITY INVOKER: RLS still applies, so a caller can only
--    ever erase their OWN data (policies require auth.uid() = user_id).
-- ============================================================================

create or replace function public.ka_erase_my_account()
returns table (
    erased_devices bigint,
    erased_scan_sessions bigint,
    erased_operation_requests bigint,
    erased_item_outcomes bigint,
    erased_storage_snapshots bigint,
    erased_battery_samples bigint,
    erased_profile bigint
)
language plpgsql
security invoker
set search_path = public
as $$
declare
    v_user uuid := auth.uid();
    v_devices bigint := 0;
    v_sessions bigint := 0;
    v_ops bigint := 0;
    v_outcomes bigint := 0;
    v_snaps bigint := 0;
    v_batt bigint := 0;
    v_profile bigint := 0;
begin
    if v_user is null then
        raise exception 'ka_erase_my_account requires an authenticated user'
            using errcode = '42501';
    end if;

    -- Children first (FK cascades would handle it, but explicit deletes give
    -- exact audit counts per table).
    delete from public.ka_operation_item_outcomes o
    where exists (
        select 1 from public.ka_operation_requests r
        where r.id = o.operation_id and r.user_id = v_user
    );
    get diagnostics v_outcomes = row_count;

    delete from public.ka_operation_requests where user_id = v_user;
    get diagnostics v_ops = row_count;

    delete from public.ka_scan_sessions where user_id = v_user;
    get diagnostics v_sessions = row_count;

    delete from public.ka_storage_snapshots where user_id = v_user;
    get diagnostics v_snaps = row_count;

    delete from public.ka_battery_samples where user_id = v_user;
    get diagnostics v_batt = row_count;

    delete from public.ka_devices where user_id = v_user;
    get diagnostics v_devices = row_count;

    delete from public.ka_profiles where id = v_user;
    get diagnostics v_profile = row_count;

    -- Audit receipt: who erased how much, and when. Kept even after erasure.
    create table if not exists public.ka_erasure_audit (
        id          uuid primary key default gen_random_uuid(),
        user_id     uuid not null,
        erased_at   timestamptz not null default now(),
        devices     bigint not null,
        sessions    bigint not null,
        operations  bigint not null,
        outcomes    bigint not null,
        snapshots   bigint not null,
        battery     bigint not null,
        profile     bigint not null
    );
    -- The audit table itself is locked down in 002-style fashion here:
    -- nobody (including the erased user) can read or modify it via anon key.
    alter table public.ka_erasure_audit enable row level security;

    insert into public.ka_erasure_audit
        (user_id, devices, sessions, operations, outcomes, snapshots, battery, profile)
    values
        (v_user, v_devices, v_sessions, v_ops, v_outcomes, v_snaps, v_batt, v_profile);

    return query select
        v_devices, v_sessions, v_ops, v_outcomes, v_snaps, v_batt, v_profile;
end;
$$;

-- The erased user must not be able to read the audit log or re-insert rows.
-- No policies are created on ka_erasure_audit for anon/authenticated roles:
-- with RLS enabled and no policy, all anon-key access is denied by default.
-- Server-side (service role) access remains possible for compliance audits.

revoke all on function public.ka_erase_my_account() from public;
grant execute on function public.ka_erase_my_account() to authenticated;

-- Retention note for the owner:
--   * PostgREST/Realtime logs and Supabase platform audit logs are retained by
--     the platform per its own retention policy and are outside app control.
--   * auth.users deletion (GDPR "right to erasure" of the account itself) is
--     performed server-side with the service_role key via
--     `supabase.auth.admin.deleteUser(userId)` — never from the Android app.
