package com.knoux.ai.ui.v2

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.BrandSymbol
import com.knoux.ai.ui.components.BrandWordmark
import com.knoux.ai.ui.components.HeaderHalo
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.screens.LoginLaurel
import kotlin.math.cos
import kotlin.math.sin

private data class EcosystemService(val name: String, val icon: ImageVector, val accent: Color, val position: Offset)

private val ecosystemServices = listOf(
    EcosystemService("مراجعة\nالخصوصية", Icons.Rounded.Lock, V2Pink, Offset(.30f, .115f)),
    EcosystemService("اكتشاف\nالملفات المكررة", Icons.Rounded.ContentCopy, V2Blue, Offset(.70f, .115f)),
    EcosystemService("مركز\nالجهاز الذكي", Icons.Rounded.MonitorHeart, V2Cyan, Offset(.144f, .301f)),
    EcosystemService("توفير\nالبطارية", Icons.Rounded.BatteryChargingFull, V2Green, Offset(.856f, .301f)),
    EcosystemService("تنظيف بقايا\nالتطبيقات", Icons.Rounded.CleaningServices, V2Pink, Offset(.138f, .493f)),
    EcosystemService("تحليل\nالتخزين", Icons.Rounded.Storage, V2Cyan, Offset(.862f, .493f)),
    EcosystemService("التقارير\nوالرؤى", Icons.Rounded.BarChart, V2Cyan, Offset(.144f, .685f)),
    EcosystemService("إدارة\nالتطبيقات", Icons.Rounded.GridView, V2Pink, Offset(.856f, .685f)),
    EcosystemService("الفحص\nالعميق", Icons.Rounded.Radar, V2Violet, Offset(.339f, .869f)),
    EcosystemService("التحسين\nالذكي", Icons.Rounded.AutoAwesome, V2Blue, Offset(.661f, .869f)),
)

@Composable
internal fun OnboardingEcosystemPage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(12.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    HeaderHalo(Modifier.matchParentSize(), color = V2Pink)
                    BrandSymbol(size = 36.dp)
                }
                BrandWordmark(height = 24.dp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            LoginLaurel(mirror = false)
            Text(
                buildAnnotatedString {
                    append("منظومة ذكية ")
                    withStyle(SpanStyle(color = Color(0xFFF396F7))) { append("واحدة.") }
                },
                color = Color.White,
                fontFamily = V2Font,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            LoginLaurel(mirror = true)
        }
        Spacer(Modifier.height(5.dp))
        KText(
            "10 أقسام ذكية، تجربة واحدة مترابطة.",
            size = 13.sp,
            color = V2Soft,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
        EcosystemMap()
    }
}

