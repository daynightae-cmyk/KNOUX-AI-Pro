package com.knoux.ai

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.knoux.ai.splash.KnouxSplashScreen
import com.knoux.ai.ui.components.KnouxBackground
import com.knoux.ai.ui.components.KnouxAppHeader
import com.knoux.ai.ui.components.KnouxBottomBar
import com.knoux.ai.ui.components.SettingsIconButton
import com.knoux.ai.ui.components.TopLeading
import com.knoux.ai.ui.components.MainTab
import com.knoux.ai.ui.debug.QaCommandBus
import com.knoux.ai.ui.debug.RuntimeProbe
import com.knoux.ai.ui.screens.AiOptimizationScreen
import com.knoux.ai.ui.screens.AppManagerScreen
import com.knoux.ai.ui.screens.AppResidueScreen
import com.knoux.ai.ui.screens.AiRecommendationsScreen
import com.knoux.ai.ui.screens.BatteryDiagnosticsScreen
import com.knoux.ai.ui.screens.BatterySaverScreen
import com.knoux.ai.ui.screens.DeepScanScreen
import com.knoux.ai.ui.screens.DeepScanSummaryScreen
import com.knoux.ai.ui.screens.DuplicateFilesScreen
import com.knoux.ai.ui.screens.DuplicateGroupReviewScreen
import com.knoux.ai.ui.screens.HomeScreen
import com.knoux.ai.ui.screens.InstalledAppsReviewScreen
import com.knoux.ai.ui.screens.PerformanceReportDetailScreen
import com.knoux.ai.ui.screens.PrivacyRiskInspectorScreen
import com.knoux.ai.ui.screens.ResidueItemReviewScreen
import com.knoux.ai.ui.screens.SmartCleanCenterScreen
import com.knoux.ai.ui.screens.StorageBreakdownScreen
import com.knoux.ai.ui.screens.PrivacySweepScreen
import com.knoux.ai.ui.screens.ReportsScreen
import com.knoux.ai.ui.screens.StorageAnalyzerScreen
import com.knoux.ai.ui.screens.HubService
import com.knoux.ai.ui.screens.ServiceHubScreen
import com.knoux.ai.ui.screens.SettingsScreen
import com.knoux.ai.ui.screens.WelcomeScreen
import com.knoux.ai.ui.components.LocalOpenSettings
import com.knoux.ai.ui.theme.KnouxTheme
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.R
import kotlinx.coroutines.delay

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnouxTheme {
                // The visual references are LTR English screens. Keep layout
                // direction stable until a real Arabic/RTL localization pass
                // (with translated strings) is added.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    KnouxApp(activity = this@MainActivity)
                }
            }
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_CLEAN = "clean"
private const val ROUTE_TOOLS = "tools"
private const val ROUTE_REPORTS = "reports"
private const val SETTINGS_ROUTE = "settings"

