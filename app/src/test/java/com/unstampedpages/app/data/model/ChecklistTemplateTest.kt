package com.unstampedpages.app.data.model

import org.junit.Assert.*
import org.junit.Test

private const val TEST_ITEM = "Test Item"

class ChecklistTemplateItemTest {

    // ==================== Constructor Tests ====================

    @Test
    fun `ChecklistTemplateItem can be created with required fields`() {
        val item = ChecklistTemplateItem(
            name = TEST_ITEM,
            category = ChecklistCategory.ELECTRONICS
        )

        assertEquals(TEST_ITEM, item.name)
        assertEquals(ChecklistCategory.ELECTRONICS, item.category)
    }

    @Test
    fun `ChecklistTemplateItem has default quantity of 1`() {
        val item = ChecklistTemplateItem(
            name = TEST_ITEM,
            category = ChecklistCategory.ELECTRONICS
        )

        assertEquals(1, item.quantity)
    }

    @Test
    fun `ChecklistTemplateItem can be created with custom quantity`() {
        val item = ChecklistTemplateItem(
            name = "Socks",
            category = ChecklistCategory.CLOTHING,
            quantity = 5
        )

        assertEquals(5, item.quantity)
    }

    // ==================== Name Tests ====================

    @Test
    fun `ChecklistTemplateItem name can be empty string`() {
        val item = ChecklistTemplateItem(
            name = "",
            category = ChecklistCategory.OTHER
        )

        assertEquals("", item.name)
    }

    @Test
    fun `ChecklistTemplateItem name can contain special characters`() {
        val item = ChecklistTemplateItem(
            name = "Phone charger (USB-C)",
            category = ChecklistCategory.ELECTRONICS
        )

        assertEquals("Phone charger (USB-C)", item.name)
    }

    @Test
    fun `ChecklistTemplateItem name can be very long`() {
        val longName = "A".repeat(100)
        val item = ChecklistTemplateItem(
            name = longName,
            category = ChecklistCategory.OTHER
        )

        assertEquals(longName, item.name)
    }

    // ==================== Category Tests ====================

    @Test
    fun `ChecklistTemplateItem can have any category`() {
        ChecklistCategory.entries.forEach { category ->
            val item = ChecklistTemplateItem(
                name = "Test",
                category = category
            )
            assertEquals(category, item.category)
        }
    }

    // ==================== Quantity Tests ====================

    @Test
    fun `ChecklistTemplateItem quantity can be 0`() {
        val item = ChecklistTemplateItem(
            name = "Test",
            category = ChecklistCategory.OTHER,
            quantity = 0
        )

        assertEquals(0, item.quantity)
    }

    @Test
    fun `ChecklistTemplateItem quantity can be large`() {
        val item = ChecklistTemplateItem(
            name = "Test",
            category = ChecklistCategory.OTHER,
            quantity = 999
        )

        assertEquals(999, item.quantity)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `ChecklistTemplateItem equality works correctly`() {
        val item1 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 1)
        val item2 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 1)
        val item3 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 2)

        assertEquals(item1, item2)
        assertNotEquals(item1, item3)
    }

    @Test
    fun `ChecklistTemplateItem equality considers all fields`() {
        val item1 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 1)
        val item2 = ChecklistTemplateItem("Test", ChecklistCategory.CLOTHING, 1)
        val item3 = ChecklistTemplateItem("Different", ChecklistCategory.ELECTRONICS, 1)

        assertNotEquals(item1, item2)
        assertNotEquals(item1, item3)
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `ChecklistTemplateItem hashCode is consistent`() {
        val item = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 2)
        val hash1 = item.hashCode()
        val hash2 = item.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `equal ChecklistTemplateItems have same hashCode`() {
        val item1 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 2)
        val item2 = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 2)

        assertEquals(item1.hashCode(), item2.hashCode())
    }

    // ==================== Copy Tests ====================

    @Test
    fun `ChecklistTemplateItem copy works correctly`() {
        val original = ChecklistTemplateItem("Original", ChecklistCategory.ELECTRONICS, 1)
        val copy = original.copy(name = "Modified")

        assertEquals("Original", original.name)
        assertEquals("Modified", copy.name)
        assertEquals(original.category, copy.category)
        assertEquals(original.quantity, copy.quantity)
    }

    @Test
    fun `ChecklistTemplateItem copy can update all fields`() {
        val original = ChecklistTemplateItem("Original", ChecklistCategory.ELECTRONICS, 1)
        val copy = original.copy(
            name = "Modified",
            category = ChecklistCategory.CLOTHING,
            quantity = 5
        )

        assertEquals("Modified", copy.name)
        assertEquals(ChecklistCategory.CLOTHING, copy.category)
        assertEquals(5, copy.quantity)
    }

    // ==================== Destructuring Tests ====================

    @Test
    fun `ChecklistTemplateItem destructuring works correctly`() {
        val item = ChecklistTemplateItem("Test", ChecklistCategory.ELECTRONICS, 3)
        val (name, category, quantity) = item

        assertEquals("Test", name)
        assertEquals(ChecklistCategory.ELECTRONICS, category)
        assertEquals(3, quantity)
    }

    // ==================== toString Tests ====================

    @Test
    fun `ChecklistTemplateItem toString contains field values`() {
        val item = ChecklistTemplateItem("Laptop", ChecklistCategory.ELECTRONICS, 1)
        val string = item.toString()

        assertTrue(string.contains("Laptop"))
        assertTrue(string.contains("ELECTRONICS"))
    }
}

