package com.knoux.ai.developer.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.knoux.ai.developer.model.ProviderConfig
import com.knoux.ai.developer.provider.ProviderRegistry
import com.knoux.ai.developer.provider.ProviderRegistryHolder
import com.knoux.ai.developer.vault.BiometricGate
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import kotlinx.coroutines.launch

private data class VaultEntryUi(
    val providerId: String,
    val displayName: String,
    val maskedKey: String,
    val isConfigured: Boolean,
    val isEnabled: Boolean,
)

private fun findActivity(context: android.content.Context): FragmentActivity? {
    var current: android.content.Context? = context
    while (current is android.content.ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return current as? FragmentActivity
}

/**
 * Secure API Key Vault (DEV-03).
 *
 * Real lifecycle: add (encrypted with Android Keystore), reveal only after
 * biometric/device-credential authentication, enable/disable, delete, and a
 * live provider connection test. Plaintext is never written to preferences,
 * logs or process arguments; the masked value is the only persisted display.
 */
@Composable
fun ApiKeyVaultScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vault = remember { CredentialVault(context) }
    val registry: ProviderRegistry = remember {
        ProviderRegistryHolder.registerBuiltInAdapters(context)
        ProviderRegistryHolder.init()
    }

    var entries by remember { mutableStateOf(listOf<VaultEntryUi>()) }
    var message by remember { mutableStateOf<String?>(null) }
    var testing by remember { mutableStateOf(emptySet<String>()) }
    var showAdd by remember { mutableStateOf(false) }
    var selectedProviderId by remember { mutableStateOf<String?>(null) }
    var keyText by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }
    var revealed by remember { mutableStateOf<Pair<String, String>?>(null) }
    var deleteTarget by remember { mutableStateOf<VaultEntryUi?>(null) }

    fun reload() {
        entries = registry.allProviders
            .sortedBy { it.displayName.lowercase() }
            .map { adapter ->
                VaultEntryUi(
                    providerId = adapter.providerId,
                    displayName = adapter.displayName,
                    maskedKey = vault.getMaskedKey(adapter.providerId),
                    isConfigured = vault.hasCredential(adapter.providerId),
                    isEnabled = vault.isEnabled(adapter.providerId),
                )
            }
    }

    LaunchedEffect(Unit) { reload() }

    fun saveKey() {
        val providerId = selectedProviderId ?: run {
            dialogError = "اختر مزودًا أولًا."
            return
        }
        val adapter = registry.getAdapter(providerId) ?: run {
            dialogError = "المزوّد غير مسجل."
            return
        }
        val trimmed = keyText.trim()
        if (trimmed.isEmpty() && adapter.authScheme != "none") {
            dialogError = "المفتاح مطلوب لهذا المزود."
            return
        }
        scope.launch {
            try {
                vault.storeCredential(
                    ProviderConfig(
                        providerId = adapter.providerId,
                        keystoreAlias = "knoux_key_${adapter.providerId}",
                        maskedKey = vault.maskKey(trimmed),
                        isConfigured = true,
                        isEnabled = true,
                    ),
                    trimmed,
                )
                showAdd = false
                keyText = ""
                dialogError = null
                message = "تم حفظ مفتاح ${adapter.displayName} مشفرًا في Keystore."
                reload()
            } catch (failure: Exception) {
                dialogError = failure.message ?: "تعذر حفظ المفتاح"
            }
        }
    }

    fun reveal(entry: VaultEntryUi) {
        val activity = findActivity(context)
        if (activity == null) {
            message = "التحقق الحيوي يتطلب شاشة نشطة."
            return
        }
        BiometricGate.authenticate(activity) { authenticated ->
            if (!authenticated) {
                message = "أُلغي التحقق. لم يُعرض المفتاح."
                return@authenticate
            }
            scope.launch {
                val plaintext = vault.getCredential(entry.providerId)
                if (plaintext == null) {
                    message = "تعذر فك التشفير؛ قد يكون مفتاح Keystore قد أُبطل. أعد إدخال المفتاح."
                } else {
                    revealed = entry.providerId to plaintext
                }
            }
        }
    }

    fun test(entry: VaultEntryUi) {
        val adapter = registry.getAdapter(entry.providerId) ?: return
        if (testing.contains(entry.providerId)) return
        scope.launch {
            testing = testing + entry.providerId
            message = null
            try {
                val key = vault.getCredential(entry.providerId).orEmpty()
                val started = SystemClock.elapsedRealtime()
                val result = adapter.testConnection(key)
                val latency = SystemClock.elapsedRealtime() - started
                message = if (result.success) {
                    "✓ ${entry.displayName}: ${result.modelsDiscovered} نموذجًا · ${latency}ms"
                } else {
                    "✗ ${entry.displayName}: ${result.errorMessage ?: result.state.name} · ${latency}ms"
                }
            } catch (failure: Exception) {
                message = "✗ ${entry.displayName}: ${failure.message ?: "فشل الاتصال"}"
            } finally {
                testing = testing - entry.providerId
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { KText("إضافة مفتاح API", size = 14.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    var menuOpen by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { menuOpen = true }) {
                            KText(
                                registry.getAdapter(selectedProviderId ?: "")?.displayName ?: "اختر المزود",
                                size = 11.sp,
                                color = V2Cyan,
                            )
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            registry.allProviders.sortedBy { it.displayName }.forEach { adapter ->
                                DropdownMenuItem(
                                    text = { KText(adapter.displayName, size = 11.sp, color = Color.White) },
                                    onClick = {
                                        selectedProviderId = adapter.providerId
                                        menuOpen = false
                                        dialogError = null
                                    },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = keyText,
                        onValueChange = {
                            keyText = it
                            dialogError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { KText("المفتاح (يُشفَّر ولا يُسجَّل)", size = 9.sp, color = V2Soft) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.White),
                    )
                    dialogError?.let { error ->
                        Spacer(Modifier.height(6.dp))
                        KText(error, size = 9.4.sp, color = V2Red, maxLines = 3)
                    }
                    Spacer(Modifier.height(6.dp))
                    KText(
                        "Ollama المحلي لا يحتاج مفتاحًا. المفاتيح الأخرى تُخزَّن AES-256-GCM في Android Keystore.",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 3,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = ::saveKey) { KText("حفظ", size = 10.sp, color = V2Green) }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false }) { KText("إلغاء", size = 10.sp, color = V2Soft) }
            },
        )
    }

    revealed?.let { (providerId, plaintext) ->
        AlertDialog(
            onDismissRequest = { revealed = null },
            title = { KText("المفتاح — $providerId", size = 13.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    KText(plaintext, size = 11.sp, color = V2GoldText, maxLines = 6)
                    Spacer(Modifier.height(8.dp))
                    KText(
                        "لا تشارك هذه القيمة. ستُخفى عند إغلاق النافذة.",
                        size = 9.sp,
                        color = V2Amber,
                        maxLines = 2,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { revealed = null }) { KText("إغلاق", size = 10.sp, color = V2Cyan) }
            },
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { KText("حذف المفتاح", size = 14.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                KText(
                    "سيُحذف مفتاح ${target.displayName} المشفر نهائيًا من هذا الجهاز. لا يمكن التراجع.",
                    size = 10.sp,
                    color = V2Soft,
                    maxLines = 3,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget = null
                    scope.launch {
                        vault.removeCredential(target.providerId)
                        message = "تم حذف مفتاح ${target.displayName}."
                        reload()
                    }
                }) { KText("حذف", size = 10.sp, color = V2Red) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { KText("إلغاء", size = 10.sp, color = V2Soft) }
            },
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "خزنة المفاتيح الآمنة",
            english = "API KEY VAULT",
            subtitle = "AES-256-GCM في Android Keystore · عرض بتحقق حيوي · اختبار اتصال حقيقي",
            accent = V2Violet,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 11.dp,
                    radius = 14.dp,
                    glow = V2Violet,
                    borderAlpha = .30f,
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            KText(
                                "${entries.count { it.isConfigured }} من ${entries.size} مزودًا مهيأ",
                                size = 10.5.sp,
                                weight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { showAdd = true }) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = V2Cyan,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                KText("إضافة مفتاح", size = 9.4.sp, color = V2Cyan)
                            }
                        }
                        message?.let { text ->
                            KText(text, size = 9.2.sp, color = V2GoldText, maxLines = 4)
                        }
                    }
                }
            }

            items(entries, key = { it.providerId }) { entry ->
                VaultEntryCard(
                    entry = entry,
                    testing = testing.contains(entry.providerId),
                    onReveal = { reveal(entry) },
                    onToggle = {
                        scope.launch {
                            vault.setEnabled(entry.providerId, !entry.isEnabled)
                            reload()
                        }
                    },
                    onDelete = { deleteTarget = entry },
                    onTest = { test(entry) },
                )
            }
        }
    }
}

@Composable
private fun VaultEntryCard(
    entry: VaultEntryUi,
    testing: Boolean,
    onReveal: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
) {
    val stateColor = when {
        !entry.isConfigured -> V2Red
        entry.isEnabled -> V2Green
        else -> V2Amber
    }

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = stateColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(stateColor.copy(alpha = .16f))
                        .border(1.dp, stateColor.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = stateColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(entry.displayName, size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        when {
                            !entry.isConfigured -> "غير مهيأ"
                            entry.isEnabled -> "مهيأ ومفعّل"
                            else -> "مهيأ ومعطّل"
                        },
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                TextButton(onClick = onReveal, enabled = entry.isConfigured && !testing) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = null,
                        tint = V2Cyan,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    KText("المفتاح المخزّن", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        if (entry.isConfigured) entry.maskedKey else "—",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onToggle, enabled = entry.isConfigured && !testing) {
                        KText(if (entry.isEnabled) "إيقاف" else "تفعيل", size = 9.4.sp, color = V2Cyan)
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onTest, enabled = entry.isConfigured && !testing) {
                        KText(if (testing) "جارٍ…" else "اختبار", size = 9.4.sp, color = V2Blue)
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onDelete, enabled = !testing) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = null,
                            tint = V2Red,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}
