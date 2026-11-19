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
import androidx.compose.material.icons.rounded.Psychology
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
import com.knoux.ai.developer.model.AgentProfile
import com.knoux.ai.developer.model.AgentRole
import com.knoux.ai.developer.model.ExecutionMode
import com.knoux.ai.developer.model.IntelligenceLevel
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

private fun agentRoleText(role: AgentRole): String = when (role) {
    AgentRole.SoftwareArchitect -> "مهندس برمجيات"
    AgentRole.FullStackDeveloper -> "مطور كامل المسار"
    AgentRole.AndroidNativeDeveloper -> "مطور أندرويد أصلي"
    AgentRole.FrontendEngineer -> "مطور واجهة أمامية"
    AgentRole.BackendEngineer -> "مطور خلفي"
    AgentRole.UiUxEngineer -> "مصمم تجربة المستخدم والواجهة"
    AgentRole.Debugger -> "مُصلح أخطاء"
    AgentRole.QaEngineer -> "مُختبر جودة"
    AgentRole.SecurityReviewer -> "مراجع أمان"
    AgentRole.DevOpsEngineer -> "مطور عمليات"
    AgentRole.ResearchAgent -> "وكيل بحث"
    AgentRole.DocumentationAgent -> "وكيل توثيق"
    AgentRole.DatabaseEngineer -> "مهندس قواعد بيانات"
    AgentRole.CodeReviewer -> "مراجع أكواد"
    AgentRole.GeneralAssistant -> "مساعد عام"
}

private fun executionModeText(mode: ExecutionMode): String = when (mode) {
    ExecutionMode.Ask -> "السؤال والاستفسار"
    ExecutionMode.Plan -> "التخطيط فقط"
    ExecutionMode.Build -> "البناء والتنفيذ"
    ExecutionMode.Debug -> "تصحيح الأخطاء"
    ExecutionMode.Review -> "المراجعة"
    ExecutionMode.Auto -> "تلقائي متعدد الخطوات"
}

private fun executionModeColor(mode: ExecutionMode): Color = when (mode) {
    ExecutionMode.Ask -> V2Cyan
    ExecutionMode.Plan -> V2Blue
    ExecutionMode.Build -> V2Green
    ExecutionMode.Debug -> V2Pink
    ExecutionMode.Review -> V2Violet
    ExecutionMode.Auto -> V2Amber
}

private fun intelligenceLevelText(level: IntelligenceLevel): String = when (level) {
    IntelligenceLevel.Economy -> "مُوفر"
    IntelligenceLevel.Fast -> "سريع"
    IntelligenceLevel.Normal -> "عادي"
    IntelligenceLevel.Deep -> "عميق"
    IntelligenceLevel.Max -> "أقصى"
}

/**
 * Agent Engine (DEV-05).
 * Agent profiles per role with execution mode, intelligence level, preferred model,
 * and tool permissions. Real agent routing based on model capability.
 */
@Composable
fun AgentEngineScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    var profiles by remember { mutableStateOf(listOf<AgentProfile>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        profiles = AgentRole.values().map { role ->
            AgentProfile(
                role = role,
                preferredProviderId = null,
                preferredModelId = null,
                executionMode = ExecutionMode.Ask,
                intelligenceLevel = IntelligenceLevel.Normal,
                maxTokens = 4096,
                maxToolCalls = 10,
                maxIterations = 5,
                requireApproval = true,
                streamingEnabled = true,
            )
        }
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "محرك الوكلاء",
            english = "AGENT ENGINE",
            subtitle = "وكلاء الذكاء الاصطناعي القابلة للتكيّف لكل دور هندسي",
            accent = V2Green,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(profiles) { profile ->
                AgentProfileCard(
                    profile = profile,
                )
            }
        }
    }
}

@Composable
private fun AgentProfileCard(
    profile: AgentProfile,
) {
    val modeColor = executionModeColor(profile.executionMode)

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = modeColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(modeColor.copy(alpha = .16f))
                        .border(1.dp, modeColor.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Psychology,
                        contentDescription = null,
                        tint = modeColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(agentRoleText(profile.role), size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        executionModeText(profile.executionMode),
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* configure agent */ }) {
                    KText("تكوين", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    KText("مستوى الاستدلال", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        intelligenceLevelText(profile.intelligenceLevel),
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Column {
                    KText("الحد الأقصى للتكرارات", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        profile.maxIterations.toString(),
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Column {
                    KText("الموافقة مطلوبة", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        if (profile.requireApproval) "نعم" else "لا",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = if (profile.requireApproval) V2Red else V2Green,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}