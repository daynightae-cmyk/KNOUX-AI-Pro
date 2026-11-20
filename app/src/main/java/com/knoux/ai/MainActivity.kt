package com.knoux.ai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.knoux.ai.splash.KnouxSplashScreen
import com.knoux.ai.ui.theme.KnouxDayColorScheme
import com.knoux.ai.ui.theme.KnouxTheme
import com.knoux.ai.ui.screens.WelcomeScreen
import com.knoux.ai.ui.v2.KnouxOnboarding
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.KnouxProState
import com.knoux.ai.ui.v2.KnouxDayState
import com.knoux.ai.ui.v2.LocalDayMode
import com.knoux.ai.ui.v2.LocalKnouxOnDark
import com.knoux.ai.ui.v2.LocalProActive
import com.knoux.ai.ui.v2.LocalProTheme
import com.knoux.ai.ui.v2.LocalServiceNavigate
import com.knoux.ai.ui.v2.ProTheme
import com.knoux.ai.ui.v2.markKnouxOnboardingSeen
import com.knoux.ai.startup.StartDestination
import com.knoux.ai.startup.StartupCoordinator
import com.knoux.ai.ui.v2.V2AiRecommendationsScreen
import com.knoux.ai.ui.v2.V2AiScreen
import com.knoux.ai.ui.v2.V2AppDetailScreen
import com.knoux.ai.ui.v2.V2AppManagerScreen
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2BatteryDiagnosticsScreen
import com.knoux.ai.ui.v2.V2BatteryReadingsScreen
import com.knoux.ai.ui.v2.V2BatteryScreen
import com.knoux.ai.ui.v2.V2BottomBar
import com.knoux.ai.ui.v2.CleanTools
import com.knoux.ai.ui.v2.SectionHub
import com.knoux.ai.ui.v2.SectionTools
import com.knoux.ai.ui.v2.V2SectionHubScreen
import com.knoux.ai.ui.v2.V2SectionToolScreen
import com.knoux.ai.ui.v2.V2CleanHubScreen
import com.knoux.ai.ui.v2.V2CleanToolScreen
import com.knoux.ai.ui.v2.V2DeepScanScreen
import com.knoux.ai.ui.v2.V2DuplicatesScreen
import com.knoux.ai.ui.v2.V2HomeScreen
import com.knoux.ai.ui.v2.V2PerformanceReportScreen
import com.knoux.ai.ui.v2.V2PrivacyRiskScreen
import com.knoux.ai.ui.v2.V2PrivacyScreen
import com.knoux.ai.ui.v2.V2ProScreen
import com.knoux.ai.ui.v2.V2ReportsScreen
import com.knoux.ai.ui.v2.V2ResidueReviewScreen
import com.knoux.ai.ui.v2.V2ResidueScreen
import com.knoux.ai.ui.v2.V2ScanSummaryScreen
import com.knoux.ai.ui.v2.V2SettingsScreenV2
import com.knoux.ai.ui.v2.V2SmartCleanScreen
import com.knoux.ai.ui.v2.V2StorageBreakdownScreen
import com.knoux.ai.ui.v2.V2StorageScreen
import com.knoux.ai.ui.v2.V2LargeFilesScreen
import com.knoux.ai.ui.v2.V2OldBackupsScreen
import com.knoux.ai.ui.v2.V2SystemLogsScreen
import com.knoux.ai.ui.v2.V2DownloadCleanupScreen
import com.knoux.ai.ui.v2.V2FileExplorerScreen
import com.knoux.ai.ui.v2.V2DeviceInfoScreen
import com.knoux.ai.ui.v2.V2NetworkScanScreen
import com.knoux.ai.ui.v2.V2SecurityScanScreen
import com.knoux.ai.ui.v2.V2NotificationManagerScreen
import com.knoux.ai.ui.v2.V2ToolsHubScreen
import com.knoux.ai.ui.v2.V2DeveloperToolsScreen
import com.knoux.ai.ui.v2.V2GoogleServicesScreen
import com.knoux.ai.ui.v2.V2AdvancedSecurityScreen
import com.knoux.ai.ui.v2.V2BackupRestoreScreen
import com.knoux.ai.developer.ui.DeveloperStudioScreen
import com.knoux.ai.developer.ui.ProviderMarketplaceScreen
import com.knoux.ai.developer.ui.ApiKeyVaultScreen
import com.knoux.ai.developer.ui.ModelExplorerScreen
import com.knoux.ai.developer.ui.AgentEngineScreen
import com.knoux.ai.developer.ui.MobileDeveloperWorkspaceScreen
import com.knoux.ai.developer.ui.AgentToolSystemScreen
import com.knoux.ai.cloud.ui.UnifiedCloudCleanerScreen
import com.knoux.ai.google.ui.GoogleStorageDashboardScreen
import com.knoux.ai.microsoft.ui.MicrosoftStorageDashboardScreen

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TLS uses the Android platform defaults. No process-wide SSL factory or
        // hostname verifier is replaced; provider connections stay isolated.
        enableEdgeToEdge()
        // Debug-only route injection so every screen can be inspected directly with adb.
        val debugRoute = if (BuildConfig.DEBUG) intent?.getStringExtra("route") else null
        val debugDayMode = if (BuildConfig.DEBUG) intent?.getStringExtra("day_mode")?.toBooleanStrictOrNull() else null
        if (BuildConfig.DEBUG) Log.i("KnouxRoute", "onCreate route=$debugRoute")
        // KnouxProState.previewEnabled is now resolved off the main thread inside KnouxApp
        // via produceState, so onCreate stays free of blocking I/O before the first frame.
        setContent {
            KnouxTheme {
                KnouxApp(startRoute = debugRoute, startDayMode = debugDayMode)
            }
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_CLEAN = "clean"
private const val ROUTE_TOOLS = "tools"
private const val ROUTE_REPORTS = "reports"
private const val ROUTE_PRO = "pro"
private const val SETTINGS_ROUTE = "settings"

private const val ROUTE_APP_DETAIL = "app-detail"

private enum class EntryStage { Splash, Onboarding, Auth, Shell }

@Composable
fun KnouxApp(startRoute: String? = null, startDayMode: Boolean? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val entryDebugRoute = startRoute?.takeIf { it == "onboarding" || it == "auth" }
    val shellStartRoute = startRoute?.takeUnless { it == "onboarding" || it == "auth" }
    // Resolve the post-splash destination once, off the main thread, while the
    // approved splash artwork and sound timeline run unchanged.
    val coordinator = remember {
        StartupCoordinator(context, scope).also { it.resolve() }
    }
    val destination = coordinator.destination.value
    var entryStage by rememberSaveable {
        mutableStateOf(
            when {
                entryDebugRoute == "onboarding" -> EntryStage.Onboarding
                entryDebugRoute == "auth" -> EntryStage.Auth
                !startRoute.isNullOrBlank() -> EntryStage.Shell
                else -> EntryStage.Splash
            }
        )
    }
    var splashFinished by remember { mutableStateOf(false) }

    // Resolve Pro preview identity off the main thread: read SharedPreferences on
    // Dispatchers.IO so the first frame is not blocked by disk I/O.
    val proPreview by produceState(initialValue = false, key1 = context) {
        val ctx = context.applicationContext
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            KnouxProState.load(ctx)
        }
        value = KnouxProState.previewEnabled
    }

    // Day world resolves the same way: prefs on IO, then recomposition paints
    // the porcelain chrome. KnouxDayState.enabled is observable state, so the
    // settings toggle repaints the whole shell instantly.
    LaunchedEffect(context) {
        val ctx = context.applicationContext
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            KnouxDayState.load(ctx, startDayMode)
        }
    }
    val dayActive = KnouxDayState.enabled

    // Status-bar glyphs follow the world: dark icons on porcelain, light on
    // the night sky. The entry art stays night-lit, so its stage keeps light
    // icons even when the shell behind it is porcelain.
    val view = LocalView.current
    androidx.compose.runtime.SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        val nightStage = entryStage != EntryStage.Shell
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
            dayActive && !nightStage
    }

    // Leave splash exactly once: after the animation finishes AND the local
    // session decision is ready. Returning users with a valid local session go
    // straight to Shell; no login flash, no forced Auth detour.
    LaunchedEffect(splashFinished, destination) {
        if (entryStage == EntryStage.Splash && splashFinished && destination != null) {
            entryStage = when (destination) {
                StartDestination.Onboarding -> EntryStage.Onboarding
                StartDestination.Shell -> EntryStage.Shell
                StartDestination.Auth -> EntryStage.Auth
                else -> EntryStage.Splash
            }
        }
    }
    // `splashFinished` is set ONLY by KnouxSplashScreen's real animation-completion
    // callback. Destination readiness alone must never terminate the animation:
    // the effect above fires once, when BOTH inputs are ready, so there is no
    // destination-driven early cut and no duplicate navigation.
    var signedIn by rememberSaveable { mutableStateOf(false) }
    var accountInfoVisible by remember { mutableStateOf(false) }
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: ROUTE_HOME

    val open: (String) -> Unit = { target ->
        nav.navigate(target) { launchSingleTop = true }
    }
    val goBack: () -> Unit = {
        if (!nav.popBackStack()) {
            nav.navigate(ROUTE_HOME) { launchSingleTop = true }
        }
    }
    val openTab: (String) -> Unit = { target ->
        nav.navigate(target) {
            popUpTo(ROUTE_HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    // The Pro visual preview drives the whole shell: golden theme + the red PRO
    // markers on the 50 paid services. It is a debug/preview presentation, not a
    // verified entitlement (billing is Stage 2); LocalProActive stays false in
    // release until a real entitlement is supplied.
    CompositionLocalProvider(
        LocalServiceNavigate provides open,
        LocalProTheme provides if (proPreview) ProTheme.Pro else ProTheme.Standard,
        LocalProActive provides proPreview,
        LocalDayMode provides dayActive,
    ) {
        Box(Modifier.fillMaxSize()) {
        V2Background()

        // Splash, onboarding and auth are approved night-lit art: they keep
        // the full night world even when the shell behind them is porcelain.
        Crossfade(
            targetState = entryStage,
            animationSpec = tween(420),
            label = "entry-handoff",
        ) { stage ->
            when (stage) {
                EntryStage.Splash -> KnouxSplashScreen(
                    onFinished = { splashFinished = true }
                )
                EntryStage.Onboarding -> CompositionLocalProvider(
                    LocalDayMode provides false,
                    LocalKnouxOnDark provides true,
                ) {
                    Box(Modifier.fillMaxSize()) {
                        V2Background()
                        KnouxOnboarding(
                            onFinish = {
                                markKnouxOnboardingSeen(context)
                                entryStage = EntryStage.Auth
                            }
                        )
                    }
                }
                EntryStage.Auth -> CompositionLocalProvider(
                    LocalDayMode provides false,
                    LocalKnouxOnDark provides true,
                ) {
                    Box(Modifier.fillMaxSize()) {
                        V2Background()
                        WelcomeScreen(
                            onContinueAsGuest = {
                                signedIn = false
                                entryStage = EntryStage.Shell
                            },
                            onSignedIn = {
                                signedIn = true
                                entryStage = EntryStage.Shell
                            },
                            onOpenAccountInfo = { accountInfoVisible = true },
                        )
                    }
                }
                EntryStage.Shell -> {
                // Material surfaces (dialogs, sheets) follow the porcelain day
                // scheme; the V2 chrome paints its own world on top.
                val shellScheme = if (dayActive) KnouxDayColorScheme else androidx.compose.material3.MaterialTheme.colorScheme
                androidx.compose.material3.MaterialTheme(colorScheme = shellScheme) {
                Scaffold(
                    containerColor = Color.Transparent,
                    // Nothing hides the page's ending: the bottom bar already
                    // pads itself past the navigation bar, and the scaffold must
                    // not eat another navigation-bar inset, or the last card of
                    // a service page loses ~44px of its height behind the bar.
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (route != SETTINGS_ROUTE) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                V2BottomBar(
                                    selectedRoute = route,
                                    onSelect = { tab -> openTab(tab) },
                                )
                            }
                        }
                    },
                ) { inner ->
                    AppNavHost(
                        nav = nav,
                        startRoute = shellStartRoute,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner),
                        open = open,
                        goBack = goBack,
                        onAccount = { accountInfoVisible = true },
                    )
                }
                }
                }
            }
        }
    }

    if (accountInfoVisible) {
        // The account dialog lives outside the shell branch: give it the same
        // porcelain Material scheme so its surface and text match day mode.
        androidx.compose.material3.MaterialTheme(
            colorScheme = if (dayActive) KnouxDayColorScheme else androidx.compose.material3.MaterialTheme.colorScheme,
        ) {
        AlertDialog(
            onDismissRequest = { accountInfoVisible = false },
            title = { KText(if (signedIn) "KNOUX ID · تم تسجيل الدخول" else "KNOUX ID", size = 16.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                KText(
                    "يمكنك الدخول بحساب Google أو Passkey أو البريد وكلمة المرور/رمز OTP، " +
                        "أو المتابعة كضيف. وضع الضيف يشغّل الأدوات محليًا بدون اعتباره اشتراكًا أو استحقاق Pro.",
                    size = 12.sp,
                    color = V2Soft,
                )
            },
            confirmButton = {
                TextButton(onClick = { accountInfoVisible = false }) {
                    KText("فهمت", size = 12.sp, weight = FontWeight.Bold, color = V2Cyan)
                }
            },
        )
        }
    }
    }
}