class ChecklistTemplateTest {

    // ==================== Enum Entries Tests ====================

    @Test
    fun `ChecklistTemplate has 3 templates`() {
        assertEquals(3, ChecklistTemplate.entries.size)
    }

    @Test
    fun `ChecklistTemplate contains BUSINESS_TRIP`() {
        assertTrue(ChecklistTemplate.entries.contains(ChecklistTemplate.BUSINESS_TRIP))
    }

    @Test
    fun `ChecklistTemplate contains BEACH_VACATION`() {
        assertTrue(ChecklistTemplate.entries.contains(ChecklistTemplate.BEACH_VACATION))
    }

    @Test
    fun `ChecklistTemplate contains HIKING`() {
        assertTrue(ChecklistTemplate.entries.contains(ChecklistTemplate.HIKING))
    }

    // ==================== Display Name Resource ID Tests ====================

    @Test
    fun `BUSINESS_TRIP has valid displayNameResId`() {
        assertTrue(ChecklistTemplate.BUSINESS_TRIP.displayNameResId != 0)
    }

    @Test
    fun `BEACH_VACATION has valid displayNameResId`() {
        assertTrue(ChecklistTemplate.BEACH_VACATION.displayNameResId != 0)
    }

    @Test
    fun `HIKING has valid displayNameResId`() {
        assertTrue(ChecklistTemplate.HIKING.displayNameResId != 0)
    }

    @Test
    fun `all templates have valid displayNameResId`() {
        ChecklistTemplate.entries.forEach { template ->
            assertTrue(
                "Template ${template.name} should have valid displayNameResId",
                template.displayNameResId != 0
            )
        }
    }

    @Test
    fun `all displayNameResIds are unique`() {
        val resourceIds = ChecklistTemplate.entries.map { it.displayNameResId }
        assertEquals(resourceIds.size, resourceIds.distinct().size)
    }

    // ==================== Items List Tests ====================

    @Test
    fun `BUSINESS_TRIP has items`() {
        assertTrue(ChecklistTemplate.BUSINESS_TRIP.items.isNotEmpty())
    }

    @Test
    fun `BEACH_VACATION has items`() {
        assertTrue(ChecklistTemplate.BEACH_VACATION.items.isNotEmpty())
    }

    @Test
    fun `HIKING has items`() {
        assertTrue(ChecklistTemplate.HIKING.items.isNotEmpty())
    }

    @Test
    fun `all templates have at least 10 items`() {
        ChecklistTemplate.entries.forEach { template ->
            assertTrue(
                "Template ${template.name} should have at least 10 items",
                template.items.size >= 10
            )
        }
    }

    @Test
    fun `BUSINESS_TRIP has expected item count`() {
        assertEquals(17, ChecklistTemplate.BUSINESS_TRIP.items.size)
    }

    @Test
    fun `BEACH_VACATION has expected item count`() {
        assertEquals(16, ChecklistTemplate.BEACH_VACATION.items.size)
    }

    @Test
    fun `HIKING has expected item count`() {
        assertEquals(20, ChecklistTemplate.HIKING.items.size)
    }

    // ==================== Items Content Tests ====================

