package com.knoux.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knoux.ai.splash.KnouxSplashScreen
import com.knoux.ai.ui.components.KnouxBackground
import com.knoux.ai.ui.components.KnouxBottomBar
import com.knoux.ai.ui.components.MainTab
import com.knoux.ai.ui.screens.AiOptimizationScreen
import com.knoux.ai.ui.screens.AppManagerScreen
import com.knoux.ai.ui.screens.AppResidueScreen
import com.knoux.ai.ui.screens.BatterySaverScreen
import com.knoux.ai.ui.screens.DeepScanScreen
import com.knoux.ai.ui.screens.DuplicateFilesScreen
import com.knoux.ai.ui.screens.HomeScreen
import com.knoux.ai.ui.screens.PrivacySweepScreen
import com.knoux.ai.ui.screens.ReportsScreen
import com.knoux.ai.ui.screens.StorageAnalyzerScreen
import com.knoux.ai.ui.screens.ToolIndexScreen
import com.knoux.ai.ui.theme.KnouxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnouxTheme {
                KnouxApp()
            }
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_CLEAN = "clean"
private const val ROUTE_TOOLS = "tools"
private const val ROUTE_REPORTS = "reports"

@Composable
fun KnouxApp() {
    var splashDone by rememberSaveable { mutableStateOf(false) }
    val nav = rememberNavController()
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }

    Box(Modifier.fillMaxSize()) {
        KnouxBackground()
        if (!splashDone) {
            KnouxSplashScreen(onFinished = { splashDone = true })
            return@Box
        }

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                Column(
                    Modifier
                        .fillMaxSize()
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
            NavHost(
                navController = nav,
                startDestination = ROUTE_HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
            ) {
                composable(ROUTE_HOME) { HomeScreen(onNavigate = { nav.navigate(it) }) }
                composable(ROUTE_CLEAN) {
                    ToolIndexScreen(
                        title = "Clean",
                        subtitle = "Deep scan, residue cleanup and duplicate review.",
                        entries = listOf(
                            "Deep Scan" to "deepscan",
                            "App Residue Cleanup" to "residue",
                            "Duplicate Files Finder" to "duplicates",
                        ),
                        onOpen = { nav.navigate(it) },
                    )
                }
                composable(ROUTE_TOOLS) {
                    ToolIndexScreen(
                        title = "Tools",
                        subtitle = "Storage, apps, privacy, battery and optimization.",
                        entries = listOf(
                            "Storage Analyzer" to "storage",
                            "App Manager" to "appmanager",
                            "Privacy Sweep" to "privacy",
                            "Battery Saver" to "battery",
                            "AI Optimization" to "ai",
                        ),
                        onOpen = { nav.navigate(it) },
                    )
                }
                composable(ROUTE_REPORTS) { ReportsScreen(onBack = { nav.popBackStack() }) }
                composable("privacy") { PrivacySweepScreen(onBack = { nav.popBackStack() }) }
                composable("duplicates") { DuplicateFilesScreen(onBack = { nav.popBackStack() }) }
                composable("deepscan") { DeepScanScreen(onBack = { nav.popBackStack() }) }
                composable("residue") { AppResidueScreen(onBack = { nav.popBackStack() }) }
                composable("appmanager") { AppManagerScreen(onBack = { nav.popBackStack() }) }
                composable("storage") { StorageAnalyzerScreen(onBack = { nav.popBackStack() }) }
                composable("battery") { BatterySaverScreen(onBack = { nav.popBackStack() }) }
                composable("ai") { AiOptimizationScreen(onBack = { nav.popBackStack() }) }
            }
        }
    }
}

private fun NavHostController.switchTab(tab: MainTab) {
    val route = when (tab) {
        MainTab.Home -> ROUTE_HOME
        MainTab.Clean -> ROUTE_CLEAN
        MainTab.Tools -> ROUTE_TOOLS
        MainTab.Reports -> ROUTE_REPORTS
    }
    navigate(route) {
        popUpTo(ROUTE_HOME) { inclusive = false; saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
