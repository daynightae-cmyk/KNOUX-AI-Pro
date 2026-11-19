package com.knoux.ai.ui.v2

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

/** Histories are not necessarily ordered (Room and preference readers differ). */
internal fun oldestScan(history: List<ScanHistoryEntry>): ScanHistoryEntry? =
    history.minByOrNull { it.timestamp }

internal fun newestScan(history: List<ScanHistoryEntry>): ScanHistoryEntry? =
    history.maxByOrNull { it.timestamp }

internal fun formatSignedBytes(delta: Long): String = when {
    delta > 0 -> "+${formatBytes(delta)}"
    delta < 0 -> "−${formatBytes(if (delta == Long.MIN_VALUE) Long.MAX_VALUE else -delta)}"
    else -> formatBytes(0)
}

internal fun batteryDelta(before: Int?, after: Int?): Int? =
    if (before == null || after == null) null else after - before

internal fun formatBatteryDelta(before: Int?, after: Int?): String =
    batteryDelta(before, after)?.let { "${if (it > 0) "+" else ""}$it نقطة مئوية" }
        ?: "غير متاح: قراءة بطارية ناقصة"

/** Unknown post-query state must never verify an uninstall. */
internal fun uninstallStatus(accepted: Boolean, exists: Boolean?): String = when {
    !accepted -> "CANCELLED"
    exists == null -> "ERROR"
    exists -> "PARTIAL"
    else -> "VERIFIED"
}

internal data class PackagePresence(val exists: Boolean?, val error: String? = null)

internal fun queryPackagePresence(context: Context, packageName: String): PackagePresence = try {
    context.packageManager.getApplicationInfo(packageName, 0)
    PackagePresence(true)
} catch (_: PackageManager.NameNotFoundException) {
    PackagePresence(false)
} catch (error: Exception) {
    PackagePresence(null, error.message ?: error.javaClass.simpleName)
}

internal fun readPowerSaveMode(context: Context): Boolean? = try {
    (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isPowerSaveMode
} catch (_: Exception) {
    null
}

internal fun powerSaveLabel(mode: Boolean?): String = when (mode) {
    true -> "مفعّل"
    false -> "غير مفعّل"
    null -> "غير مقروء"
}

/** OS handoffs report an unavailable activity rather than silently doing nothing. */
internal fun startScopedActivity(context: Context, intent: Intent): String? = try {
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    null
} catch (error: Exception) {
    "تعذّر فتح شاشة النظام: ${error.message ?: error.javaClass.simpleName}"
}

internal fun showInstalledAppInfo(context: Context, packageName: String): String? =
    startScopedActivity(context, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.parse("package:$packageName")))

internal fun launchVisiblePackage(context: Context, packageName: String): String? = try {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent == null) "لا يوفّر هذا التطبيق نشاطاً قابلاً للفتح"
    else startScopedActivity(context, intent)
} catch (error: Exception) {
    "تعذّر فتح التطبيق: ${error.message ?: error.javaClass.simpleName}"
}

internal fun viewGrantedDocument(context: Context, uri: Uri): String? = try {
    val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
    startScopedActivity(context, Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
} catch (error: Exception) {
    "تعذّر فتح الملف: ${error.message ?: error.javaClass.simpleName}"
}

/* ------------------------------------------------------------------ *
 * Privacy notes — app-private storage, never uploaded. The Room event
 * records that a note was saved; the text stays in private preferences.
 * ------------------------------------------------------------------ */

private const val PRIVACY_NOTES_PREFS = "knoux_privacy_notes_v1"
private const val PRIVACY_NOTES_KEY = "notes"
private const val PRIVACY_NOTE_RECORD_SEPARATOR = "\u001F"
private const val PRIVACY_NOTE_FIELD_SEPARATOR = "\u001E"

internal fun savePrivacyNote(context: Context, text: String, savedAt: Long = System.currentTimeMillis()) {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return
    val prefs = context.getSharedPreferences(PRIVACY_NOTES_PREFS, Context.MODE_PRIVATE)
    val existing = prefs.getString(PRIVACY_NOTES_KEY, null).orEmpty()
    val record = savedAt.toString() + PRIVACY_NOTE_FIELD_SEPARATOR + trimmed
    val combined = if (existing.isEmpty()) record else existing + PRIVACY_NOTE_RECORD_SEPARATOR + record
    prefs.edit().putString(PRIVACY_NOTES_KEY, combined).apply()
}

internal fun recentPrivacyNotes(context: Context, limit: Int = 10): List<String> =
    context.getSharedPreferences(PRIVACY_NOTES_PREFS, Context.MODE_PRIVATE)
        .getString(PRIVACY_NOTES_KEY, null)
        .orEmpty()
        .split(PRIVACY_NOTE_RECORD_SEPARATOR)
        .mapNotNull { record ->
            record.split(PRIVACY_NOTE_FIELD_SEPARATOR, limit = 2)
                .getOrNull(1)
                ?.takeIf { it.isNotBlank() }
        }
        .takeLast(limit)
        .reversed()

internal fun privacyNoteCount(context: Context): Int =
    context.getSharedPreferences(PRIVACY_NOTES_PREFS, Context.MODE_PRIVATE)
        .getString(PRIVACY_NOTES_KEY, null)
        .orEmpty()
        .split(PRIVACY_NOTE_RECORD_SEPARATOR)
        .count { it.isNotBlank() }
