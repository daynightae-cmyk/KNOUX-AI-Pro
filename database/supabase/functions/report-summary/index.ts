// ============================================================================
// KNOUX AI Pro — optional Edge Function: report-summary
// Deploy: supabase functions deploy report-summary
//
// Rules (per README_DEPLOYMENT.md):
//  * Platform JWT verification: the caller's Bearer token is verified with
//    supabaseAdmin.auth.getUser(jwt); unauthenticated calls get 401.
//  * All data access goes through a client created WITH THE USER'S JWT, so
//    RLS applies and the function can only ever read the caller's own rows.
//  * Aggregate numbers only. Never route raw files or file contents through
//    the cloud.
//  * No service-role key is used anywhere in this function: the caller JWT is
//    verified with the anon key via auth.getUser(jwt), and all data reads use
//    a client bound to the user's JWT.
// ============================================================================

import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.45.4";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") ?? "";
const SUPABASE_ANON_KEY = Deno.env.get("SUPABASE_ANON_KEY") ?? "";

interface ReportSummary {
  user_id: string;
  generated_at: string;
  scans: {
    total: number;
    completed: number;
    failed: number;
    items_found: number;
    bytes_found: number;
    bytes_reclaimable: number;
  };
  operations: {
    total: number;
    pending: number;
    completed: number;
    failed: number;
  };
  storage: {
    latest_snapshot_recorded_at: string | null;
    total_bytes: number;
    used_bytes: number;
    free_bytes: number;
  };
}

serve(async (req: Request) => {
  try {
    if (req.method !== "POST" && req.method !== "GET") {
      return new Response(JSON.stringify({ error: "method_not_allowed" }), {
        status: 405,
        headers: { "content-type": "application/json" },
      });
    }

    const authHeader = req.headers.get("Authorization") ?? "";
    const jwt = authHeader.replace(/^Bearer\s+/i, "");
    if (!jwt) {
      return new Response(JSON.stringify({ error: "missing bearer token" }), {
        status: 401,
        headers: { "content-type": "application/json" },
      });
    }

    // Verify the JWT against the platform (GoTrue). Invalid/expired -> 401.
    const admin = createClient(SUPABASE_URL, SUPABASE_ANON_KEY, {
      auth: { persistSession: false, autoRefreshToken: false },
    });
    const { data: userData, error: userError } = await admin.auth.getUser(jwt);
    if (userError || !userData?.user) {
      return new Response(JSON.stringify({ error: "invalid_or_expired_token" }), {
        status: 401,
        headers: { "content-type": "application/json" },
      });
    }
    const userId = userData.user.id;

    // RLS client bound to the USER's JWT: every query below is subject to the
    // owner policies in 002_rls.sql, so cross-user access is impossible.
    const rls = createClient(SUPABASE_URL, SUPABASE_ANON_KEY, {
      global: { headers: { Authorization: `Bearer ${jwt}` } },
      auth: { persistSession: false, autoRefreshToken: false },
    });

    const { data: sessions, error: sErr } = await rls
      .from("ka_scan_sessions")
      .select("status, items_found, bytes_found, bytes_reclaimable");
    if (sErr) throw sErr;

    const { data: ops, error: oErr } = await rls
      .from("ka_operation_requests")
      .select("status");
    if (oErr) throw oErr;

    const { data: snap, error: snapErr } = await rls
      .from("ka_storage_snapshots")
      .select("recorded_at, total_bytes, used_bytes, free_bytes")
      .order("recorded_at", { ascending: false })
      .limit(1)
      .maybeSingle();
    if (snapErr) throw snapErr;

    const summary: ReportSummary = {
      user_id: userId,
      generated_at: new Date().toISOString(),
      scans: {
        total: sessions.length,
        completed: sessions.filter((s) => s.status === "COMPLETED").length,
        failed: sessions.filter((s) => s.status === "FAILED").length,
        items_found: sessions.reduce((n, s) => n + (s.items_found ?? 0), 0),
        bytes_found: sessions.reduce((n, s) => n + (s.bytes_found ?? 0), 0),
        bytes_reclaimable: sessions.reduce((n, s) => n + (s.bytes_reclaimable ?? 0), 0),
      },
      operations: {
        total: ops.length,
        pending: ops.filter((o) => o.status === "PENDING").length,
        completed: ops.filter((o) => o.status === "COMPLETED").length,
        failed: ops.filter((o) => o.status === "FAILED").length,
      },
      storage: {
        latest_snapshot_recorded_at: snap?.recorded_at ?? null,
        total_bytes: snap?.total_bytes ?? 0,
        used_bytes: snap?.used_bytes ?? 0,
        free_bytes: snap?.free_bytes ?? 0,
      },
    };

    return new Response(JSON.stringify(summary), {
      status: 200,
      headers: { "content-type": "application/json" },
    });
  } catch (e) {
    // Never echo internals; log server-side only.
    console.error("report-summary failed:", e instanceof Error ? e.message : e);
    return new Response(JSON.stringify({ error: "internal_error" }), {
      status: 500,
      headers: { "content-type": "application/json" },
    });
  }
});
