package com.unstampedpages.app.util

import android.view.HapticFeedbackConstants
import android.view.View

@Suppress("DEPRECATION")
object HapticUtils {

    fun performCheckHaptic(view: View?) {
        view?.performHapticFeedback(
            HapticFeedbackConstants.CONFIRM,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    fun performLongPressHaptic(view: View?) {
        view?.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}
