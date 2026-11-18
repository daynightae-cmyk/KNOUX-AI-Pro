package com.knoux.ai.ui.v2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the one honest contract of the 150-service catalog: exact counts,
 * unique ids, a real route for every item we claim to support, and a written
 * reason for every item we do not.
 */
class ServiceCatalog150Test {

    @Test
    fun catalogHasExactlyOneHundredCoreAndFiftyPro() {
        assertEquals(150, ServiceCatalog150.total)
        assertEquals(100, ServiceCatalog150.coreCount)
        assertEquals(50, ServiceCatalog150.proCount)
    }

    @Test
    fun everyFamilyHasTenCoreAndFivePro() {
        assertEquals(10, ServiceCatalog150.families.size)
        ServiceCatalog150.families.forEach { family ->
            val rows = ServiceCatalog150.byFamily(family)
            assertEquals("family $family", 15, rows.size)
            assertEquals("core $family", 10, rows.filter { it.kind == CatalogKind.CORE }.size)
            assertEquals("pro $family", 5, rows.filter { it.kind == CatalogKind.PRO }.size)
        }
    }

    @Test
    fun idsAreUnique() {
        val ids = ServiceCatalog150.items.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun ordinalsAreContiguousInsideEachFamilyAndKind() {
        ServiceCatalog150.families.forEach { family ->
            listOf(CatalogKind.CORE, CatalogKind.PRO).forEach { kind ->
                val ordinals = ServiceCatalog150.byFamily(family)
                    .filter { it.kind == kind }
                    .map { it.ordinal }
                    .sorted()
                val expected = (1..ordinals.size).toList()
                assertEquals("$family/$kind", expected, ordinals)
            }
        }
    }

    @Test
    fun routedItemsCarryARouteAndBlockedItemsCarryAReason() {
        ServiceCatalog150.items.forEach { item ->
            if (item.isRouted) {
                assertTrue("${item.id} has a blank route", item.route!!.isNotBlank())
            } else {
                assertNotNull("${item.id} is blocked without a reason", item.blockReason)
                assertTrue("${item.id} reason is blank", item.blockReason!!.isNotBlank())
            }
        }
    }

    @Test
    fun everyUiSectionMapsToARealFamily() {
        val sections = listOf(
            "storage", "duplicates", "residue", "privacy", "appmanager",
            "battery", "deepscan", "smartclean", "reports", "ai",
        )
        val mapped = sections.map { SectionTools.catalogFamily(it) }.toSet()
        assertEquals(ServiceCatalog150.families.toSet(), mapped)
    }

    @Test
    fun smartCleanCatalogMatchesItsFifteenRealTools() {
        val catalog = ServiceCatalog150.byFamily("smart_clean").sortedBy { it.ordinal }
        val tools = CleanTools.defs.sortedBy { it.order.toDouble() }
        assertEquals(15, tools.size)
        assertEquals(catalog.size, tools.size)
        catalog.filter { it.isRouted }.forEach { item ->
            val tool = tools.firstOrNull { it.route == item.route }
            assertNotNull("smart_clean route missing for ${item.id}", tool)
        }
    }

    @Test
    fun coreRoutesExistInTheRealToolCatalog() {
        val realRoutes = (
            SectionTools.defs.map { it.route } + CleanTools.defs.map { it.route }
            ).toSet()
        ServiceCatalog150.items
            .filter { it.kind == CatalogKind.CORE }
            .forEach { item ->
                assertTrue(
                    "CORE ${item.id} points at a route that does not exist: ${item.route}",
                    realRoutes.contains(item.route),
                )
            }
    }

    @Test
    fun blockedItemsAreAllPro() {
        val blocked = ServiceCatalog150.items.filter { !it.isRouted }
        assertTrue(blocked.isNotEmpty())
        assertTrue(blocked.all { it.kind == CatalogKind.PRO })
    }
}
