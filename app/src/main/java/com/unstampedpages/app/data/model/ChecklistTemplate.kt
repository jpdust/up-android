package com.unstampedpages.app.data.model

import androidx.annotation.StringRes
import com.unstampedpages.app.R

data class ChecklistTemplateItem(
    @StringRes val nameResId: Int,
    val category: ChecklistCategory,
    val quantity: Int = 1
)

enum class ChecklistTemplate(
    @StringRes val displayNameResId: Int,
    val items: List<ChecklistTemplateItem>
) {
    BUSINESS_TRIP(
        displayNameResId = R.string.template_business_trip,
        items = listOf(
            ChecklistTemplateItem(R.string.item_laptop, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_laptop_charger, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_phone_charger, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_portable_battery, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_business_cards, ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(R.string.item_passport, ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(R.string.item_travel_itinerary, ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(R.string.item_suit_blazer, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_dress_shirts, ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem(R.string.item_dress_pants, ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem(R.string.item_dress_shoes, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_belt, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_watch, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_toothbrush, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_toothpaste, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_deodorant, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_pain_relievers, ChecklistCategory.MEDICINE)
        )
    ),
    BEACH_VACATION(
        displayNameResId = R.string.template_beach_vacation,
        items = listOf(
            ChecklistTemplateItem(R.string.item_swimsuit, ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem(R.string.item_sunglasses, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_sun_hat, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_flip_flops, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_beach_towel, ChecklistCategory.OTHER),
            ChecklistTemplateItem(R.string.item_sunscreen, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_aloe_vera_gel, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_passport, ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(R.string.item_phone_charger, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_camera, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_waterproof_phone_case, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_shorts, ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem(R.string.item_t_shirts, ChecklistCategory.CLOTHING, 4),
            ChecklistTemplateItem(R.string.item_sandals, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_snacks, ChecklistCategory.SNACKS),
            ChecklistTemplateItem(R.string.item_motion_sickness_pills, ChecklistCategory.MEDICINE)
        )
    ),
    HIKING(
        displayNameResId = R.string.template_hiking,
        items = listOf(
            ChecklistTemplateItem(R.string.item_backpack, ChecklistCategory.OTHER),
            ChecklistTemplateItem(R.string.item_hiking_boots, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_hiking_socks, ChecklistCategory.CLOTHING, 3),
            ChecklistTemplateItem(R.string.item_water_bottle, ChecklistCategory.OTHER, 2),
            ChecklistTemplateItem(R.string.item_trail_map, ChecklistCategory.DOCUMENTS),
            ChecklistTemplateItem(R.string.item_first_aid_kit, ChecklistCategory.MEDICINE),
            ChecklistTemplateItem(R.string.item_sunscreen, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_bug_spray, ChecklistCategory.TOILETRIES),
            ChecklistTemplateItem(R.string.item_rain_jacket, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_flashlight, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_phone_charger, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_portable_battery, ChecklistCategory.ELECTRONICS),
            ChecklistTemplateItem(R.string.item_energy_bars, ChecklistCategory.SNACKS, 5),
            ChecklistTemplateItem(R.string.item_trail_mix, ChecklistCategory.SNACKS, 2),
            ChecklistTemplateItem(R.string.item_hat, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_sunglasses, ChecklistCategory.ACCESSORIES),
            ChecklistTemplateItem(R.string.item_quick_dry_pants, ChecklistCategory.CLOTHING),
            ChecklistTemplateItem(R.string.item_moisture_wicking_shirts, ChecklistCategory.CLOTHING, 2),
            ChecklistTemplateItem(R.string.item_pain_relievers, ChecklistCategory.MEDICINE),
            ChecklistTemplateItem(R.string.item_blister_bandages, ChecklistCategory.MEDICINE)
        )
    )
}
