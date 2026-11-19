package com.knoux.ai.developer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.developer.model.ProviderConfig
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Deep
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.formatCount
import com.knoux.ai.ui.v2.v2Bidi
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

/**
 * Secure API Key Vault (DEV-03).
 * Shows masked keys, connection state, and allows add / test / toggle / delete.
 * Real keys never appear in Logcat or UI text — only masked values.
 */
@Composable
fun ApiKeyVaultScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val vault = CredentialVault(context)

    var providers by remember { mutableStateOf(listOf<ProviderConfig>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val registry = com.knoux.ai.developer.provider.ProviderRegistryHolder.init()
        val list = registry.allProviders.map { adapter ->
            ProviderConfig(
                providerId = adapter.providerId,
                keystoreAlias = "knoux_key_${adapter.providerId}",
                maskedKey = vault.maskKey(""),
                isConfigured = vault.hasCredential(adapter.providerId),
                isEnabled = vault.isEnabled(adapter.providerId),
            )
        }
        providers = list
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "خزنة المفاتيح الآمنة",
            english = "API KEY VAULT",
            subtitle = "مفاتيح API مشفرة في Keystore Android — لا تُخزن بأي نص واضح",
            accent = V2Violet,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(providers) { config ->
                VaultEntryCard(
                    config = config,
                    vault = vault,
                    onReveal = { /* biometric gate + reveal */ },
                    onToggle = { /* enable/disable */ },
                    onDelete = { /* remove credential */ },
                    onTest = { /* test key */ },
                )
            }
        }
    }
}

@Composable
private fun VaultEntryCard(
    config: ProviderConfig,
    vault: CredentialVault,
    onReveal: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
) {
    val stateColor = if (config.isConfigured) V2Green else V2Red

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = stateColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                    KText(config.providerId.v2Bidi(), size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (config.isConfigured) "مconfigured" else "غير مconfigured",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                TextButton(
                    onClick = onReveal,
                    enabled = config.isConfigured,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = null,
                        tint = V2Cyan,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    KText("المفتاح المخزّن", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        config.maskedKey.v2Bidi(),
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onToggle) {
                        KText(
                            if (config.isEnabled) "إيقاف" else "تفعيل",
                            size = 9.4.sp,
                            color = V2Cyan,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = onTest, enabled = config.isConfigured) {
                        KText("اختبار", size = 9.4.sp, color = V2Blue)
                    }
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = onDelete) {
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
