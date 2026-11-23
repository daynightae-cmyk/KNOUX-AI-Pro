-- ============================================================================
-- KNOUX AI Pro — Supabase schema migration 001 (core tables)
-- Project ref: tduofkizwkjtqcbklfah  (verified ACTIVE; public schema was empty
-- at 2026-11-23 — these migrations create the app schema from zero.)
--
-- Design rules:
--  * Every application table is user-owned: user_id references auth.users(id)
--    ON DELETE CASCADE, so account erasure removes all user data.
--  * Primary keys are client-generated UUIDs, so offline-first retries and
--    upserts are idempotent (PostgREST resolution=merge-duplicates).
--  * Timestamps are timestamptz; the Android client sends ISO-8601.
--  * NO service_role key is ever shipped to the app; all access is via RLS
--    (see 002_rls.sql). Apply order: 001 -> 002 -> 003.
--  * Idempotent: safe to re-run on an empty schema.
-- ============================================================================

create table if not exists public.ka_profiles (
    id                  uuid primary key references auth.users (id) on delete cascade,
    email               text not null,
    display_name        text,
    cloud_sync_enabled  boolean not null default false,  -- explicit opt-in only
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now()
);

create table if not exists public.ka_devices (
    id            uuid primary key default gen_random_uuid(),
    user_id       uuid not null references auth.users (id) on delete cascade,
    device_label  text,
    device_model  text,
    api_level     integer,
    app_version   text,
    registered_at timestamptz not null default now(),
    last_seen_at  timestamptz not null default now()
);
create index if not exists ka_devices_user_idx on public.ka_devices (user_id);

-- Mirrors Room scan_sessions 1:1. Provenance columns (device_model/api_level)
-- stay on the session row as measured on the device at scan time.
create table if not exists public.ka_scan_sessions (
    id                 uuid primary key,
    user_id            uuid not null references auth.users (id) on delete cascade,
    device_id          uuid references public.ka_devices (id) on delete cascade,
    service_id         text not null,
    service_name       text,
    status             text not null,
    permission_granted boolean not null default false,
    started_at         timestamptz not null,
    ended_at           timestamptz,
    items_found        integer not null default 0,
    bytes_found        bigint not null default 0,
    bytes_reclaimable  bigint not null default 0,
    device_model       text,
    api_level          integer
);
create index if not exists ka_scan_sessions_user_idx on public.ka_scan_sessions (user_id, started_at desc);

-- Mirrors Room operation_requests. Consent is enforced at the database:
-- a row may only be INSERTED with consent_given = true and consent_at set.
-- (The client journals PENDING intent locally in Room; only consented
-- operations are ever synced to the cloud.)
create table if not exists public.ka_operation_requests (
    id                       uuid primary key,
    user_id                  uuid not null references auth.users (id) on delete cascade,
    device_id                uuid references public.ka_devices (id) on delete cascade,
    scan_session_id          uuid references public.ka_scan_sessions (id) on delete set null,
    service_id               text not null,
    requested_action         text not null,
    selection_count          integer not null default 0,
    consent_given            boolean not null default false,
    consent_at               timestamptz,
    predicted_eligible_bytes bigint,
    status                   text not null default 'PENDING',
    created_at               timestamptz not null default now(),
    constraint ka_op_consent_timestamp
        check (consent_given = false or consent_at is not null)
);
create index if not exists ka_operation_requests_user_idx
    on public.ka_operation_requests (user_id, created_at desc);

-- Mirrors Room operation_item_outcomes (measured receipts, never estimates).
create table if not exists public.ka_operation_item_outcomes (
    operation_id     uuid not null references public.ka_operation_requests (id) on delete cascade,
    item_id          text not null,
    result           text not null,
    post_query_exists boolean not null default false,
    measured_bytes   bigint,
    provider_error   text,
    completed_at     timestamptz,
    primary key (operation_id, item_id)
);

-- Mirrors Room storage_snapshots.
create table if not exists public.ka_storage_snapshots (
    id          uuid primary key,
    user_id     uuid not null references auth.users (id) on delete cascade,
    device_id   uuid references public.ka_devices (id) on delete cascade,
    total_bytes bigint not null default 0,
    used_bytes  bigint not null default 0,
    free_bytes  bigint not null default 0,
    media_bytes bigint not null default 0,
    cache_bytes bigint not null default 0,
    other_bytes bigint not null default 0,
    recorded_at timestamptz not null default now(),
    scope       text not null default 'device'
);
create index if not exists ka_storage_snapshots_user_idx
    on public.ka_storage_snapshots (user_id, recorded_at desc);

-- Mirrors Room battery_samples.
create table if not exists public.ka_battery_samples (
    id            uuid primary key,
    user_id       uuid not null references auth.users (id) on delete cascade,
    device_id     uuid references public.ka_devices (id) on delete cascade,
    level_percent integer,
    status        text,
    temperature_c real,
    voltage_mv    integer,
    technology    text,
    charging      boolean not null default false,
    health        text,
    recorded_at   timestamptz not null default now()
);
create index if not exists ka_battery_samples_user_idx
    on public.ka_battery_samples (user_id, recorded_at desc);

-- Keep ka_profiles.updated_at honest on UPDATE.
create or replace function public.ka_touch_updated_at()
returns trigger language plpgsql as $$
begin
    new.updated_at := now();
    return new;
end $$;

drop trigger if exists ka_profiles_touch on public.ka_profiles;
create trigger ka_profiles_touch
    before update on public.ka_profiles
    for each row execute function public.ka_touch_updated_at();
