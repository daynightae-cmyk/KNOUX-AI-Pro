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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Terminal
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
import com.knoux.ai.developer.model.AgentTool
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Deep
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

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
 * Agent Tool System (DEV-07).
 * Tool registry with clear permissions: workspace scope, path traversal protection,
 * read/write checks, timeout, cancellation, and audit history.
 */
@Composable
fun AgentToolSystemScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    var tools by remember { mutableStateOf(listOf<AgentTool>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        tools = AgentTool.values().toList()
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "نظام أدوات الوكيل",
            english = "AGENT TOOL SYSTEM",
            subtitle = "سجل أدوات مع صلاحيات واضحة ومسار تدقيق",
            accent = V2Amber,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(tools) { tool ->
                ToolCard(
                    tool = tool,
                )
            }
        }
    }
}

@Composable
private fun ToolCard(
    tool: AgentTool,
) {
    val toolColor = agentToolColor(tool)

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = toolColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(agentToolText(tool), size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        toolPermissionText(tool),
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 2,
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* execute tool */ }) {
                    KText("تشغيل", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    KText("النطاق", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        "مساحة العمل المصرح بها",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Column {
                    KText("الحماية", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        "عابر للمسار محمي",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2Green,
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

private fun toolPermissionText(tool: AgentTool): String = when (tool) {
    AgentTool.ReadFile, AgentTool.ListDirectory, AgentTool.SearchFiles -> "قراءة فقط ضمن مساحة العمل"
    AgentTool.CreateFile, AgentTool.EditFile, AgentTool.ApplyPatch -> "كتابة تتطلب موافقة المستخدم"
    AgentTool.CompareDiff, AgentTool.GitStatus, AgentTool.GitDiff, AgentTool.GitLog -> "قراءة فقط (Git)"
    AgentTool.RunTests, AgentTool.TerminalExecution, AgentTool.BuildMonitoring -> "تنفيذ مراقب مع مهلة انتهاء"
    AgentTool.FetchDocumentation, AgentTool.ProjectDiagnostics -> "قراءة فقط (تشخيص)"
}