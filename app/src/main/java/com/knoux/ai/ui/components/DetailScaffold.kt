package com.knoux.ai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

/** Shared chrome for interior detail screens 11-20. */
@Composable
fun DetailScaffold(
    hero: HeroKind,
    title: String,
    accent: String,
    subtitle: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    body: LazyListScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        KnouxTopBar(
            leading = TopLeading.Back,
            onLeadingClick = onBack,
            right = { SettingsIconButton() },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { NeonHero(hero, height = 132.dp) }
            item { ScreenTitle(title, accent = accent) }
            item { ScreenSubtitle(subtitle) }
            item { Spacer(Modifier.height(10.dp)) }
            body()
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
        }
    }
}
