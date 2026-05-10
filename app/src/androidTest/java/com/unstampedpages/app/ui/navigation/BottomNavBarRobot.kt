package com.unstampedpages.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Robot Pattern implementation for BottomNavBar.
 *
 * Separates the "what" (test intent) from the "how" (Compose semantics).
 * Each method returns `this` to allow fluent chaining.
 *
 * Usage:
 * ```
 * composeTestRule.bottomNavBarRobot {
 *     verifyAllTabsDisplayed()
 *     verifyOnlyTabIsSelected("Home")
 *     clickTab("Checklist")
 *     verifyOnlyTabIsSelected("Checklist")
 * }
 * ```
 */
class BottomNavBarRobot(private val rule: ComposeTestRule) {

    // ==================== Visibility ====================

    fun verifyTabDisplayed(label: String): BottomNavBarRobot {
        rule.onNodeWithText(label).assertIsDisplayed()
        return this
    }

    fun verifyAllTabsDisplayed(labels: List<String>): BottomNavBarRobot {
        labels.forEach { verifyTabDisplayed(it) }
        return this
    }

    // ==================== Selection state ====================

    /**
     * Asserts that the tab identified by [label] carries the `selected = true` semantic,
     * which Material 3 NavigationBarItem sets via Modifier.selectable(selected = ...).
     */
    fun verifyTabIsSelected(label: String): BottomNavBarRobot {
        rule.onNodeWithText(label).assertIsSelected()
        return this
    }

    fun verifyTabIsNotSelected(label: String): BottomNavBarRobot {
        rule.onNodeWithText(label).assertIsNotSelected()
        return this
    }

    /**
     * Asserts that [selectedLabel] is selected and every other label in [allLabels] is not.
     * This is the primary assertion for the "exactly one tab selected at a time" invariant.
     */
    fun verifyOnlyTabIsSelected(
        selectedLabel: String,
        allLabels: List<String>
    ): BottomNavBarRobot {
        verifyTabIsSelected(selectedLabel)
        allLabels.filter { it != selectedLabel }.forEach { verifyTabIsNotSelected(it) }
        return this
    }

    // ==================== Interactions ====================

    fun clickTab(label: String): BottomNavBarRobot {
        rule.onNodeWithText(label).performClick()
        rule.waitForIdle()
        return this
    }

    // ==================== DSL entry point ====================

    companion object {
        fun ComposeTestRule.bottomNavBarRobot(block: BottomNavBarRobot.() -> Unit): BottomNavBarRobot =
            BottomNavBarRobot(this).apply(block)
    }
}
