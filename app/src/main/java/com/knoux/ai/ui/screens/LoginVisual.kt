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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.drawWithCache
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
        Modifier.fillMaxWidth().zIndex(1f).padding(top = 9.dp, bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(Modifier.size(51.dp), contentAlignment = Alignment.Center) {
            HeaderHalo(Modifier.matchParentSize(), color = NeonViolet)
            BrandSymbol(size = 51.dp)
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandWordmark(height = 26.dp)
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
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xEC07051C), Color(0xEC07051C), Color.Transparent),
                    ),
                )
                .padding(horizontal = 18.dp, vertical = 2.dp),
        )
    }
}

@Composable
internal fun LoginEditionTitle() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("KNOUX AI", color = ProTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
            Text("Pro", color = ProEditionGold, fontSize = 22.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
internal fun LoginCosmicScene(height: Dp, reveal: Float, overlapHeader: Boolean = false) {
    Box(Modifier.fillMaxWidth().height(height), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().clipToBounds()) {
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
                Brush.radialGradient(listOf(Color(0x33532AFF), Color.Transparent), center = center, radius = size.width * .56f),
                radius = size.width * .56f,
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

@Composable
internal fun NeonLoginButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    bright: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    val background = when {
        !enabled -> listOf(Color(0xFF1A1632), Color(0xFF0D1029))
        bright -> listOf(Color.White, Color.White)
        else -> listOf(Color(0xFF351B77), Color(0xFF120D36), Color(0xFF241153))
    }
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 49.dp)
            .clip(shape)
            .background(Brush.verticalGradient(background))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    if (bright) listOf(Color(0xFF747775), Color(0xFF747775))
                    else listOf(ElectricBlue, NeonViolet, ProArcticCyan),
                ),
                shape,
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (bright) {
            Image(painterResource(R.drawable.knoux_google_g), null, Modifier.size(20.dp))
        } else {
            Icon(icon, null, tint = ProArcticCyan, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label,
                color = when {
                    !enabled -> ProTextSecondary
                    bright -> Color(0xFF1F1F1F)
                    else -> ProTextPrimary
                },
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Text(subtitle, color = ProTextSecondary, fontSize = 10.sp, lineHeight = 14.sp, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.width(20.dp))
    }
}
