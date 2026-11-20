package com.knoux.ai.developer.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.developer.model.AgentTool
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2TopBar
import com.knoux.ai.ui.v2.V2TopMode
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet

private data class ToolStatus(
    val available: Boolean,
    val label: String,
    val detail: String,
)

private fun agentToolColor(tool: AgentTool): Color = when (tool) {
    AgentTool.ReadFile, AgentTool.ListDirectory, AgentTool.SearchFiles -> V2Cyan
    AgentTool.CreateFile, AgentTool.EditFile, AgentTool.ApplyPatch -> V2Green
    AgentTool.CompareDiff, AgentTool.GitStatus, AgentTool.GitDiff, AgentTool.GitLog -> V2Violet
    AgentTool.RunTests, AgentTool.TerminalExecution, AgentTool.BuildMonitoring -> V2Amber
    AgentTool.FetchDocumentation, AgentTool.ProjectDiagnostics -> V2Pink
}

private fun agentToolText(tool: AgentTool): String = when (tool) {
    AgentTool.ReadFile -> "قراءة ملف"
    AgentTool.ListDirectory -> "عرض دليل"
    AgentTool.SearchFiles -> "البحث في الملفات"
    AgentTool.CreateFile -> "إنشاء ملف"
    AgentTool.EditFile -> "تعديل ملف"
    AgentTool.ApplyPatch -> "تطبيق تصحيح"
    AgentTool.CompareDiff -> "مقارنة الفروقات"
    AgentTool.GitStatus -> "حالة Git"
    AgentTool.GitDiff -> "فروقات Git"
    AgentTool.GitLog -> "سجل Git"
    AgentTool.RunTests -> "تشغيل الاختبارات"
    AgentTool.TerminalExecution -> "تنفيذ محطة"
    AgentTool.FetchDocumentation -> "جلب التوثيق"
    AgentTool.ProjectDiagnostics -> "تشخيص المشروع"
    AgentTool.BuildMonitoring -> "مراقبة البناء"
}

/**
 * Exact capabilities, not marketing text. Android apps cannot fork arbitrary
 * shell/git/test processes from their sandbox; those entries stay visibly
 * unavailable rather than showing a button that does nothing.
 */
private fun toolStatus(tool: AgentTool): ToolStatus = when (tool) {
    AgentTool.ReadFile, AgentTool.ListDirectory, AgentTool.SearchFiles,
    AgentTool.CreateFile, AgentTool.EditFile -> ToolStatus(
        available = true,
        label = "متاح عبر SAF",
        detail = "ينفَّذ داخل مجلد يمنحه المستخدم عبر Storage Access Framework. " +
            "لا وصول خارج شجرة المجلد، والحفظ يتطلب إذن كتابة صالحًا. " +
            "افتح «مساحة عمل المطور» لمنح المجلد وتشغيل الأداة.",
    )
    AgentTool.ApplyPatch -> ToolStatus(
        available = false,
        label = "غير منفذ",
        detail = "تطبيق patch ثنائي غير موصول. التعديل النصي متاح يدويًا عبر محرر مساحة العمل فقط.",
    )
    AgentTool.CompareDiff -> ToolStatus(
        available = false,
        label = "غير منفذ",
        detail = "لا توجد واجهة diff في هذه النسخة. لا تُعرض فروقات وهمية.",
    )
    AgentTool.GitStatus, AgentTool.GitDiff, AgentTool.GitLog -> ToolStatus(
        available = false,
        label = "غير متاح على Android",
        detail = "لا يوجد git binary داخل sandbox التطبيق، وتشغيله يتطلب Termux أو جهازًا بصلاحيات root أو خادم بناء. " +
            "لن تُعاد نتائج Git مُختلقة.",
    )
    AgentTool.RunTests -> ToolStatus(
        available = false,
        label = "غير متاح على Android",
        detail = "تشغيل Gradle/الاختبارات يحتاج JDK وبناء كامل خارج التطبيق. نفّذها على CI أو جهاز التطوير.",
    )
    AgentTool.TerminalExecution -> ToolStatus(
        available = false,
        label = "غير متاح على Android",
        detail = "Android لا يسمح لتطبيق عادي بتنفيذ shell عشوائي. الأوامر المدعومة فقط هي واجهات SAF أعلاه.",
    )
    AgentTool.BuildMonitoring -> ToolStatus(
        available = false,
        label = "غير متاح على Android",
        detail = "مراقبة بناء Gradle تتطلب عملية بناء حقيقية على مضيف التطوير أو CI؛ لا مصدر بيانات داخل التطبيق.",
    )
    AgentTool.FetchDocumentation -> ToolStatus(
        available = false,
        label = "غير منفذ",
        detail = "لا يوجد جالب توثيق موصول. لن يُرسل طلب شبكة وهمي.",
    )
    AgentTool.ProjectDiagnostics -> ToolStatus(
        available = false,
        label = "غير منفذ",
        detail = "تشخيص المشروع يحتاج محلل بناء؛ غير موصول في هذه النسخة.",
    )
}

