package com.unstampedpages.app.ui.screens.countryinfo

import androidx.annotation.DrawableRes
import com.unstampedpages.app.R

object CustomFlagProvider {

    private val overrides: Map<String, Int> = mapOf(
        "xnc" to R.drawable.flag_xnc,
        "xni" to R.drawable.flag_xni,
        "xso" to R.drawable.flag_xso
    )

    @DrawableRes
    fun getFlagDrawable(countryId: String): Int? = overrides[countryId]

    fun hasCustomFlag(countryId: String): Boolean = countryId in overrides
}