@Composable
fun KnouxApp(activity: Activity? = null) {
    var splashDone by rememberSaveable { mutableStateOf(false) }
    var welcomeDone by rememberSaveable { mutableStateOf(false) }
    var accountInfoVisible by remember { mutableStateOf(false) }
    val nav = rememberNavController()
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    LaunchedEffect(route, splashDone) {
        if (!splashDone) {
            delay(2600)
            RuntimeProbe.capture(activity, "00_splash")
        } else {
            delay(900)
            RuntimeProbe.capture(activity, route ?: "unknown")
        }
    }

    // Keep the bottom navigation in sync with detail routes. The references
    // show Clean active while the Smart Clean Center is open, Tools active for
    // storage/privacy/battery details, and Reports active for report details.
    LaunchedEffect(route) {
        tab = when (route) {
            ROUTE_HOME -> MainTab.Home
            ROUTE_CLEAN, "smartclean", "deepscan", "duplicates", "residue",
            "duplicate-group", "deep-scan-summary", "residue-item" -> MainTab.Clean
            ROUTE_TOOLS, "storage", "appmanager", "privacy", "battery", "ai",
            "storage-breakdown", "installed-apps", "battery-diagnostics",
            "ai-recommendations", "privacy-risk" -> MainTab.Tools
            ROUTE_REPORTS, "performance-report" -> MainTab.Reports
            else -> MainTab.Home
        }
    }

    // Debug-only visual QA bridge. The debug application polls the loopback dev
    // server and posts routes here. Release builds have no poller, so this
    // collector stays idle and normal user navigation is untouched.
    LaunchedEffect(splashDone) {
        if (!splashDone) return@LaunchedEffect
        QaCommandBus.commands.collect { raw ->
            val command = raw.trim()
            if (command.isEmpty()) return@collect
            // The visual QA bridge drives the app directly, so the welcome
            // gate must not block automated navigation.
            welcomeDone = true
            if (command == "back") {
                nav.popBackStack()
                delay(1100)
                RuntimeProbe.capture(activity, "qa_back")
            } else {
                val target = when (command) {
                    "home" -> ROUTE_HOME
                    "clean" -> ROUTE_CLEAN
                    "tools" -> ROUTE_TOOLS
                    "reports" -> ROUTE_REPORTS
                    else -> command
                }
                nav.navigate(target) { launchSingleTop = true }
                delay(1100)
                val safe = target.replace(Regex("[^A-Za-z0-9._-]"), "_")
                RuntimeProbe.capture(activity, "qa_$safe")
            }
        }
    }

    CompositionLocalProvider(
        LocalOpenSettings provides { nav.navigate(SETTINGS_ROUTE) },
    ) {
    Box(Modifier.fillMaxSize()) {
        KnouxBackground()
        Crossfade(
            targetState = when {
                !splashDone -> 0
                !welcomeDone -> 1
                else -> 2
            },
            animationSpec = tween(420),
            label = "splash-handoff",
        ) { stage ->
        if (stage == 0) {
            KnouxSplashScreen(onFinished = { splashDone = true })
        } else if (stage == 1) {
            WelcomeScreen(
                onContinueAsGuest = { welcomeDone = true },
                onOpenAccountInfo = { accountInfoVisible = true },
            )
        } else {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    KnouxBottomBar(
                        selected = tab,
                        onSelect = { selected ->
                            tab = selected
                            nav.switchTab(selected)
                        },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            },
        ) { inner ->
            Column(Modifier.fillMaxSize()) {
            // ONE global header owned by the navigation level. No screen draws
            // its own, which removes the duplicated logo / Pro badge / settings
            // icon / back arrow defect at its compositional root cause.
            KnouxAppHeader(
                leading = if (route == ROUTE_HOME) TopLeading.Menu else TopLeading.Back,
                onLeadingClick = {
                    when (route) {
                        ROUTE_HOME -> nav.switchTab(MainTab.Clean)
                        ROUTE_CLEAN, ROUTE_TOOLS, ROUTE_REPORTS -> nav.switchTab(MainTab.Home)
                        else -> nav.backToHub(hubFor(route))
                    }
                },
                accent = accentFor(route),
                right = { SettingsIconButton() },
            )
            NavHost(
                navController = nav,
                startDestination = ROUTE_HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                enterTransition = {
                    fadeIn(tween(300)) + scaleIn(initialScale = 0.985f, animationSpec = tween(300))
                },
                exitTransition = { fadeOut(tween(190)) },
                popEnterTransition = { fadeIn(tween(280)) },
                popExitTransition = { fadeOut(tween(220)) + scaleOut(targetScale = 1.015f) },
            ) {
                composable(ROUTE_HOME) { HomeScreen(onNavigate = { nav.navigate(it) }) }
                composable(ROUTE_CLEAN) {
                    ServiceHubScreen(
                        title = "Smart ",
                        accent = "Clean",
                        subtitle = "Junk, duplicates and residue — every step previewed before anything is touched.",
                        featured = HubService(
                            title = "Smart Clean Center",
                            subtitle = "Review each junk category with its real size, then clean only what you confirm.",
                            route = "smartclean",
                            art = R.drawable.knoux_home_icon_clean,
                            accent = ProArcticCyan,
                            value = "2.86 GB",
                            valueLabel = "junk found",
                        ),
                        services = listOf(
                            HubService(
                                title = "Deep Scan",
                                subtitle = "Full device scan across junk, duplicates, privacy and health.",
                                route = "deepscan",
                                art = R.drawable.knoux_svc_deepscan,
                                accent = ProArcticCyan,
                                value = "12",
                                valueLabel = "modules",
                            ),
                            HubService(
                                title = "Duplicate Files Finder",
                                subtitle = "Hash-verified duplicate photos, video, audio and documents.",
                                route = "duplicates",
                                art = R.drawable.knoux_svc_duplicates,
                                accent = ProBlue,
                                value = "1,248",
                                valueLabel = "duplicates",
                            ),
                            HubService(
                                title = "App Residue Cleanup",
                                subtitle = "Leftovers and empty folders from apps you already removed.",
                                route = "residue",
                                art = R.drawable.knoux_svc_residue,
                                accent = ProArcticCyan,
                                value = "1.9 GB",
                                valueLabel = "reclaimable",
                            ),
                        ),
                        onOpen = { nav.navigate(it) },
                        onBack = { nav.switchTab(MainTab.Home) },
                    )
                }
                composable(ROUTE_TOOLS) {
                    ServiceHubScreen(
                        title = "Device ",
                        accent = "Tools",
                        subtitle = "Storage, apps, privacy, battery and performance — each with its own workspace.",
                        featured = HubService(
                            title = "Storage Analyzer",
                            subtitle = "See where every byte goes, what is reclaimable and which folders dominate.",
                            route = "storage",
                            art = R.drawable.knoux_svc_storage,
                            accent = ProArcticCyan,
                            value = "128 GB",
                            valueLabel = "used of 256 GB",
                        ),
                        services = listOf(
                            HubService(
                                title = "App Manager",
                                subtitle = "Installed apps, heavy weights and rarely used apps.",
                                route = "appmanager",
                                art = R.drawable.knoux_svc_apps,
                                accent = ProBlue,
                                value = "41",
                                valueLabel = "apps listed",
                            ),
                            HubService(
                                title = "Privacy Sweep",
                                subtitle = "Permission evidence and exposure signals for every app.",
                                route = "privacy",
                                art = R.drawable.knoux_svc_privacy,
                                accent = ProArcticCyan,
                                value = "7",
                                valueLabel = "trackers found",
                            ),
                            HubService(
                                title = "Battery Saver",
                                subtitle = "Reduce drain, control background work and keep the device cool.",
                                route = "battery",
                                art = R.drawable.knoux_svc_battery,
                                accent = ProArcticCyan,
                                value = "6h 42m",
                                valueLabel = "estimated gain",
                            ),
                            HubService(
                                title = "AI Optimization",
                                subtitle = "Memory, startup and thermal tuning with measured follow-up.",
                                route = "ai",
                                art = R.drawable.knoux_svc_ai,
                                accent = ProBlue,
                                value = "94",
                                valueLabel = "performance score",
                            ),
                        ),
                        onOpen = { nav.navigate(it) },
                        onBack = { nav.switchTab(MainTab.Home) },
                    )
                }
                composable(ROUTE_REPORTS) {
                    ReportsScreen(
                        onBack = { nav.switchTab(MainTab.Home) },
                        onOpenDetail = { nav.navigate("performance-report") },
                    )
                }
                composable("privacy") {
                    PrivacySweepScreen(
                        onBack = { nav.backToHub(ROUTE_TOOLS) },
                        onOpenDetail = { nav.navigate("privacy-risk") },
                    )
                }
                composable("duplicates") {
                    DuplicateFilesScreen(
                        onBack = { nav.backToHub(ROUTE_CLEAN) },
                    )
                }
                composable("deepscan") {
                    DeepScanScreen(
                        onBack = { nav.backToHub(ROUTE_CLEAN) },
                        onOpenSummary = { nav.navigate("deep-scan-summary") },
                    )
                }
                composable("residue") {
                    AppResidueScreen(
                        onBack = { nav.backToHub(ROUTE_CLEAN) },
                        onOpenDetail = { nav.navigate("residue-item") },
                    )
                }
                composable("appmanager") {
                    AppManagerScreen(
                        onBack = { nav.backToHub(ROUTE_TOOLS) },
                        onOpenDetail = { nav.navigate("installed-apps") },
                    )
                }
                composable("storage") {
                    StorageAnalyzerScreen(
                        onBack = { nav.backToHub(ROUTE_TOOLS) },
                        onOpenDetail = { nav.navigate("storage-breakdown") },
                    )
                }
                composable("battery") {
                    BatterySaverScreen(
                        onBack = { nav.backToHub(ROUTE_TOOLS) },
                        onOpenDetail = { nav.navigate("battery-diagnostics") },
                    )
                }
                composable("ai") {
                    AiOptimizationScreen(
                        onBack = { nav.backToHub(ROUTE_TOOLS) },
                        onOpenDetail = { nav.navigate("ai-recommendations") },
                    )
                }

                composable("smartclean") {
                    SmartCleanCenterScreen(
                        onBack = { nav.backToHub(ROUTE_CLEAN) },
                        onOpenDeepScan = { nav.navigate("deepscan") },
                    )
                }
                composable(SETTINGS_ROUTE) {
                    SettingsScreen(
                        onBack = { nav.backToHub(ROUTE_HOME) },
                        onShowWelcome = {
                            welcomeDone = false
                            nav.popBackStack()
                        },
                    )
                }
                composable("privacy-risk") { PrivacyRiskInspectorScreen(onBack = { nav.backToHub(ROUTE_TOOLS) }) }
                composable("duplicate-group") { DuplicateGroupReviewScreen(onBack = { nav.backToHub(ROUTE_CLEAN) }) }
                composable("deep-scan-summary") {
                    DeepScanSummaryScreen(
                        onBack = { nav.backToHub(ROUTE_CLEAN) },
                        onOpenSmartClean = { nav.navigate("smartclean") },
                    )
                }
                composable("performance-report") { PerformanceReportDetailScreen(onBack = { nav.backToHub(ROUTE_REPORTS) }) }
                composable("residue-item") { ResidueItemReviewScreen(onBack = { nav.backToHub(ROUTE_CLEAN) }) }
                composable("installed-apps") { InstalledAppsReviewScreen(onBack = { nav.backToHub(ROUTE_TOOLS) }) }
                composable("storage-breakdown") { StorageBreakdownScreen(onBack = { nav.backToHub(ROUTE_TOOLS) }) }
                composable("battery-diagnostics") { BatteryDiagnosticsScreen(onBack = { nav.backToHub(ROUTE_TOOLS) }) }
                composable("ai-recommendations") { AiRecommendationsScreen(onBack = { nav.backToHub(ROUTE_TOOLS) }) }
            }
            }
        }
        }
    }
    }
    }

    if (accountInfoVisible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { accountInfoVisible = false },
            title = { androidx.compose.material3.Text("KNOUX ID") },
            text = {
                androidx.compose.material3.Text(
                    "Account sync is not connected in this build. Guest mode already runs every tool on this phone with no account, no upload and no tracking. When cloud sync ships, sign-in will appear here."
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { accountInfoVisible = false }) {
                    androidx.compose.material3.Text("Got it")
                }
            },
        )
    }
}

