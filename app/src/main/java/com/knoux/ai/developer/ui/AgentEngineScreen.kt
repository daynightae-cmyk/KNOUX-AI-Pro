package com.knoux.ai.developer.ui

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.knoux.ai.developer.provider.ProviderRegistryHolder
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
import org.json.JSONArray
import org.json.JSONObject

private const val AGENT_PREFS = "knoux_agent_profiles_v1"
private const val AGENT_PROFILES_KEY = "profiles"

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

private fun defaultProfiles(): List<AgentProfile> = AgentRole.values().map { role ->
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

private fun loadProfiles(context: Context): List<AgentProfile> {
    val defaults = defaultProfiles().associateBy { it.role }
    val raw = context.getSharedPreferences(AGENT_PREFS, Context.MODE_PRIVATE)
        .getString(AGENT_PROFILES_KEY, null) ?: return defaults.values.toList()
    return runCatching {
        val array = JSONArray(raw)
        val loaded = mutableMapOf<AgentRole, AgentProfile>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val role = runCatching { AgentRole.valueOf(obj.getString("role")) }.getOrNull() ?: continue
            val base = defaults[role] ?: continue
            loaded[role] = base.copy(
                preferredProviderId = obj.optString("provider").takeIf { it.isNotEmpty() },
                preferredModelId = obj.optString("model").takeIf { it.isNotEmpty() },
                executionMode = runCatching {
                    ExecutionMode.valueOf(obj.getString("mode"))
                }.getOrDefault(base.executionMode),
                intelligenceLevel = runCatching {
                    IntelligenceLevel.valueOf(obj.getString("level"))
                }.getOrDefault(base.intelligenceLevel),
                maxTokens = obj.optInt("maxTokens", base.maxTokens ?: 4096),
                maxToolCalls = obj.optInt("maxToolCalls", base.maxToolCalls ?: 10),
                maxIterations = obj.optInt("maxIterations", base.maxIterations),
                requireApproval = obj.optBoolean("requireApproval", base.requireApproval),
                streamingEnabled = obj.optBoolean("streaming", base.streamingEnabled),
                updatedAt = obj.optLong("updatedAt", base.updatedAt),
            )
        }
        AgentRole.values().map { loaded[it] ?: defaults.getValue(it) }
    }.getOrDefault(defaults.values.toList())
}

private fun saveProfiles(context: Context, profiles: List<AgentProfile>) {
    val array = JSONArray()
    profiles.forEach { profile ->
        array.put(
            JSONObject()
                .put("role", profile.role.name)
                .put("provider", profile.preferredProviderId ?: "")
                .put("model", profile.preferredModelId ?: "")
                .put("mode", profile.executionMode.name)
                .put("level", profile.intelligenceLevel.name)
                .put("maxTokens", profile.maxTokens ?: 0)
                .put("maxToolCalls", profile.maxToolCalls ?: 0)
                .put("maxIterations", profile.maxIterations)
                .put("requireApproval", profile.requireApproval)
                .put("streaming", profile.streamingEnabled)
                .put("updatedAt", profile.updatedAt),
        )
    }
    context.getSharedPreferences(AGENT_PREFS, Context.MODE_PRIVATE)
        .edit().putString(AGENT_PROFILES_KEY, array.toString()).commit()
}

/**
 * Agent Engine (DEV-05).
 *
 * Profiles are real, persisted per role and applied to every future agent
 * request. This build intentionally does not fabricate execution: provider
 * adapters implement model discovery/connection tests only, so the screen says
 * so instead of showing a fake run. Tool execution is handled separately and
 * only where Android actually permits it.
 */
@Composable
fun AgentEngineScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    var profiles by remember { mutableStateOf(listOf<AgentProfile>()) }
    var editing by remember { mutableStateOf<AgentProfile?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val providers = remember {
        ProviderRegistryHolder.registerBuiltInAdapters(context)
        ProviderRegistryHolder.init().allProviders.map { it.providerId to it.displayName }
    }

    LaunchedEffect(Unit) { profiles = loadProfiles(context) }

    editing?.let { profile ->
        AgentConfigureDialog(
            profile = profile,
            providers = providers,
            onDismiss = { editing = null },
            onSave = { updated ->
                val next = profiles.map { if (it.role == updated.role) updated else it }
                saveProfiles(context, next)
                profiles = next
                editing = null
                message = "حُفظ تكوين ${agentRoleText(updated.role)} على هذا الجهاز."
            },
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
        V2LuxuryIdentity(
            title = "محرك الوكلاء",
            english = "AGENT ENGINE",
            subtitle = "تكوين محفوظ لكل دور هندسي — دون تنفيذ وهمي",
            accent = V2Green,
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
                    borderAlpha = .30f,
                ) {
                    Column {
                        KText(
                            "التنفيذ التلقائي غير مفعّل في هذه النسخة. المحوّلات الحالية تنفّذ اكتشاف النماذج واختبار الاتصال فقط، " +
                                "ولا يُرسل أي طلب توليد. التكوين أدناه حقيقي ومحفوظ ليُستخدم عند تفعيل طبقة التوليد.",
                            size = 9.2.sp,
                            color = V2GoldText,
                            maxLines = 5,
                        )
                        message?.let { text ->
                            Spacer(Modifier.height(5.dp))
                            KText(text, size = 9.sp, color = V2Cyan, maxLines = 3)
                        }
                    }
                }
            }

            items(profiles, key = { it.role.name }) { profile ->
                AgentProfileCard(
                    profile = profile,
                    onConfigure = { editing = profile },
                )
            }
        }
    }
}

