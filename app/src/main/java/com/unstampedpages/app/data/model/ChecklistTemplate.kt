package com.unstampedpages.app.data.model

data class ChecklistTemplateItem(
    val name: String,
    val category: ChecklistCategory,
    val quantity: Int = 1
)

private const val PHONE_CHARGER = "Phone charger"

enum class ChecklistTemplate(
    val displayName: String,
    val items: List<ChecklistTemplateItem>
) {
    BUSINESS_TRIP(
        displayName = "Business Trip",
        items = listOf(
            ChecklistTemplateItem("Laptop", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Laptop charger", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(PHONE_CHARGER, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Portable battery", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Business cards", ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem("Passport", ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem("Travel itinerary", ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem("Suit/Blazer", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Dress shirts", ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem("Dress pants", ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem("Dress shoes", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Belt", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Watch", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Toothbrush", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Toothpaste", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Deodorant", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Pain relievers", ChecklistCategory.MEDICINE)
        )
    ),
    BEACH_VACATION(
        displayName = "Beach Vacation",
        items = listOf(
            ChecklistTemplateItem("Swimsuit", ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem("Sunglasses", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Sun hat", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Flip flops", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Beach towel", ChecklistCategory.OTHER),
            ChecklistTemplateItem("Sunscreen", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Aloe vera gel", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Passport", ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(PHONE_CHARGER, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Camera", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Waterproof phone case", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Shorts", ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem("T-shirts", ChecklistCategory.CLOTHING, 4),
            ChecklistTemplateItem("Sandals", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Snacks", ChecklistCategory.SNACKS),
            ChecklistTemplateItem("Motion sickness pills", ChecklistCategory.MEDICINE)
        )
    ),
    HIKING(
        displayName = "Hiking Adventure",
        items = listOf(
            ChecklistTemplateItem("Backpack", ChecklistCategory.OTHER),
            ChecklistTemplateItem("Hiking boots", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Hiking socks", ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem("Water bottle", ChecklistCategory.OTHER, 2),
            ChecklistTemplateItem("Trail map", ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem("First aid kit", ChecklistCategory.MEDICINE),
            ChecklistTemplateItem("Sunscreen", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Bug spray", ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem("Rain jacket", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Flashlight", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(PHONE_CHARGER, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Portable battery", ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem("Energy bars", ChecklistCategory.SNACKS, 5),
            ChecklistTemplateItem("Trail mix", ChecklistCategory.SNACKS, 2),
            ChecklistTemplateItem("Hat", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Sunglasses", ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem("Quick-dry pants", ChecklistCategory.CLOTHING),
            ChecklistTemplateItem("Moisture-wicking shirts", ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem("Pain relievers", ChecklistCategory.MEDICINE),
            ChecklistTemplateItem("Blister bandages", ChecklistCategory.MEDICINE)
        )
    )
}
