package com.unstampedpages.app.data.model

import com.unstampedpages.app.R
import org.junit.Assert.*
import org.junit.Test

class ChecklistTemplateItemTest {

    // ==================== Constructor Tests ====================

    @Test
    fun `ChecklistTemplateItem can be created with required fields`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_laptop,
            category = ChecklistCategory.ELECTRONICS
        )

        assertEquals(R.string.item_laptop, item.nameResId)
        assertEquals(ChecklistCategory.ELECTRONICS, item.category)
    }

    @Test
    fun `ChecklistTemplateItem has default quantity of 1`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_laptop,
            category = ChecklistCategory.ELECTRONICS
        )

        assertEquals(1, item.quantity)
    }

    @Test
    fun `ChecklistTemplateItem can be created with custom quantity`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_hiking_socks,
            category = ChecklistCategory.CLOTHING,
            quantity = 5
        )

        assertEquals(5, item.quantity)
    }

    // ==================== NameResId Tests ====================

    @Test
    fun `ChecklistTemplateItem nameResId is non-zero`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_passport,
            category = ChecklistCategory.DOCUMENTS
        )

        assertTrue(item.nameResId != 0)
    }

    // ==================== Category Tests ====================

    @Test
    fun `ChecklistTemplateItem can have any category`() {
        ChecklistCategory.entries.forEach { category ->
            val item = ChecklistTemplateItem(
                nameResId = R.string.item_passport,
                category = category
            )
            assertEquals(category, item.category)
        }
    }

    // ==================== Quantity Tests ====================

    @Test
    fun `ChecklistTemplateItem quantity can be 0`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_passport,
            category = ChecklistCategory.OTHER,
            quantity = 0
        )

        assertEquals(0, item.quantity)
    }

    @Test
    fun `ChecklistTemplateItem quantity can be large`() {
        val item = ChecklistTemplateItem(
            nameResId = R.string.item_passport,
            category = ChecklistCategory.OTHER,
            quantity = 999
        )

        assertEquals(999, item.quantity)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `ChecklistTemplateItem equality works correctly`() {
        val item1 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 1)
        val item2 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 1)
        val item3 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 2)

        assertEquals(item1, item2)
        assertNotEquals(item1, item3)
    }

    @Test
    fun `ChecklistTemplateItem equality considers all fields`() {
        val item1 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 1)
        val item2 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.CLOTHING, 1)
        val item3 = ChecklistTemplateItem(R.string.item_passport, ChecklistCategory.ELECTRONICS, 1)

        assertNotEquals(item1, item2)
        assertNotEquals(item1, item3)
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `ChecklistTemplateItem hashCode is consistent`() {
        val item = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 2)
        val hash1 = item.hashCode()
        val hash2 = item.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `equal ChecklistTemplateItems have same hashCode`() {
        val item1 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 2)
        val item2 = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 2)

        assertEquals(item1.hashCode(), item2.hashCode())
    }

    // ==================== Copy Tests ====================

    @Test
    fun `ChecklistTemplateItem copy works correctly`() {
        val original = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 1)
        val copy = original.copy(nameResId = R.string.item_passport)

        assertEquals(R.string.item_laptop, original.nameResId)
        assertEquals(R.string.item_passport, copy.nameResId)
        assertEquals(original.category, copy.category)
        assertEquals(original.quantity, copy.quantity)
    }

    @Test
    fun `ChecklistTemplateItem copy can update all fields`() {
        val original = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 1)
        val copy = original.copy(
            nameResId = R.string.item_passport,
            category = ChecklistCategory.DOCUMENTS,
            quantity = 5
        )

        assertEquals(R.string.item_passport, copy.nameResId)
        assertEquals(ChecklistCategory.DOCUMENTS, copy.category)
        assertEquals(5, copy.quantity)
    }

    // ==================== Destructuring Tests ====================

    @Test
    fun `ChecklistTemplateItem destructuring works correctly`() {
        val item = ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS, 3)
        val (nameResId, category, quantity) = item

        assertEquals(R.string.item_laptop, nameResId)
        assertEquals(ChecklistCategory.ELECTRONICS, category)
        assertEquals(3, quantity)
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
    fun `BUSINESS_TRIP has correct displayNameResId`() {
        assertEquals(R.string.template_business_trip, ChecklistTemplate.BUSINESS_TRIP.displayNameResId)
    }

    @Test
    fun `BEACH_VACATION has correct displayNameResId`() {
        assertEquals(R.string.template_beach_vacation, ChecklistTemplate.BEACH_VACATION.displayNameResId)
    }

    @Test
    fun `HIKING has correct displayNameResId`() {
        assertEquals(R.string.template_hiking, ChecklistTemplate.HIKING.displayNameResId)
    }

    @Test
    fun `all templates have non-zero displayNameResId`() {
        ChecklistTemplate.entries.forEach { template ->
            assertTrue(
                "Template ${template.name} should have non-zero displayNameResId",
                template.displayNameResId != 0
            )
        }
    }

    @Test
    fun `all displayNameResIds are unique`() {
        val displayNameResIds = ChecklistTemplate.entries.map { it.displayNameResId }
        assertEquals(displayNameResIds.size, displayNameResIds.distinct().size)
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
            it.nameResId == R.string.item_laptop
        }
        assertTrue(hasLaptop)
    }

    @Test
    fun `BUSINESS_TRIP contains passport`() {
        val hasPassport = ChecklistTemplate.BUSINESS_TRIP.items.any {
            it.nameResId == R.string.item_passport
        }
        assertTrue(hasPassport)
    }

    @Test
    fun `BEACH_VACATION contains swimsuit`() {
        val hasSwimsuit = ChecklistTemplate.BEACH_VACATION.items.any {
            it.nameResId == R.string.item_swimsuit
        }
        assertTrue(hasSwimsuit)
    }

    @Test
    fun `BEACH_VACATION contains sunscreen`() {
        val hasSunscreen = ChecklistTemplate.BEACH_VACATION.items.any {
            it.nameResId == R.string.item_sunscreen
        }
        assertTrue(hasSunscreen)
    }

    @Test
    fun `HIKING contains hiking boots`() {
        val hasBoots = ChecklistTemplate.HIKING.items.any {
            it.nameResId == R.string.item_hiking_boots
        }
        assertTrue(hasBoots)
    }

    @Test
    fun `HIKING contains first aid kit`() {
        val hasFirstAid = ChecklistTemplate.HIKING.items.any {
            it.nameResId == R.string.item_first_aid_kit
        }
        assertTrue(hasFirstAid)
    }

    @Test
    fun `all templates contain phone charger`() {
        ChecklistTemplate.entries.forEach { template ->
            val hasCharger = template.items.any {
                it.nameResId == R.string.item_phone_charger
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
                    "Item in ${template.name} should have a category",
                    item.category
                )
                assertTrue(
                    "Item in ${template.name} should have a valid category",
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
                    "Item in ${template.name} should have positive quantity",
                    item.quantity >= 1
                )
            }
        }
    }

    @Test
    fun `BUSINESS_TRIP dress shirts has quantity greater than 1`() {
        val dressShirts = ChecklistTemplate.BUSINESS_TRIP.items.find {
            it.nameResId == R.string.item_dress_shirts
        }
        assertNotNull(dressShirts)
        assertTrue(dressShirts!!.quantity > 1)
    }

    @Test
    fun `BEACH_VACATION swimsuit has quantity of 2`() {
        val swimsuit = ChecklistTemplate.BEACH_VACATION.items.find {
            it.nameResId == R.string.item_swimsuit
        }
        assertNotNull(swimsuit)
        assertEquals(2, swimsuit!!.quantity)
    }

    @Test
    fun `HIKING energy bars has quantity greater than 1`() {
        val energyBars = ChecklistTemplate.HIKING.items.find {
            it.nameResId == R.string.item_energy_bars
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

    // ==================== NameResId Uniqueness Tests ====================

    @Test
    fun `BUSINESS_TRIP items have unique nameResIds`() {
        val nameResIds = ChecklistTemplate.BUSINESS_TRIP.items.map { it.nameResId }
        assertEquals(nameResIds.size, nameResIds.distinct().size)
    }

    @Test
    fun `BEACH_VACATION items have unique nameResIds`() {
        val nameResIds = ChecklistTemplate.BEACH_VACATION.items.map { it.nameResId }
        assertEquals(nameResIds.size, nameResIds.distinct().size)
    }

    @Test
    fun `HIKING items have unique nameResIds`() {
        val nameResIds = ChecklistTemplate.HIKING.items.map { it.nameResId }
        assertEquals(nameResIds.size, nameResIds.distinct().size)
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
    fun `all items have non-zero nameResIds`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertTrue(
                    "Item in ${template.name} should have non-zero nameResId",
                    item.nameResId != 0
                )
            }
        }
    }

    @Test
    fun `no items have excessive quantities`() {
        ChecklistTemplate.entries.forEach { template ->
            template.items.forEach { item ->
                assertTrue(
                    "Item in ${template.name} should not have excessive quantity",
                    item.quantity <= 10
                )
            }
        }
    }
}
