package com.knoux.ai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.annotation.DrawableRes
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
import com.knoux.ai.ui.components.KnouxTopBar
import com.knoux.ai.ui.components.NeonHero
import com.knoux.ai.ui.components.PreviewDisclosure
import com.knoux.ai.ui.components.ProBadge
import com.knoux.ai.ui.components.ReferenceHeroBand
import com.knoux.ai.ui.components.ScreenSubtitle
import com.knoux.ai.ui.components.SettingsIconButton
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.TopLeading
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

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
    @DrawableRes heroImage: Int = 0,
    onOpenSettings: (() -> Unit)? = null,
    bottomActions: @Composable RowScope.() -> Unit = {},
    body: LazyListScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (heroImage != 0) {
            ReferenceHeroBand(
                image = heroImage,
                height = 116.dp,
                contentDescription = "$title$accent hero artwork",
            )
        } else {
            NeonHero(hero, height = 132.dp)
        }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenTitle(
                title,
                accent = accent,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            ScreenSubtitle(
                subtitle,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(4.dp))
            PreviewDisclosure()
            Spacer(Modifier.height(6.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
        ) {
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
