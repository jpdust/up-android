package com.unstampedpages.app.data.model

import org.junit.Assert.*
import org.junit.Test

class ChecklistCategoryTest {

    // ==================== Enum Entries Tests ====================

    @Test
    fun `ChecklistCategory has 8 categories`() {
        assertEquals(8, ChecklistCategory.entries.size)
    }

    @Test
    fun `ChecklistCategory contains ELECTRONICS`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.ELECTRONICS))
    }

    @Test
    fun `ChecklistCategory contains TOILETRIES`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.TOILETRIES))
    }

    @Test
    fun `ChecklistCategory contains CLOTHING`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.CLOTHING))
    }

    @Test
    fun `ChecklistCategory contains DOCUMENTS`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.DOCUMENTS))
    }

    @Test
    fun `ChecklistCategory contains MEDICINE`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.MEDICINE))
    }

    @Test
    fun `ChecklistCategory contains ACCESSORIES`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.ACCESSORIES))
    }

    @Test
    fun `ChecklistCategory contains SNACKS`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.SNACKS))
    }

    @Test
    fun `ChecklistCategory contains OTHER`() {
        assertTrue(ChecklistCategory.entries.contains(ChecklistCategory.OTHER))
    }

    // ==================== Display Name Tests ====================

    @Test
    fun `ELECTRONICS has correct displayName`() {
        assertEquals("Electronics", ChecklistCategory.ELECTRONICS.displayName)
    }

    @Test
    fun `TOILETRIES has correct displayName`() {
        assertEquals("Toiletries", ChecklistCategory.TOILETRIES.displayName)
    }

    @Test
    fun `CLOTHING has correct displayName`() {
        assertEquals("Clothing", ChecklistCategory.CLOTHING.displayName)
    }

    @Test
    fun `DOCUMENTS has correct displayName`() {
        assertEquals("Documents", ChecklistCategory.DOCUMENTS.displayName)
    }

    @Test
    fun `MEDICINE has correct displayName`() {
        assertEquals("Medicine", ChecklistCategory.MEDICINE.displayName)
    }

    @Test
    fun `ACCESSORIES has correct displayName`() {
        assertEquals("Accessories", ChecklistCategory.ACCESSORIES.displayName)
    }

    @Test
    fun `SNACKS has correct displayName`() {
        assertEquals("Snacks", ChecklistCategory.SNACKS.displayName)
    }

    @Test
    fun `OTHER has correct displayName`() {
        assertEquals("Other", ChecklistCategory.OTHER.displayName)
    }

    @Test
    fun `all categories have non-empty displayName`() {
        ChecklistCategory.entries.forEach { category ->
            assertTrue(
                "Category ${category.name} should have non-empty displayName",
                category.displayName.isNotBlank()
            )
        }
    }

    @Test
    fun `all displayNames are unique`() {
        val displayNames = ChecklistCategory.entries.map { it.displayName }
        assertEquals(
            "All display names should be unique",
            displayNames.size,
            displayNames.distinct().size
        )
    }

    // ==================== Sort Order Tests ====================

    @Test
    fun `ELECTRONICS has sortOrder 0`() {
        assertEquals(0, ChecklistCategory.ELECTRONICS.sortOrder)
    }

    @Test
    fun `TOILETRIES has sortOrder 1`() {
        assertEquals(1, ChecklistCategory.TOILETRIES.sortOrder)
    }

    @Test
    fun `CLOTHING has sortOrder 2`() {
        assertEquals(2, ChecklistCategory.CLOTHING.sortOrder)
    }

    @Test
    fun `DOCUMENTS has sortOrder 3`() {
        assertEquals(3, ChecklistCategory.DOCUMENTS.sortOrder)
    }

    @Test
    fun `MEDICINE has sortOrder 4`() {
        assertEquals(4, ChecklistCategory.MEDICINE.sortOrder)
    }

    @Test
    fun `ACCESSORIES has sortOrder 5`() {
        assertEquals(5, ChecklistCategory.ACCESSORIES.sortOrder)
    }

    @Test
    fun `SNACKS has sortOrder 6`() {
        assertEquals(6, ChecklistCategory.SNACKS.sortOrder)
    }

    @Test
    fun `OTHER has sortOrder 7`() {
        assertEquals(7, ChecklistCategory.OTHER.sortOrder)
    }

    @Test
    fun `all categories have unique sortOrder`() {
        val sortOrders = ChecklistCategory.entries.map { it.sortOrder }
        assertEquals(
            "All sort orders should be unique",
            sortOrders.size,
            sortOrders.distinct().size
        )
    }

    @Test
    fun `categories can be sorted by sortOrder`() {
        val sorted = ChecklistCategory.entries.sortedBy { it.sortOrder }

        assertEquals(ChecklistCategory.ELECTRONICS, sorted[0])
        assertEquals(ChecklistCategory.TOILETRIES, sorted[1])
        assertEquals(ChecklistCategory.CLOTHING, sorted[2])
        assertEquals(ChecklistCategory.DOCUMENTS, sorted[3])
        assertEquals(ChecklistCategory.MEDICINE, sorted[4])
        assertEquals(ChecklistCategory.ACCESSORIES, sorted[5])
        assertEquals(ChecklistCategory.SNACKS, sorted[6])
        assertEquals(ChecklistCategory.OTHER, sorted[7])
    }

    @Test
    fun `OTHER has the highest sortOrder`() {
        val maxSortOrder = ChecklistCategory.entries.maxOf { it.sortOrder }
        assertEquals(ChecklistCategory.OTHER.sortOrder, maxSortOrder)
    }

    // ==================== Icon Tests ====================

    @Test
    fun `all categories have an icon`() {
        ChecklistCategory.entries.forEach { category ->
            assertNotNull(
                "Category ${category.name} should have an icon",
                category.icon
            )
        }
    }

    // ==================== valueOf Tests ====================

    @Test
    fun `valueOf ELECTRONICS returns ELECTRONICS`() {
        assertEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.valueOf("ELECTRONICS"))
    }

    @Test
    fun `valueOf TOILETRIES returns TOILETRIES`() {
        assertEquals(ChecklistCategory.TOILETRIES, ChecklistCategory.valueOf("TOILETRIES"))
    }

    @Test
    fun `valueOf CLOTHING returns CLOTHING`() {
        assertEquals(ChecklistCategory.CLOTHING, ChecklistCategory.valueOf("CLOTHING"))
    }

    @Test
    fun `valueOf DOCUMENTS returns DOCUMENTS`() {
        assertEquals(ChecklistCategory.DOCUMENTS, ChecklistCategory.valueOf("DOCUMENTS"))
    }

    @Test
    fun `valueOf MEDICINE returns MEDICINE`() {
        assertEquals(ChecklistCategory.MEDICINE, ChecklistCategory.valueOf("MEDICINE"))
    }

    @Test
    fun `valueOf ACCESSORIES returns ACCESSORIES`() {
        assertEquals(ChecklistCategory.ACCESSORIES, ChecklistCategory.valueOf("ACCESSORIES"))
    }

    @Test
    fun `valueOf SNACKS returns SNACKS`() {
        assertEquals(ChecklistCategory.SNACKS, ChecklistCategory.valueOf("SNACKS"))
    }

    @Test
    fun `valueOf OTHER returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.valueOf("OTHER"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with invalid name throws IllegalArgumentException`() {
        ChecklistCategory.valueOf("INVALID")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with lowercase name throws IllegalArgumentException`() {
        ChecklistCategory.valueOf("electronics")
    }

    // ==================== fromName Tests ====================

    @Test
    fun `fromName ELECTRONICS returns ELECTRONICS`() {
        assertEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.fromName("ELECTRONICS"))
    }

    @Test
    fun `fromName lowercase returns correct category`() {
        assertEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.fromName("electronics"))
    }

    @Test
    fun `fromName mixed case returns correct category`() {
        assertEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.fromName("Electronics"))
    }

    @Test
    fun `fromName TOILETRIES returns TOILETRIES`() {
        assertEquals(ChecklistCategory.TOILETRIES, ChecklistCategory.fromName("toiletries"))
    }

    @Test
    fun `fromName CLOTHING returns CLOTHING`() {
        assertEquals(ChecklistCategory.CLOTHING, ChecklistCategory.fromName("Clothing"))
    }

    @Test
    fun `fromName DOCUMENTS returns DOCUMENTS`() {
        assertEquals(ChecklistCategory.DOCUMENTS, ChecklistCategory.fromName("DOCUMENTS"))
    }

    @Test
    fun `fromName MEDICINE returns MEDICINE`() {
        assertEquals(ChecklistCategory.MEDICINE, ChecklistCategory.fromName("medicine"))
    }

    @Test
    fun `fromName ACCESSORIES returns ACCESSORIES`() {
        assertEquals(ChecklistCategory.ACCESSORIES, ChecklistCategory.fromName("ACCESSORIES"))
    }

    @Test
    fun `fromName SNACKS returns SNACKS`() {
        assertEquals(ChecklistCategory.SNACKS, ChecklistCategory.fromName("snacks"))
    }

    @Test
    fun `fromName OTHER returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName("OTHER"))
    }

    @Test
    fun `fromName with invalid name returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName("INVALID"))
    }

    @Test
    fun `fromName with empty string returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName(""))
    }

    @Test
    fun `fromName with special characters returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName("@#$%"))
    }

    @Test
    fun `fromName with numbers returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName("12345"))
    }

    @Test
    fun `fromName with partial match returns OTHER`() {
        assertEquals(ChecklistCategory.OTHER, ChecklistCategory.fromName("ELECTRO"))
    }

    // ==================== Name Property Tests ====================

    @Test
    fun `name property returns correct values`() {
        assertEquals("ELECTRONICS", ChecklistCategory.ELECTRONICS.name)
        assertEquals("TOILETRIES", ChecklistCategory.TOILETRIES.name)
        assertEquals("CLOTHING", ChecklistCategory.CLOTHING.name)
        assertEquals("DOCUMENTS", ChecklistCategory.DOCUMENTS.name)
        assertEquals("MEDICINE", ChecklistCategory.MEDICINE.name)
        assertEquals("ACCESSORIES", ChecklistCategory.ACCESSORIES.name)
        assertEquals("SNACKS", ChecklistCategory.SNACKS.name)
        assertEquals("OTHER", ChecklistCategory.OTHER.name)
    }

    // ==================== Ordinal Tests ====================

    @Test
    fun `ordinal values are sequential`() {
        ChecklistCategory.entries.forEachIndexed { index, category ->
            assertEquals(index, category.ordinal)
        }
    }

    // ==================== Equality Tests ====================

    @Test
    fun `same category equals itself`() {
        assertEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.ELECTRONICS)
    }

    @Test
    fun `different categories are not equal`() {
        assertNotEquals(ChecklistCategory.ELECTRONICS, ChecklistCategory.CLOTHING)
    }

    // ==================== Collection Operations Tests ====================

    @Test
    fun `categories can be filtered`() {
        val nonOther = ChecklistCategory.entries.filter { it != ChecklistCategory.OTHER }
        assertEquals(7, nonOther.size)
        assertFalse(nonOther.contains(ChecklistCategory.OTHER))
    }

    @Test
    fun `categories can be mapped to displayNames`() {
        val names = ChecklistCategory.entries.map { it.displayName }
        assertEquals(8, names.size)
        assertTrue(names.contains("Electronics"))
        assertTrue(names.contains("Other"))
    }

    @Test
    fun `categories can be used in when expression`() {
        val results = ChecklistCategory.entries.map { category ->
            when (category) {
                ChecklistCategory.ELECTRONICS -> "tech"
                ChecklistCategory.TOILETRIES -> "hygiene"
                ChecklistCategory.CLOTHING -> "clothes"
                ChecklistCategory.DOCUMENTS -> "papers"
                ChecklistCategory.MEDICINE -> "health"
                ChecklistCategory.ACCESSORIES -> "extras"
                ChecklistCategory.SNACKS -> "food"
                ChecklistCategory.OTHER -> "misc"
            }
        }
        assertEquals(8, results.size)
        assertTrue(results.contains("tech"))
        assertTrue(results.contains("misc"))
    }
}