/** The hub a given route belongs to, used by the global back affordance. */
private fun hubFor(route: String?): String = when (route) {
    ROUTE_CLEAN, "smartclean", "deepscan", "duplicates", "residue",
    "duplicate-group", "deep-scan-summary", "residue-item" -> ROUTE_CLEAN
    "privacy", "storage", "appmanager", "battery", "ai",
    "storage-breakdown", "installed-apps", "battery-diagnostics",
    "ai-recommendations", "privacy-risk" -> ROUTE_TOOLS
    "performance-report" -> ROUTE_REPORTS
    else -> ROUTE_HOME
}

/** Context-aware accent glow for the global header. */
private fun accentFor(route: String?): androidx.compose.ui.graphics.Color = when (route) {
    ROUTE_CLEAN, "smartclean", "deepscan", "duplicates", "residue" -> ProArcticCyan
    "privacy", "privacy-risk" -> ProArcticCyan
    "storage", "storage-breakdown", "battery", "battery-diagnostics" -> ProArcticCyan
    else -> ProBlue
}

private fun NavHostController.switchTab(tab: MainTab) {
    val route = when (tab) {
        MainTab.Home -> ROUTE_HOME
        MainTab.Clean -> ROUTE_CLEAN
        MainTab.Tools -> ROUTE_TOOLS
        MainTab.Reports -> ROUTE_REPORTS
    }
    navigate(route) {
        // Saved-state stacks made the top-bar back button look dead: the
        // popped entry was restored instead of removed. Each tab now owns a
        // single, predictable stack: hub -> service -> detail.
        popUpTo(ROUTE_HOME) { inclusive = false; saveState = false }
        launchSingleTop = true
    }
}

/**
 * Back from a service or detail screen always lands on its own hub.
 *
 * Falls back to the hub route when there is nothing left to pop, so the top
 * bar arrow can never dead-end.
 */
private fun NavHostController.backToHub(hub: String) {
    if (!popBackStack()) {
        navigate(hub) {
            popUpTo(ROUTE_HOME) { inclusive = false; saveState = false }
            launchSingleTop = true
        }
    }
}
