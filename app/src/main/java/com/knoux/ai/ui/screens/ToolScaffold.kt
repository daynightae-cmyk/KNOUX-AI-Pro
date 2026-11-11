package com.knoux.ai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.IconCircle
import com.knoux.ai.ui.components.KnouxTopBar
import com.knoux.ai.ui.components.NeonHero
import com.knoux.ai.ui.components.ScreenSubtitle
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.TopLeading

/**
 * Shared layout for the nine inner tool screens: top bar, neon hero, italic
 * title/subtitle, scrollable body and a fixed action row above the tab bar.
 */
@Composable
fun ToolScreenScaffold(
    hero: HeroKind,
    title: String,
    accent: String,
    subtitle: String,
    rightIcon: ImageVector,
    onBack: () -> Unit,
    bottomActions: @Composable RowScope.() -> Unit = {},
    body: LazyListScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        KnouxTopBar(
            leading = TopLeading.Back,
            onLeadingClick = onBack,
            right = {
                IconCircle(rightIcon, null, { })
                IconCircle(Icons.Rounded.Settings, "Settings", { })
            },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { NeonHero(hero, height = 168.dp) }
            item { ScreenTitle(title, accent = accent) }
            item { ScreenSubtitle(subtitle) }
            item { Spacer(Modifier.height(14.dp)) }
            body()
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bottomActions()
        }
    }
}

/** Empty placeholder used by the secondary tab indexes (Clean / Tools). */
@Composable
fun ToolIndexScreen(
    title: String,
    subtitle: String,
    entries: List<Pair<String, String>>,
    onOpen: (String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        ScreenTitle(title, accent = "")
        ScreenSubtitle(subtitle)
        Spacer(Modifier.height(16.dp))
        entries.forEach { (name, route) ->
            Box(Modifier.padding(bottom = 10.dp)) {
                com.knoux.ai.ui.components.FeatureRow(
                    row = com.knoux.ai.ui.data.PreviewRow(
                        title = name,
                        subtitle = "Open the $name screen",
                        badge = "",
                        icon = com.knoux.ai.ui.data.Key.Chip,
                    ),
                    onClick = { onOpen(route) },
                )
            }
        }
    }
}