@Composable
private fun AgentConfigureDialog(
    profile: AgentProfile,
    providers: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onSave: (AgentProfile) -> Unit,
) {
    var mode by remember { mutableStateOf(profile.executionMode) }
    var level by remember { mutableStateOf(profile.intelligenceLevel) }
    var iterations by remember { mutableStateOf(profile.maxIterations) }
    var approval by remember { mutableStateOf(profile.requireApproval) }
    var streaming by remember { mutableStateOf(profile.streamingEnabled) }
    var providerId by remember { mutableStateOf(profile.preferredProviderId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            KText(
                "تكوين ${agentRoleText(profile.role)}",
                size = 14.sp,
                weight = FontWeight.Bold,
                color = Color.White,
            )
        },
        text = {
            Column {
                KText("وضع التنفيذ", size = 10.sp, weight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ExecutionMode.values().forEach { candidate ->
                        ChoiceRow(
                            label = executionModeText(candidate),
                            selected = mode == candidate,
                            color = executionModeColor(candidate),
                            onClick = { mode = candidate },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                KText("مستوى الاستدلال", size = 10.sp, weight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    IntelligenceLevel.values().forEach { candidate ->
                        ChoiceRow(
                            label = intelligenceLevelText(candidate),
                            selected = level == candidate,
                            color = V2Cyan,
                            onClick = { level = candidate },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText("أقصى تكرارات", size = 10.sp, color = Color.White)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { if (iterations > 1) iterations-- }) {
                        KText("−", size = 14.sp, color = V2Cyan)
                    }
                    KText(iterations.toString(), size = 11.sp, color = V2GoldText)
                    TextButton(onClick = { if (iterations < 20) iterations++ }) {
                        KText("+", size = 14.sp, color = V2Cyan)
                    }
                }
                SwitchRow("طلب موافقة قبل الكتابة", approval) { approval = it }
                SwitchRow("تفعيل البث عند التوليد", streaming) { streaming = it }

                if (providers.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    KText("المزوّد المفضل", size = 10.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    ChoiceRow(
                        label = "تلقائي حسب القدرات",
                        selected = providerId == null,
                        color = V2Soft,
                        onClick = { providerId = null },
                    )
                    providers.forEach { (id, name) ->
                        ChoiceRow(
                            label = name,
                            selected = providerId == id,
                            color = V2Cyan,
                            onClick = { providerId = id },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    profile.copy(
                        preferredProviderId = providerId,
                        executionMode = mode,
                        intelligenceLevel = level,
                        maxIterations = iterations,
                        requireApproval = approval,
                        streamingEnabled = streaming,
                        updatedAt = System.currentTimeMillis(),
                    ),
                )
            }) { KText("حفظ", size = 10.sp, color = V2Green) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { KText("إلغاء", size = 10.sp, color = V2Soft) }
        },
    )
}

@Composable
private fun ChoiceRow(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(12.dp).clip(RoundedCornerShape(6.dp))
                .background(if (selected) color else V2Soft.copy(alpha = .25f)),
        )
        Spacer(Modifier.width(8.dp))
        KText(
            label,
            size = 10.sp,
            color = if (selected) color else V2Soft,
            weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onClick) { KText(if (selected) "محدد" else "اختيار", size = 9.sp, color = color) }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        KText(label, size = 10.sp, color = Color.White)
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = V2Green, uncheckedThumbColor = V2Soft),
        )
    }
}

@Composable
private fun AgentProfileCard(
    profile: AgentProfile,
    onConfigure: () -> Unit,
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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                        size = 9.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                TextButton(onClick = onConfigure) {
                    KText("تكوين", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                    KText("أقصى تكرارات", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
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
                    KText("موافقة مطلوبة", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
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