    @Test
    fun `BUSINESS_TRIP contains laptop`() {
        val hasLaptop = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.name.equals("Laptop", ignoreCase = true)
        }
        assertTrue(hasLaptop)
    }

    @Test
    fun `BUSINESS_TRIP contains passport`() {
        val hasPassport = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.name.equals("Passport", ignoreCase = true)
        }
        assertTrue(hasPassport)
    }

    @Test
    fun `BEACH_VACATION contains swimsuit`() {
        val hasSwimsuit = ChecklistTemplate.BEACH_VACATION.items.any {
            it.name.equals("Swimsuit", ignoreCase = true)
        }
        assertTrue(hasSwimsuit)
    }

    @Test
    fun `BEACH_VACATION contains sunscreen`() {
        val hasSunscreen = ChecklistTemplate.BEACH_VACATION.items.any {
            it.name.equals("Sunscreen", ignoreCase = true)
        }
        assertTrue(hasSunscreen)
    }

    @Test
    fun `HIKING contains hiking boots`() {
        val hasBoots = ChecklistTemplate.HIKING.items.any {
            it.name.equals("Hiking boots", ignoreCase = true)
        }
        assertTrue(hasBoots)
    }

    @Test
    fun `HIKING contains first aid kit`() {
        val hasFirstAid = ChecklistTemplate.HIKING.items.any {
            it.name.equals("First aid kit", ignoreCase = true)
        }
        assertTrue(hasFirstAid)
    }

    @Test
    fun `all templates contain phone charger`() {
        ChecklistTemplate.entries.forEach { template ->
            val hasCharger = template.items.any {
                it.name.contains("charger", ignoreCase = true) ||
                it.name.contains("Phone charger", ignoreCase = true)
            }
            assertTrue(
                "Template ${template.name} should contain a phone charger",
                hasCharger
            )
        }
    }

    // ==================== Category Distribution Tests ====================

    @Test
    fun `BUSINESS_TRIP has items in ELECTRONICS category`() {
        val hasElectronics = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.category == ChecklistCategory.ELECTRONICS
        }
        assertTrue(hasElectronics)
    }

    @Test
    fun `BUSINESS_TRIP has items in DOCUMENTS category`() {
        val hasDocuments = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.category == ChecklistCategory.DOCUMENTS
        }
        assertTrue(hasDocuments)
    }

    @Test
    fun `BUSINESS_TRIP has items in CLOTHING category`() {
        val hasClothing = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.category == ChecklistCategory.CLOTHING
        }
        assertTrue(hasClothing)
    }

    @Test
    fun `BEACH_VACATION has items in TOILETRIES category`() {
        val hasToiletries = ChecklistTemplate.BEACH_VACATION.items.any {
            it.category == ChecklistCategory.TOILETRIES
        }
        assertTrue(hasToiletries)
    }

    @Test
    fun `HIKING has items in MEDICINE category`() {
        val hasMedicine = ChecklistTemplate.HIKING.items.any {
            it.category == ChecklistCategory.MEDICINE
        }
        assertTrue(hasMedicine)
    }

    @Test
    fun `HIKING has items in SNACKS category`() {
        val hasSnacks = ChecklistTemplate.HIKING.items.any {
            it.category == ChecklistCategory.SNACKS
        }
        assertTrue(hasSnacks)
    }

    @Test
    fun `all template items have valid categories`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertNotNull(
                    "Item '${item.name}' in ${template.name} should have a category",
                    item.category
                )
                assertTrue(
                    "Item '${item.name}' in ${template.name} should have a valid category",
                    ChecklistCategory.entries.contains(item.category)
                )
            }
        }
    }

    // ==================== Quantity Tests ====================

    @Test
    fun `all template items have positive quantities`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertTrue(
                    "Item '${item.name}' in ${template.name} should have positive quantity",
                    item.quantity >= 1
                )
            }
        }
    }

    @Test
    fun `BUSINESS_TRIP dress shirts has quantity greater than 1`() {
        val dressShirts = ChecklistTemplate.BUSINESS_TRIP.items.find {
            it.name.equals("Dress shirts", ignoreCase = true)
        }
        assertNotNull(dressShirts)
        assertTrue(dressShirts!!.quantity > 1)
    }

    @Test
    fun `BEACH_VACATION swimsuit has quantity of 2`() {
        val swimsuit = ChecklistTemplate.BEACH_VACATION.items.find {
            it.name.equals("Swimsuit", ignoreCase = true)
        }
        assertNotNull(swimsuit)
        assertEquals(2, swimsuit!!.quantity)
    }

    @Test
    fun `HIKING energy bars has quantity greater than 1`() {
        val energyBars = ChecklistTemplate.HIKING.items.find {
            it.name.equals("Energy bars", ignoreCase = true)
        }
        assertNotNull(energyBars)
        assertTrue(energyBars!!.quantity > 1)
    }

    @Test
    fun `some items have default quantity of 1`() {
        ChecklistTemplate.entries.forEach { template ->
            val singleQuantityItems = template.items.filter { it.quantity == 1 }
            assertTrue(
                "Template ${template.name} should have some items with quantity 1",
                singleQuantityItems.isNotEmpty()
            )
        }
    }

    // ==================== Name Uniqueness Tests ====================

    @Test
    fun `BUSINESS_TRIP items have unique names`() {
        val names = ChecklistTemplate.BUSINESS_TRIP.items.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `BEACH_VACATION items have unique names`() {
        val names = ChecklistTemplate.BEACH_VACATION.items.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `HIKING items have unique names`() {
        val names = ChecklistTemplate.HIKING.items.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    // ==================== valueOf Tests ====================

    @Test
    fun `valueOf BUSINESS_TRIP returns BUSINESS_TRIP`() {
        assertEquals(ChecklistTemplate.BUSINESS_TRIP, ChecklistTemplate.valueOf("BUSINESS_TRIP"))
    }

    @Test
    fun `valueOf BEACH_VACATION returns BEACH_VACATION`() {
        assertEquals(ChecklistTemplate.BEACH_VACATION, ChecklistTemplate.valueOf("BEACH_VACATION"))
    }

    @Test
    fun `valueOf HIKING returns HIKING`() {
        assertEquals(ChecklistTemplate.HIKING, ChecklistTemplate.valueOf("HIKING"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with invalid name throws IllegalArgumentException`() {
        ChecklistTemplate.valueOf("INVALID")
    }

    // ==================== Ordinal Tests ====================

    @Test
    fun `BUSINESS_TRIP has ordinal 0`() {
        assertEquals(0, ChecklistTemplate.BUSINESS_TRIP.ordinal)
    }

    @Test
    fun `BEACH_VACATION has ordinal 1`() {
        assertEquals(1, ChecklistTemplate.BEACH_VACATION.ordinal)
    }

    @Test
    fun `HIKING has ordinal 2`() {
        assertEquals(2, ChecklistTemplate.HIKING.ordinal)
    }

    // ==================== Name Property Tests ====================

    @Test
    fun `name property returns correct values`() {
        assertEquals("BUSINESS_TRIP", ChecklistTemplate.BUSINESS_TRIP.name)
        assertEquals("BEACH_VACATION", ChecklistTemplate.BEACH_VACATION.name)
        assertEquals("HIKING", ChecklistTemplate.HIKING.name)
    }

    // ==================== Collection Operations Tests ====================

    @Test
    fun `templates can be filtered by item count`() {
        val largeTemplates = ChecklistTemplate.entries.filter { it.items.size >= 17 }
        assertTrue(largeTemplates.isNotEmpty())
    }

    @Test
    fun `templates can be mapped to item counts`() {
        val counts = ChecklistTemplate.entries.map { it.items.size }
        assertEquals(3, counts.size)
        assertTrue(counts.all { it > 0 })
    }

    @Test
    fun `templates can be used in when expression`() {
        ChecklistTemplate.entries.forEach { template ->
            val description = when (template) {
                ChecklistTemplate.BUSINESS_TRIP -> "For work travel"
                ChecklistTemplate.BEACH_VACATION -> "For beach trips"
                ChecklistTemplate.HIKING -> "For outdoor adventures"
            }
            assertTrue(description.isNotEmpty())
        }
    }

    @Test
    fun `total items across all templates`() {
        val totalItems = ChecklistTemplate.entries.sumOf { it.items.size }
        assertTrue(totalItems >= 50)
    }

    // ==================== Template Item Validation Tests ====================

    @Test
    fun `all items have non-empty names`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertTrue(
                    "Item in ${template.name} should have non-empty name",
                    item.name.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `no items have excessive quantities`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertTrue(
                    "Item '${item.name}' in ${template.name} should not have excessive quantity",
                    item.quantity <= 10
                )
            }
        }
    }
}
