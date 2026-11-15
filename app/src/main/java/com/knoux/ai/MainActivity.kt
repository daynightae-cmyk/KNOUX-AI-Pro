package com.knoux.ai

import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.knoux.ai.splash.KnouxSplashScreen
import com.knoux.ai.ui.theme.KnouxTheme
import com.knoux.ai.ui.v2.V2AiRecommendationsScreen
import com.knoux.ai.ui.v2.V2AiScreen
import com.knoux.ai.ui.v2.V2AppDetailScreen
import com.knoux.ai.ui.v2.V2AppManagerScreen
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2BatteryDiagnosticsScreen
import com.knoux.ai.ui.v2.V2BatteryScreen
import com.knoux.ai.ui.v2.V2BottomBar
import com.knoux.ai.ui.v2.V2CleanHubScreen
import com.knoux.ai.ui.v2.V2DeepScanScreen
import com.knoux.ai.ui.v2.V2DuplicateGroupScreen
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
import com.knoux.ai.ui.v2.V2ToolsHubScreen

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Debug-only route injection so every screen can be inspected directly with adb.
        val debugRoute = if (BuildConfig.DEBUG) intent?.getStringExtra("route") else null
        setContent {
            KnouxTheme {
                KnouxApp(startRoute = debugRoute)
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

@Composable
fun KnouxApp(startRoute: String? = null) {
    var splashDone by rememberSaveable { mutableStateOf(!startRoute.isNullOrBlank()) }
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

    Box(Modifier.fillMaxSize()) {
        V2Background()

        Crossfade(
            targetState = splashDone,
            animationSpec = tween(420),
            label = "splash-handoff",
        ) { ready ->
            if (!ready) {
                KnouxSplashScreen(onFinished = { splashDone = true })
            } else {
                Scaffold(
                    containerColor = Color.Transparent,
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
                        startRoute = startRoute,
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

    if (accountInfoVisible) {
        AlertDialog(
            onDismissRequest = { accountInfoVisible = false },
            title = { Text("KNOUX ID") },
            text = {
                Text(
                    "حسابك غير مرتبط في هذا البناء. وضع الضيف يشغّل كل الأدوات على هذا الهاتف " +
                        "بدون حساب أو رفع أو تتبّع. عند إتاحة المزامنة السحابية سيظهر تسجيل الدخول هنا."
                )
            },
            confirmButton = {
                TextButton(onClick = { accountInfoVisible = false }) {
                    Text("فهمت")
                }
            },
        )
    }
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
        composable(SETTINGS_ROUTE) {
            V2SettingsScreenV2(onBack = goBack)
        }
        composable("privacy-risk") {
            V2PrivacyRiskScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
        composable("duplicate-group") {
            V2DuplicateGroupScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
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
        composable("ai-recommendations") {
            V2AiRecommendationsScreen(onBack = goBack, onPro = { open(ROUTE_PRO) })
        }
    }
}
