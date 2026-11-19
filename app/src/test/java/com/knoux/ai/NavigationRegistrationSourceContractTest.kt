package com.knoux.ai

import com.knoux.ai.ui.v2.CleanTools
import com.knoux.ai.ui.v2.SectionHub
import com.knoux.ai.ui.v2.SectionTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Enumerates source registrations plus real catalog routes; does not run Android navigation. */
class NavigationRegistrationSourceContractTest {
    @Test
    fun everyStaticAndDynamicNavigationRouteIsRegisteredExactlyOnce() {
        val source = mainActivitySource()
        val constants = Regex("private const val (\\w+) = \"([^\"]+)\"")
            .findAll(source).associate { it.groupValues[1] to it.groupValues[2] }
        val dynamicRoutes = mapOf(
            "cleanRoute" to CleanTools.routes,
            "hubRoute" to SectionHub.routes,
            "toolRoute" to sectionToolNavigationRoutes(),
        )
        val expressions = Regex("composable\\(([^)]+)\\)")
            .findAll(source).map { it.groupValues[1].trim() }.toList()
        dynamicRoutes.keys.forEach { variable ->
            assertEquals("Dynamic registration $variable", 1, expressions.count { it == variable })
        }
        assertTrue(source.contains("CleanTools.routes.forEach { cleanRoute ->"))
        assertTrue(source.contains("SectionHub.routes.forEach { hubRoute ->"))
        assertTrue(source.contains("sectionToolNavigationRoutes().forEach { toolRoute ->"))

        val registered = expressions.flatMap { expression ->
            when {
                expression.startsWith("\"") && expression.endsWith("\"") ->
                    listOf(expression.removeSurrounding("\""))
                expression in constants -> listOf(constants.getValue(expression))
                expression in dynamicRoutes -> dynamicRoutes.getValue(expression)
                else -> error("Unaccounted navigation registration: $expression")
            }
        }
        val duplicates = registered.groupingBy { it }.eachCount().filterValues { it != 1 }
        assertTrue("Duplicate registrations: $duplicates", duplicates.isEmpty())
        val catalogRoutes = CleanTools.routes + SectionHub.routes + SectionTools.defs.map { it.route }
        catalogRoutes.forEach { route ->
            assertEquals("Catalog route $route", 1, registered.count { it == route })
        }
    }

    @Test
    fun dedicatedBatteryAndRecommendationScreensAreExcludedFromGenericRegistration() {
        val dedicated = setOf("bat-readings", "ai-recommendations")
        assertEquals(
            SectionTools.defs.map { it.route }.filterNot { it in dedicated },
            sectionToolNavigationRoutes(),
        )
        val source = mainActivitySource()
        assertTrue(destinationBody(source, "bat-readings").contains("V2BatteryReadingsScreen("))
        assertTrue(destinationBody(source, "ai-recommendations").contains("V2AiRecommendationsScreen("))
        assertFalse(source.contains("composable(\"res-sources\")"))
        assertTrue("res-sources" in sectionToolNavigationRoutes())
    }

    @Test
    fun directDuplicateGroupLinksUseLiveDuplicatesScreenWithoutMockOrDetailLoop() {
        val source = mainActivitySource()
        val body = destinationBody(source, "duplicate-group")
        assertTrue(body.contains("V2DuplicatesScreen("))
        assertTrue(body.contains("onBack = goBack"))
        assertTrue(body.contains("onOpenDetail = {}"))
        assertTrue(body.contains("onPro = { open(ROUTE_PRO) }"))
        assertFalse(source.contains("V2DuplicateGroupScreen"))
    }

    private fun mainActivitySource() = appContractFile("src/main/java/com/knoux/ai/MainActivity.kt").readText()

    private fun destinationBody(source: String, route: String): String {
        val marker = "composable(\"$route\") {"
        assertTrue("Missing dedicated registration: $route", source.contains(marker))
        return source.substringAfter(marker).substringBefore("composable(")
    }
}
