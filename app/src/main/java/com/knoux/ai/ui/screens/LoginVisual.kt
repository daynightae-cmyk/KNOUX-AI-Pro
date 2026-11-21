package com.knoux.ai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.BrandSymbol
import com.knoux.ai.ui.components.BrandWordmark
import com.knoux.ai.ui.components.HeaderHalo
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.v2.CrownGold

private val ProEditionGold = Color(0xFFFFCF68)
private val NeonViolet = Color(0xFFBD4BFF)
private val ElectricBlue = Color(0xFF268CFF)
private val GlassShape = RoundedCornerShape(30.dp)
private val ValleyRidges = listOf(
    listOf(
        Offset(0f, .59f), Offset(.07f, .49f), Offset(.15f, .64f), Offset(.22f, .56f),
        Offset(.38f, .78f), Offset(.61f, .81f), Offset(.76f, .60f), Offset(.82f, .68f),
        Offset(.93f, .50f), Offset(1f, .63f),
    ),
    listOf(
        Offset(0f, .76f), Offset(.09f, .62f), Offset(.15f, .72f), Offset(.25f, .75f),
        Offset(.35f, .84f), Offset(.65f, .84f), Offset(.80f, .73f), Offset(.91f, .66f), Offset(1f, .75f),
    ),
)

@Composable
internal fun LoginBrand() {
    Column(
        Modifier.fillMaxWidth().zIndex(1f).padding(top = 4.dp, bottom = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        // Reference: the gold crown floats above the header wordmark.
        CrownGold(size = 24.dp, color = ProEditionGold)
        // The official company seal opens the login brand, halo breathing.
        com.knoux.ai.ui.components.BrandSeal(size = 44.dp, haloColor = NeonViolet)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandWordmark(height = 22.dp)
                Spacer(Modifier.width(9.dp))
                Row(
                    Modifier.border(1.dp, ProEditionGold, RoundedCornerShape(50))
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    CrownGold(size = 16.dp, color = ProEditionGold)
                    Text("Pro", color = ProEditionGold, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
        Text(
            "مساعدك الذكي لإدارة الجهاز وتنظيفه",
            color = ProTextPrimary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xEC07051C), Color(0xEC07051C), Color.Transparent),
                    ),
                )
                .padding(horizontal = 18.dp, vertical = 1.dp),
        )
        Text(
            "Smart Management & AI Cleaning",
            color = ProTextSecondary,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun LoginEditionTitle() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoginLaurel(mirror = false)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("KNOUX AI", color = ProTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                Text("Pro", color = ProEditionGold, fontSize = 22.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
            }
            LoginLaurel(mirror = true)
        }
    }
}

/** Gold laurel branch flanking the edition title, as in the master login reference. */
@Composable
internal fun LoginLaurel(mirror: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = 17.dp, height = 34.dp)) {
        val w = size.width
        val h = size.height
        val dir = if (mirror) -1f else 1f
        val cx = if (mirror) w * .82f else w * .18f
        val stem = Path().apply {
            moveTo(cx, h * .96f)
            quadraticBezierTo(cx + dir * w * .55f, h * .55f, cx + dir * w * .18f, h * .06f)
        }
        drawPath(stem, ProEditionGold.copy(alpha = .9f), style = Stroke(1.3.dp.toPx()))
        repeat(6) { i ->
            val t = (i + .6f) / 6.6f
            val y = h * (.94f - t * .86f)
            val x = cx + dir * w * (.30f * kotlin.math.sin(t * Math.PI.toFloat() * .9f))
            val leaf = Size(w * .40f, h * .10f)
            drawOval(
                ProEditionGold.copy(alpha = .95f - i * .06f),
                topLeft = Offset(if (mirror) x - leaf.width else x, y - leaf.height / 2),
                size = leaf,
            )
        }
    }
}

/** The gold "أو" separator between passkey and email entry, per the reference. */
@Composable
internal fun LoginOrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, ProEditionGold.copy(alpha = .8f)))))
        Text("أو", color = ProEditionGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(ProEditionGold.copy(alpha = .8f), Color.Transparent))))
    }
}

