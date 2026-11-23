-- ============================================================================
-- KNOUX AI Pro — Supabase migration 002 (Row Level Security)
-- Apply AFTER 001_core.sql, on the real project, owner-approved only.
--
-- Rules:
--  * RLS is ENABLED on every ka_* table; there is no public access path.
--  * Every policy requires auth.uid() = user_id for SELECT/INSERT/UPDATE/DELETE.
--  * ka_operation_requests INSERT additionally requires a real consent
--    (consent_given = true) so a sync without user consent is impossible.
--  * user_id can never be spoofed: WITH CHECK forces auth.uid() = user_id.
--  * The service_role key bypasses RLS by design — it is NEVER shipped in the
--    APK and must only be used server-side (edge functions / migrations).
-- ============================================================================

alter table public.ka_profiles                 enable row level security;
alter table public.ka_devices                  enable row level security;
alter table public.ka_scan_sessions            enable row level security;
alter table public.ka_operation_requests       enable row level security;
alter table public.ka_operation_item_outcomes  enable row level security;
alter table public.ka_storage_snapshots        enable row level security;
alter table public.ka_battery_samples          enable row level security;

-- Helper: drop-then-create keeps the migration re-runnable.
do $$
declare t text;
begin
    foreach t in array array[
        'ka_profiles','ka_devices','ka_scan_sessions','ka_operation_requests',
        'ka_operation_item_outcomes','ka_storage_snapshots','ka_battery_samples'
    ] loop
        execute format('drop policy if exists ka_owner_select on public.%I', t);
        execute format('drop policy if exists ka_owner_insert on public.%I', t);
        execute format('drop policy if exists ka_owner_update on public.%I', t);
        execute format('drop policy if exists ka_owner_delete on public.%I', t);
    end loop;
    drop policy if exists ka_op_consented_insert on public.ka_operation_requests;
end $$;

-- Owner-only CRUD on every application table.
create policy ka_owner_select on public.ka_profiles
    for select using (auth.uid() = user_id);
create policy ka_owner_insert on public.ka_profiles
    for insert with check (auth.uid() = user_id);
create policy ka_owner_update on public.ka_profiles
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_profiles
    for delete using (auth.uid() = user_id);

create policy ka_owner_select on public.ka_devices
    for select using (auth.uid() = user_id);
create policy ka_owner_insert on public.ka_devices
    for insert with check (auth.uid() = user_id);
create policy ka_owner_update on public.ka_devices
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_devices
    for delete using (auth.uid() = user_id);

create policy ka_owner_select on public.ka_scan_sessions
    for select using (auth.uid() = user_id);
create policy ka_owner_insert on public.ka_scan_sessions
    for insert with check (auth.uid() = user_id);
create policy ka_owner_update on public.ka_scan_sessions
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_scan_sessions
    for delete using (auth.uid() = user_id);

-- Consented operations only: the plain owner insert is deliberately NOT
-- created; inserts must satisfy the consent policy below.
create policy ka_op_consented_insert on public.ka_operation_requests
    for insert with check (
        auth.uid() = user_id
        and consent_given = true
        and consent_at is not null
    );
create policy ka_owner_select on public.ka_operation_requests
    for select using (auth.uid() = user_id);
create policy ka_owner_update on public.ka_operation_requests
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_operation_requests
    for delete using (auth.uid() = user_id);

create policy ka_owner_select on public.ka_operation_item_outcomes
    for select using (
        exists (select 1 from public.ka_operation_requests r
                where r.id = operation_id and r.user_id = auth.uid())
    );
create policy ka_owner_insert on public.ka_operation_item_outcomes
    for insert with check (
        exists (select 1 from public.ka_operation_requests r
                where r.id = operation_id and r.user_id = auth.uid())
    );
create policy ka_owner_update on public.ka_operation_item_outcomes
    for update using (
        exists (select 1 from public.ka_operation_requests r
                where r.id = operation_id and r.user_id = auth.uid())
    ) with check (
        exists (select 1 from public.ka_operation_requests r
                where r.id = operation_id and r.user_id = auth.uid())
    );
create policy ka_owner_delete on public.ka_operation_item_outcomes
    for delete using (
        exists (select 1 from public.ka_operation_requests r
                where r.id = operation_id and r.user_id = auth.uid())
    );

create policy ka_owner_select on public.ka_storage_snapshots
    for select using (auth.uid() = user_id);
create policy ka_owner_insert on public.ka_storage_snapshots
    for insert with check (auth.uid() = user_id);
create policy ka_owner_update on public.ka_storage_snapshots
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_storage_snapshots
    for delete using (auth.uid() = user_id);

create policy ka_owner_select on public.ka_battery_samples
    for select using (auth.uid() = user_id);
create policy ka_owner_insert on public.ka_battery_samples
    for insert with check (auth.uid() = user_id);
create policy ka_owner_update on public.ka_battery_samples
    for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy ka_owner_delete on public.ka_battery_samples
    for delete using (auth.uid() = user_id);