// These catalog routes use dedicated screens below, not the generic section screen.
internal fun sectionToolNavigationRoutes(): List<String> =
    SectionTools.defs.map { it.route }
        // Dedicated destinations already registered individually below; generic
        // section rendering would duplicate the route and lose their real UI.
        .filterNot {
            it == "bat-readings" || it == "ai-recommendations" ||
                it == "developer-info" || it == "google-services" ||
                it == "advanced-security" || it == "backup-restore"
        }

@Composable
private fun AppNavHost(
    nav: NavHostController,
    startRoute: String?,
    modifier: Modifier,
    open: (String) -> Unit,
    goBack: () -> Unit,
    onAccount: () -> Unit,
) {
    NavHost(
        navController = nav,
        startDestination = startRoute ?: ROUTE_HOME,
        modifier = modifier,
        enterTransition = { fadeIn(tween(280)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(240)) },
        popExitTransition = { fadeOut(tween(180)) },
    ) {
        composable(ROUTE_HOME) {
            V2HomeScreen(
                onNavigate = open,
                onMenu = { open(SETTINGS_ROUTE) },
                onPro = { open(ROUTE_PRO) },
                onProfile = onAccount,
                onNotifications = { open(ROUTE_REPORTS) },
            )
        }

        composable(ROUTE_CLEAN) {
            V2CleanHubScreen(
                onNavigate = open,
                onMenu = { open(SETTINGS_ROUTE) },
                onPro = { open(ROUTE_PRO) },
                onProfile = onAccount,
                onNotifications = { open(ROUTE_REPORTS) },
            )
        }

        composable(ROUTE_TOOLS) {
            V2ToolsHubScreen(
                onNavigate = open,
                onMenu = { open(SETTINGS_ROUTE) },
                onPro = { open(ROUTE_PRO) },
                onProfile = onAccount,
                onNotifications = { open(ROUTE_REPORTS) },
            )
        }

        composable(ROUTE_REPORTS) {
            V2ReportsScreen(
                onOpenDetail = { open("performance-report") },
                onMenu = { open(SETTINGS_ROUTE) },
                onPro = { open(ROUTE_PRO) },
                onProfile = onAccount,
                onNotifications = {},
            )
        }

        composable(ROUTE_PRO) {
            V2ProScreen(onBack = goBack, onNavigate = open)
        }

        composable("privacy") {
            V2PrivacyScreen(
                onBack = goBack,
                onOpenDetail = { open("privacy-risk") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("duplicates") {
            V2DuplicatesScreen(
                onBack = goBack,
                onOpenDetail = { open("duplicate-group") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("deepscan") {
            V2DeepScanScreen(
                onBack = goBack,
                onOpenSummary = { open("deep-scan-summary") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("residue") {
            V2ResidueScreen(
                onBack = goBack,
                onReview = { open("residue-item") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("appmanager") {
            V2AppManagerScreen(
                onBack = goBack,
                onOpenDetail = { open(ROUTE_APP_DETAIL) },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable(ROUTE_APP_DETAIL) {
            V2AppDetailScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("installed-apps") {
            V2AppManagerScreen(
                onBack = goBack,
                onOpenDetail = { open(ROUTE_APP_DETAIL) },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("storage") {
            V2StorageScreen(
                onBack = goBack,
                onOpenDetail = { open("storage-breakdown") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("battery") {
            V2BatteryScreen(
                onBack = goBack,
                onOpenDetail = { open("battery-diagnostics") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("ai") {
            V2AiScreen(
                onBack = goBack,
                onOpenDetail = { open("ai-recommendations") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("smartclean") {
            V2SmartCleanScreen(
                onBack = goBack,
                onOpenDeepScan = { open("deepscan") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        // Smart Clean's fifteen internal services. Each route opens its own screen.
        CleanTools.routes.forEach { cleanRoute ->
            composable(cleanRoute) {
                V2CleanToolScreen(
                    route = cleanRoute,
                    onBack = goBack,
                    onPro = { open(ROUTE_PRO) },
                )
            }
        }
        // Ten section hubs: each opens its fifteen independently routed services.
        SectionHub.routes.forEach { hubRoute ->
            composable(hubRoute) {
                V2SectionHubScreen(
                    route = hubRoute,
                    onBack = goBack,
                    onPro = { open(ROUTE_PRO) },
                )
            }
        }
        // Nine sections × 15 services, excluding the dedicated screens below.
        sectionToolNavigationRoutes().forEach { toolRoute ->
            composable(toolRoute) {
                V2SectionToolScreen(
                    route = toolRoute,
                    onBack = goBack,
                    onPro = { open(ROUTE_PRO) },
                )
            }
        }
        composable(SETTINGS_ROUTE) {
            V2SettingsScreenV2(onBack = goBack)
        }
        composable("privacy-risk") {
            V2PrivacyRiskScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("duplicate-group") {
            V2DuplicatesScreen(
                onBack = goBack,
                onOpenDetail = {},
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("deep-scan-summary") {
            V2ScanSummaryScreen(
                onBack = goBack,
                onOpenSmartClean = { open("smartclean") },
                onPro = { open(ROUTE_PRO) },
            )
        }
        composable("performance-report") {
            V2PerformanceReportScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("residue-item") {
            V2ResidueReviewScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("storage-breakdown") {
            V2StorageBreakdownScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("battery-diagnostics") {
            V2BatteryDiagnosticsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("bat-readings") {
            V2BatteryReadingsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("ai-recommendations") {
            V2AiRecommendationsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("large-files") {
            V2LargeFilesScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("old-backups") {
            V2OldBackupsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("system-logs") {
            V2SystemLogsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("download-cleanup") {
            V2DownloadCleanupScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("file-explorer") {
            V2FileExplorerScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("device-info") {
            V2DeviceInfoScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("network-scan") {
            V2NetworkScanScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("security-scan") {
            V2SecurityScanScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("notification-manager") {
            V2NotificationManagerScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        // The four owner-requested extra section hubs route to real device screens.
        composable("developer-info") {
            V2DeveloperToolsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("google-services") {
            V2GoogleServicesScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("advanced-security") {
            V2AdvancedSecurityScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("backup-restore") {
            V2BackupRestoreScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        // Developer Studio (EXT-01) routes.
        composable("developer-studio") {
            DeveloperStudioScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-provider-marketplace") {
            ProviderMarketplaceScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-api-key-vault") {
            ApiKeyVaultScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-model-explorer") {
            ModelExplorerScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-agent-engine") {
            AgentEngineScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-workspace") {
            MobileDeveloperWorkspaceScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("dev-tool-system") {
            AgentToolSystemScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("cloud-cleaner") {
            UnifiedCloudCleanerScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        // EXT-03 Google Ecosystem routes.
        composable("google-storage") {
            GoogleStorageDashboardScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        // EXT-04 Microsoft Ecosystem routes.
        composable("microsoft-storage") {
            MicrosoftStorageDashboardScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
    }
}