/** Four corner brackets framing the fingerprint orb, as in the reference. */
@Composable
internal fun FingerprintBrackets(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val len = size.minDimension * .20f
        val stroke = 2.dp.toPx()
        val gold = ProEditionGold.copy(alpha = .85f)
        val corners = listOf(
            Offset(0f, 0f) to listOf(Offset(len, 0f), Offset(0f, len)),
            Offset(size.width, 0f) to listOf(Offset(-len, 0f), Offset(0f, len)),
            Offset(0f, size.height) to listOf(Offset(len, 0f), Offset(0f, -len)),
            Offset(size.width, size.height) to listOf(Offset(-len, 0f), Offset(0f, -len)),
        )
        for ((origin, legs) in corners) {
            for (leg in legs) {
                drawLine(gold, origin, Offset(origin.x + leg.x, origin.y + leg.y), strokeWidth = stroke)
            }
        }
    }
}

@Composable
internal fun LoginCosmicScene(height: Dp, reveal: Float, overlapHeader: Boolean = false) {
    Box(Modifier.fillMaxWidth().height(height), contentAlignment = Alignment.Center) {
        Canvas(
            Modifier.fillMaxSize().clipToBounds()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    // Melt the scene's top/bottom edges into the app background so
                    // the cosmic hero reads as one continuous nebula, not a panel.
                    drawRect(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            .12f to Color.Black,
                            .88f to Color.Black,
                            1f to Color.Transparent,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                    drawRect(
                        Brush.horizontalGradient(
                            0f to Color.Transparent,
                            .06f to Color.Black,
                            .94f to Color.Black,
                            1f to Color.Transparent,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                },
        ) {
            val planetCenter = Offset(size.width * .5f, size.height * .72f)
            val planetRadius = size.width * .43f
            drawCircle(
                Brush.radialGradient(
                    .0f to Color(0xFF080B26),
                    .83f to Color(0xFF13143F),
                    .97f to Color(0xFF3730A2),
                    1f to ElectricBlue,
                    center = planetCenter, radius = planetRadius,
                ),
                radius = planetRadius,
                center = planetCenter,
            )
            drawCircle(
                Brush.radialGradient(listOf(Color(0x33532AFF), Color.Transparent), center = planetCenter, radius = size.width * .50f),
                radius = size.width * .50f,
                center = planetCenter,
            )
            repeat(38) { i ->
                val point = Offset(size.width * ((i * 37 + 7) % 101) / 101f, size.height * ((i * 23 + 11) % 89) / 101f)
                drawCircle(if (i % 5 == 0) ProArcticCyan.copy(alpha = .65f) else Color(0xFF9285F2).copy(alpha = .56f), (if (i % 5 == 0) 1f else .55f).dp.toPx(), point)
            }
            val haze = Offset(size.width * .5f, size.height * .78f)
            drawOval(
                Brush.radialGradient(
                    listOf(Color(0xAA7B388F), Color(0x442344B0), Color.Transparent),
                    center = haze, radius = size.width * .49f,
                ),
                topLeft = Offset(0f, size.height * .50f), size = Size(size.width, size.height * .48f),
            )
            for (ridge in ValleyRidges) {
                val outline = Path()
                ridge.forEachIndexed { index, point ->
                    val x = point.x * size.width
                    val y = point.y * size.height
                    if (index == 0) outline.moveTo(x, y) else outline.lineTo(x, y)
                }
                drawPath(
                    outline,
                    Brush.horizontalGradient(listOf(Color(0x885607BA), Color(0xFF1980DD), Color(0xA76321E9))),
                    style = Stroke(.8.dp.toPx()),
                )
                outline.lineTo(size.width, size.height * .875f)
                outline.lineTo(0f, size.height * .875f)
                outline.close()
                drawPath(
                    outline,
                    Brush.verticalGradient(
                        listOf(Color(0xC21A1458), Color(0xDF0A0C27), Color(0xFF09071D)),
                        startY = size.height * .52f, endY = size.height * .875f,
                    ),
                )
            }
            drawRect(
                Brush.verticalGradient(
                    listOf(Color(0xC11D1255), Color(0xFF09071D)),
                    startY = size.height * .875f, endY = size.height,
                ),
                topLeft = Offset(0f, size.height * .875f), size = Size(size.width, size.height * .125f),
            )
            val foot = Offset(size.width * .5f, size.height * .91f)
            repeat(5) { ring ->
                val width = size.width * (.51f + ring * .085f)
                val depth = size.height * (.07f + ring * .017f)
                drawOval(
                    brush = Brush.horizontalGradient(listOf(ElectricBlue, NeonViolet, ProArcticCyan)),
                    topLeft = Offset(foot.x - width / 2, foot.y - depth / 2),
                    size = Size(width, depth),
                    alpha = .8f - ring * .14f,
                    style = Stroke(1.4.dp.toPx()),
                )
            }
        }
        AuthStag(
            modifier = Modifier
                .padding(end = 21.dp, bottom = 8.dp)
                .fillMaxWidth(.96f)
                .height(height * if (overlapHeader) .91f else .83f)
                .graphicsLayer {
                    transformOrigin = TransformOrigin(.53f, if (overlapHeader) .87f else .50f)
                    scaleX = 1.17f
                    scaleY = 1.17f
                },
            alpha = reveal,
        )
    }
}

internal fun Modifier.loginGlass(): Modifier =
    clip(GlassShape)
        .background(
            Brush.verticalGradient(listOf(Color(0xFF291663), Color(0xFF0D0A29), Color(0xFF16144C))),
        )
        .drawWithCache {
            val topLeft = Offset(0f, 0f)
            val bottomRight = Offset(size.width, size.height)
            val topGlow = Brush.radialGradient(
                listOf(NeonViolet.copy(alpha = .29f), Color.Transparent),
                topLeft, size.width * .65f,
            )
            val bottomGlow = Brush.radialGradient(
                listOf(ElectricBlue.copy(alpha = .26f), Color.Transparent),
                bottomRight, size.width * .64f,
            )
            onDrawBehind {
                drawRect(topGlow)
                drawRect(bottomGlow)
            }
        }
        .border(
            1.6.dp,
            Brush.linearGradient(listOf(ElectricBlue, NeonViolet, Color(0xFF5226B5), ProArcticCyan)),
            GlassShape,
        )

/** One of the three trust pillars under the login panel, per reference (7). */
@Composable
internal fun LoginTrustPillar(
    icon: ImageVector,
    arabic: String,
    english: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
        Text(arabic, color = ProTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(english, color = ProTextSecondary, fontSize = 8.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
internal fun NeonLoginButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    bright: Boolean = false,
    gold: Boolean = false,
    green: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    val background = when {
        !enabled -> listOf(Color(0xFF1A1632), Color(0xFF0D1029))
        bright -> listOf(Color.White, Color.White)
        gold -> listOf(Color(0xFFF5B93E), Color(0xFFE8940A), Color(0xFFD97706))
        green -> listOf(Color(0xFF0BA572), Color(0xFF067A55), Color(0xFF065F46))
        else -> listOf(Color(0xFF351B77), Color(0xFF120D36), Color(0xFF241153))
    }
    val borderBrush = when {
        gold -> Brush.horizontalGradient(listOf(Color(0xFFFFE29A), Color(0xFFFFCF68), Color(0xFFFF9D2E)))
        green -> Brush.horizontalGradient(listOf(Color(0xFF7CFFCE), Color(0xFF3DFFB0), Color(0xFF10B981)))
        bright -> Brush.horizontalGradient(listOf(Color(0xFF747775), Color(0xFF747775)))
        else -> Brush.horizontalGradient(listOf(ElectricBlue, NeonViolet, ProArcticCyan))
    }
    // Reference: leading icon, centred label, trailing chevron — pinned LTR so
    // the icon never flips to the right in the Arabic RTL layout.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .clip(shape)
            .background(Brush.verticalGradient(background))
            .border(1.dp, borderBrush, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {
            bright -> Image(painterResource(R.drawable.knoux_google_g), null, Modifier.size(20.dp))
            gold -> Box(
                Modifier.size(26.dp).clip(RoundedCornerShape(50)).background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource(R.drawable.knoux_google_g), null, Modifier.size(17.dp))
            }
            green -> Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            else -> Icon(icon, null, tint = ProArcticCyan, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label,
                color = when {
                    !enabled -> ProTextSecondary
                    bright -> Color(0xFF1F1F1F)
                    gold -> Color(0xFF241300)
                    else -> ProTextPrimary
                },
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = if (gold || green) FontWeight.Bold else FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = when {
                        gold -> Color(0xFF3A2404).copy(alpha = .85f)
                        green -> Color.White.copy(alpha = .8f)
                        else -> ProTextSecondary
                    },
                    fontSize = 10.sp, lineHeight = 14.sp, textAlign = TextAlign.Center,
                )
            }
        }
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = when {
                !enabled -> ProTextSecondary
                gold -> Color(0xFF241300)
                green -> Color.White
                bright -> Color(0xFF1F1F1F)
                else -> ProTextPrimary.copy(alpha = .85f)
            },
            modifier = Modifier.size(20.dp),
        )
    }
    }
}