@Composable
private fun EcosystemMap() {
    val phase = if (rememberMotionEnabled()) {
        rememberInfiniteTransition(label = "ecosystem links").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(5600, easing = LinearEasing)),
            label = "ecosystem signal",
        )
    } else null
    BoxWithConstraints(Modifier.fillMaxWidth().height(414.dp)) {
        val width = maxWidth
        val height = maxHeight
        val tileWidth = width * .264f
        val tileHeight = height * .167f
        val core = width * .314f
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width * .50f, size.height * .495f)
            drawCircle(
                Brush.radialGradient(listOf(Color(0x663D0FFF), Color.Transparent), center, size.width * .44f),
                size.width * .44f,
                center,
            )
            repeat(3) { ring ->
                val radius = size.width * (.178f + ring * .028f)
                repeat(66) { dot ->
                    val angle = dot * (Math.PI * 2 / 66)
                    drawCircle(
                        if (dot % 2 == 0) V2Cyan.copy(alpha = .48f) else V2Pink.copy(alpha = .37f),
                        .58.dp.toPx(),
                        center + Offset(cos(angle).toFloat() * radius, sin(angle).toFloat() * radius),
                    )
                }
            }
            ecosystemServices.forEachIndexed { index, service ->
                val position = service.position
                val end = Offset(
                    size.width * (position.x + if (position.x < .5f) .094f else -.094f),
                    size.height * position.y,
                )
                val control1 = Offset(center.x + (end.x - center.x) * .72f, center.y)
                val control2 = Offset(end.x - (end.x - center.x) * .22f, end.y)
                val path = Path().apply {
                    moveTo(center.x, center.y)
                    cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
                }
                val brush = Brush.linearGradient(
                    listOf(service.accent.copy(alpha = .25f), service.accent),
                    start = center, end = end,
                )
                drawPath(path, brush, style = Stroke(1.35.dp.toPx()))
                val time = ((phase?.value ?: .54f) + index / 10f) % 1f
                val inverse = 1f - time
                val dot = center * (inverse * inverse * inverse) +
                    control1 * (3f * inverse * inverse * time) +
                    control2 * (3f * inverse * time * time) + end * (time * time * time)
                drawCircle(service.accent.copy(alpha = .32f), 4.dp.toPx(), dot)
                drawCircle(Color.White, 1.35.dp.toPx(), dot)
            }
        }

        Box(
            Modifier.align(AbsoluteAlignment.TopLeft)
                .absoluteOffset(x = (width - core) / 2, y = height * .495f - core / 2)
                .size(core)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF4514A6), Color(0xFF101344))))
                .border(1.8.dp, Brush.sweepGradient(listOf(V2Pink, V2Cyan, V2Violet, V2Pink)), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            HeaderHalo(Modifier.matchParentSize(), color = V2Cyan)
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                BrandSymbol(size = core * .53f)
                KText(
                    "نواة الذكاء",
                    size = 11.sp,
                    weight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF5D2099), Color(0xFF241253))))
                        .border(1.dp, V2Pink.copy(alpha = .85f), RoundedCornerShape(100))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }

        ecosystemServices.forEach { service ->
            EcosystemTile(
                service,
                Modifier.align(AbsoluteAlignment.TopLeft)
                    .absoluteOffset(
                        x = width * service.position.x - tileWidth / 2,
                        y = height * service.position.y - tileHeight / 2,
                    )
                    .width(tileWidth)
                    .height(tileHeight),
            )
        }
    }
}

@Composable
private fun EcosystemTile(service: EcosystemService, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier.clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF261957), Color(0xFF100E32))))
            .drawWithCache {
                val bloom = Brush.radialGradient(
                    listOf(service.accent.copy(alpha = .48f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width,
                )
                onDrawBehind { drawRect(bloom) }
            }
            .border(1.2.dp, Brush.linearGradient(listOf(service.accent, Color(0xFF534592), service.accent)), shape)
            .padding(horizontal = 2.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(23.dp), contentAlignment = Alignment.Center) {
            HeaderHalo(Modifier.matchParentSize(), color = service.accent)
            Icon(service.icon, null, tint = service.accent, modifier = Modifier.size(19.dp))
        }
        KText(service.name, size = 9.4.sp, lineHeight = 12.sp, align = TextAlign.Center, weight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
internal fun EcosystemRails(modifier: Modifier) {
    Canvas(modifier) {
        val upper = Path().apply {
            moveTo(-size.width * .08f, size.height * .16f)
            cubicTo(size.width * .12f, size.height * .08f, size.width * .38f, size.height * .12f, size.width * .58f, -size.height * .04f)
        }
        val lower = Path().apply {
            moveTo(-size.width * .08f, size.height * .90f)
            cubicTo(size.width * .25f, size.height * .72f, size.width * .64f, size.height * .92f, size.width * 1.06f, size.height * .72f)
        }
        val light = Brush.horizontalGradient(listOf(V2Pink, V2Violet, V2Cyan))
        for (path in listOf(upper, lower)) {
            drawPath(path, light, style = Stroke(28.dp.toPx()), alpha = .11f)
            drawPath(path, light, style = Stroke(13.dp.toPx()), alpha = .15f)
            drawPath(path, light, style = Stroke(2.2.dp.toPx()), alpha = .72f)
        }
    }
}