/**
 * Agent Tool System (DEV-07).
 * Shows the real availability of every tool. File tools are backed by the SAF
 * workspace; Git/terminal/test/build tools disclose the exact Android limits.
 */
@Composable
fun AgentToolSystemScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    var tools by remember { mutableStateOf(listOf<AgentTool>()) }
    var detail by remember { mutableStateOf<Pair<AgentTool, ToolStatus>?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        tools = AgentTool.values().toList()
    }

    detail?.let { (tool, status) ->
        AlertDialog(
            onDismissRequest = { detail = null },
            title = {
                KText(
                    agentToolText(tool),
                    size = 14.sp,
                    weight = FontWeight.Bold,
                    color = Color.White,
                )
            },
            text = {
                Column {
                    KText(status.label, size = 10.sp, weight = FontWeight.Bold, color = if (status.available) V2Green else V2Amber)
                    Spacer(Modifier.height(6.dp))
                    KText(status.detail, size = 9.6.sp, color = V2Soft, maxLines = 8)
                }
            },
            confirmButton = {
                TextButton(onClick = { detail = null }) { KText("إغلاق", size = 10.sp, color = V2Cyan) }
            },
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
        V2LuxuryIdentity(
            title = "نظام أدوات الوكيل",
            english = "AGENT TOOL SYSTEM",
            subtitle = "قدرات فعلية وحدود Android معلنة — دون أزرار ميتة",
            accent = V2Amber,
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
                    glow = V2Amber,
                    borderAlpha = .28f,
                ) {
                    KText(
                        "أدوات الملفات تعمل فقط داخل مجلد SAF يمنحه المستخدم. " +
                            "Git والمحطة والاختبارات والبناء غير قابلة للتنفيذ داخل تطبيق Android عادي، " +
                            "وتظهر هنا كغير متاحة بدلًا من محاكاة نتائج.",
                        size = 9.2.sp,
                        color = V2GoldText,
                        maxLines = 5,
                    )
                }
            }

            items(tools, key = { it.name }) { tool ->
                ToolCard(
                    tool = tool,
                    status = toolStatus(tool),
                    onClick = { detail = tool to toolStatus(tool) },
                )
            }
        }
    }
}

@Composable
private fun ToolCard(
    tool: AgentTool,
    status: ToolStatus,
    onClick: () -> Unit,
) {
    val toolColor = agentToolColor(tool)
    val stateColor = if (status.available) V2Green else V2Amber

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = toolColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(toolColor.copy(alpha = .16f))
                        .border(1.dp, toolColor.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = toolIcon(tool),
                        contentDescription = null,
                        tint = toolColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(agentToolText(tool), size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(status.label, size = 9.sp, color = stateColor, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onClick) {
                    KText("التفاصيل", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    KText("النطاق", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        if (status.available) "مجلد SAF مصرح به" else "غير قابل للتنفيذ هنا",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Column(Modifier.weight(1f)) {
                    KText("الحالة", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        status.label,
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = stateColor,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun toolIcon(tool: AgentTool): androidx.compose.ui.graphics.vector.ImageVector = when (tool) {
    AgentTool.ReadFile -> Icons.Rounded.Code
    AgentTool.ListDirectory -> Icons.Rounded.Code
    AgentTool.SearchFiles -> Icons.Rounded.DocumentScanner
    AgentTool.CreateFile -> Icons.Rounded.PlayArrow
    AgentTool.EditFile -> Icons.Rounded.Code
    AgentTool.ApplyPatch -> Icons.Rounded.ArrowForward
    AgentTool.CompareDiff -> Icons.Rounded.BugReport
    AgentTool.GitStatus -> Icons.Rounded.Terminal
    AgentTool.GitDiff -> Icons.Rounded.BugReport
    AgentTool.GitLog -> Icons.Rounded.Terminal
    AgentTool.RunTests -> Icons.Rounded.PlayArrow
    AgentTool.TerminalExecution -> Icons.Rounded.Terminal
    AgentTool.FetchDocumentation -> Icons.Rounded.DocumentScanner
    AgentTool.ProjectDiagnostics -> Icons.Rounded.BugReport
    AgentTool.BuildMonitoring -> Icons.Rounded.Terminal
}
