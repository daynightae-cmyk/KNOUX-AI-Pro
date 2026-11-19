package com.knoux.ai.developer.model

/**
 * Encrypted provider credentials stored in the Credential Vault.
 * The raw API key is NEVER stored in plaintext in Room or preferences.
 * Only a reference (key alias in Android Keystore) and masked display value
 * are persisted.
 */
data class ProviderConfig(
    val providerId: String,
    /** Android Keystore alias under which the wrapped key material is stored. */
    val keystoreAlias: String,
    /** Masked key for display (e.g. "sk-****1234"). Never the real key. */
    val maskedKey: String,
    /** Whether this credential has been configured with a real key. */
    val isConfigured: Boolean = false,
    /** Whether this is the default/active credential for the provider. */
    val isDefault: Boolean = false,
    /** Whether this credential is currently enabled. */
    val isEnabled: Boolean = true,
    /** When the credential was last tested successfully. */
    val lastTestedAt: Long = 0L,
    /** Last test result state. */
    val lastTestState: ProviderConnectionState = ProviderConnectionState.NotConfigured,
    /** When created. */
    val createdAt: Long = System.currentTimeMillis(),
)

/** Categories used to classify agent roles. */
enum class AgentRole {
    SoftwareArchitect, FullStackDeveloper, AndroidNativeDeveloper,
    FrontendEngineer, BackendEngineer, UiUxEngineer, Debugger,
    QaEngineer, SecurityReviewer, DevOpsEngineer, ResearchAgent,
    DocumentationAgent, DatabaseEngineer, CodeReviewer, GeneralAssistant,
}

fun AgentRole.displayName(): String = when (this) {
    AgentRole.SoftwareArchitect -> "مهندس برمجيات"
    AgentRole.FullStackDeveloper -> "مطور كامل المسار"
    AgentRole.AndroidNativeDeveloper -> "مطور أندرويد أصلي"
    AgentRole.FrontendEngineer -> "مطور واجهة أمامية"
    AgentRole.BackendEngineer -> "مطور خلفي"
    AgentRole.UiUxEngineer -> "م허س تجربة المستخدم والواجهة"
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

/** How the agent should execute. */
enum class ExecutionMode {
    Ask, Plan, Build, Debug, Review, Auto,
}

fun ExecutionMode.displayName(): String = when (this) {
    ExecutionMode.Ask -> "السؤال والاستفسار"
    ExecutionMode.Plan -> "التخطيط فقط"
    ExecutionMode.Build -> "البناء والتنفيذ"
    ExecutionMode.Debug -> "تصحيح الأخطاء"
    ExecutionMode.Review -> "المراجعة"
    ExecutionMode.Auto -> "تلقائي متعدد الخطوات"
}

/** Intelligence / effort level. Maps to provider-specific settings (token budget, reasoning effort, etc.). */
enum class IntelligenceLevel {
    Economy, Fast, Normal, Deep, Max,
}

fun IntelligenceLevel.displayName(): String = when (this) {
    IntelligenceLevel.Economy -> "مُوفر"
    IntelligenceLevel.Fast -> "سريع"
    IntelligenceLevel.Normal -> "عادي"
    IntelligenceLevel.Deep -> "عميق"
    IntelligenceLevel.Max -> "أقصى"
}

/**
 * Agent profile: which model and settings to use for a given role.
 */
data class AgentProfile(
    val role: AgentRole,
    /** Preferred provider ID; null means auto-route by capability. */
    val preferredProviderId: String? = null,
    /** Preferred model ID; null means auto-select. */
    val preferredModelId: String? = null,
    val executionMode: ExecutionMode = ExecutionMode.Ask,
    val intelligenceLevel: IntelligenceLevel = IntelligenceLevel.Normal,
    /** Max tokens per task, when the provider supports it. */
    val maxTokens: Int? = null,
    /** Max tool calls per iteration. */
    val maxToolCalls: Int? = null,
    /** Max iterations for Auto mode. */
    val maxIterations: Int = 5,
    /** Require user approval before write operations. */
    val requireApproval: Boolean = true,
    /** Whether streaming is enabled. */
    val streamingEnabled: Boolean = true,
    /** When last updated. */
    val updatedAt: Long = System.currentTimeMillis(),
)

/**
 * A task submitted to an agent.
 */
data class AgentTask(
    val id: String,
    val role: AgentRole,
    val prompt: String,
    val executionMode: ExecutionMode,
    val intelligenceLevel: IntelligenceLevel,
    val providerId: String?,
    val modelId: String?,
    /** Files/context attached to the task. */
    val contextFiles: List<String> = emptyList(),
    val status: TaskStatus = TaskStatus.Pending,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    /** Token usage measured from the actual provider response. */
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    /** Estimated cost in USD (from provider pricing metadata). */
    val estimatedCostUsd: Double = 0.0,
    val result: String? = null,
    val error: String? = null,
)

enum class TaskStatus {
    Pending, Running, Completed, Failed, Cancelled,
}

fun TaskStatus.displayName(): String = when (this) {
    TaskStatus.Pending -> "معلق"
    TaskStatus.Running -> "جارٍ التنفيذ"
    TaskStatus.Completed -> "مكتمل"
    TaskStatus.Failed -> "فشل"
    TaskStatus.Cancelled -> "مُلغي"
}

/**
 * Record of a tool execution performed on behalf of an agent.
 */
data class ToolCallRecord(
    val id: String,
    val taskId: String,
    val toolName: String,
    val parameters: String, // JSON
    val result: String?, // JSON or error
    val status: ToolCallStatus = ToolCallStatus.Pending,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val durationMs: Long = 0,
)

enum class ToolCallStatus {
    Pending, Running, Completed, Failed, Cancelled,
}

/** Supported agent tools. */
enum class AgentTool {
    ReadFile, ListDirectory, SearchFiles, CreateFile, EditFile,
    ApplyPatch, CompareDiff, GitStatus, GitDiff, GitLog,
    RunTests, TerminalExecution, FetchDocumentation,
    ProjectDiagnostics, BuildMonitoring,
}

fun AgentTool.displayName(): String = when (this) {
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