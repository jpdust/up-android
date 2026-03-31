package com.unstampedpages.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Color.kt color definitions.
 * Verifies color values and relationships between light/dark mode colors.
 */
class ColorTest {

    // ==================== Primary Colors Tests ====================

    @Test
    fun `Primary color is leather brown`() {
        assertEquals(Color(0xFF6B4423), Primary)
    }

    @Test
    fun `PrimaryDark is darker than Primary`() {
        assertTrue(getPrimaryLuminance(PrimaryDark) < getPrimaryLuminance(Primary))
    }

    @Test
    fun `PrimaryLight is lighter than Primary`() {
        assertTrue(getPrimaryLuminance(PrimaryLight) > getPrimaryLuminance(Primary))
    }

    @Test
    fun `Primary color has correct hex value`() {
        assertEquals(0xFF6B4423.toInt(), Primary.value.shr(32).toInt())
    }

    @Test
    fun `PrimaryDark color has correct hex value`() {
        assertEquals(Color(0xFF4A2F18), PrimaryDark)
    }

    @Test
    fun `PrimaryLight color has correct hex value`() {
        assertEquals(Color(0xFF8B6243), PrimaryLight)
    }

    // ==================== Secondary Colors Tests ====================

    @Test
    fun `Secondary color is antique gold`() {
        assertEquals(Color(0xFFD4A843), Secondary)
    }

    @Test
    fun `SecondaryDark is darker than Secondary`() {
        assertTrue(getPrimaryLuminance(SecondaryDark) < getPrimaryLuminance(Secondary))
    }

    @Test
    fun `SecondaryLight is lighter than Secondary`() {
        assertTrue(getPrimaryLuminance(SecondaryLight) > getPrimaryLuminance(Secondary))
    }

    @Test
    fun `SecondaryDark color has correct hex value`() {
        assertEquals(Color(0xFFB8922E), SecondaryDark)
    }

    @Test
    fun `SecondaryLight color has correct hex value`() {
        assertEquals(Color(0xFFE8C76B), SecondaryLight)
    }

    // ==================== Background Colors Tests ====================

    @Test
    fun `Background color is parchment`() {
        assertEquals(Color(0xFFF4E4BC), Background)
    }

    @Test
    fun `BackgroundDark is dark brown`() {
        assertEquals(Color(0xFF2D1810), BackgroundDark)
    }

    @Test
    fun `BackgroundDark is significantly darker than Background`() {
        val bgLuminance = getRelativeLuminance(Background)
        val bgDarkLuminance = getRelativeLuminance(BackgroundDark)
        assertTrue("BackgroundDark should be much darker", bgDarkLuminance < bgLuminance * 0.1)
    }

    @Test
    fun `Surface color is lighter than Background`() {
        assertEquals(Color(0xFFFDF8E8), Surface)
        assertTrue(getPrimaryLuminance(Surface) > getPrimaryLuminance(Background))
    }

    @Test
    fun `SurfaceDark is dark brown`() {
        assertEquals(Color(0xFF3D2820), SurfaceDark)
    }

    @Test
    fun `SurfaceDark is slightly lighter than BackgroundDark`() {
        assertTrue(getPrimaryLuminance(SurfaceDark) > getPrimaryLuminance(BackgroundDark))
    }

    // ==================== Accent Colors Tests ====================

    @Test
    fun `Accent color is deep adventure red`() {
        assertEquals(Color(0xFF8B2500), Accent)
    }

    @Test
    fun `AccentLight is lighter than Accent`() {
        assertEquals(Color(0xFFB84D30), AccentLight)
        assertTrue(getPrimaryLuminance(AccentLight) > getPrimaryLuminance(Accent))
    }

    // ==================== Light Mode Text Colors Tests ====================

    @Test
    fun `OnPrimary is white for light mode`() {
        assertEquals(Color(0xFFFFFFFF), OnPrimary)
    }

    @Test
    fun `OnSecondary is dark brown for readability on gold`() {
        assertEquals(Color(0xFF2D1810), OnSecondary)
    }

    @Test
    fun `OnBackground equals OnSecondary for consistency`() {
        assertEquals(OnSecondary, OnBackground)
    }

    @Test
    fun `OnSurface equals OnBackground for consistency`() {
        assertEquals(OnBackground, OnSurface)
    }

    // ==================== Dark Mode Text Colors Tests ====================

    @Test
    fun `OnBackgroundDark is warm bright parchment`() {
        assertEquals(Color(0xFFF5EBD7), OnBackgroundDark)
    }

    @Test
    fun `OnSurfaceDark equals OnBackgroundDark`() {
        assertEquals(OnBackgroundDark, OnSurfaceDark)
    }

    @Test
    fun `OnSurfaceVariantDark is slightly muted`() {
        assertEquals(Color(0xFFE8DCC4), OnSurfaceVariantDark)
        assertTrue(getPrimaryLuminance(OnSurfaceVariantDark) < getPrimaryLuminance(OnSurfaceDark))
    }

    @Test
    fun `OnBackgroundDark is lighter than OnBackground`() {
        assertTrue(
            "Dark mode text should be lighter than light mode text",
            getPrimaryLuminance(OnBackgroundDark) > getPrimaryLuminance(OnBackground)
        )
    }

    // ==================== Dark Mode Container Colors Tests ====================

    @Test
    fun `PrimaryContainerDark is lighter brown for visibility`() {
        assertEquals(Color(0xFF8B6243), PrimaryContainerDark)
        assertEquals(PrimaryLight, PrimaryContainerDark)
    }

    @Test
    fun `SecondaryContainerDark is full gold for visibility`() {
        assertEquals(Color(0xFFD4A843), SecondaryContainerDark)
        assertEquals(Secondary, SecondaryContainerDark)
    }

    @Test
    fun `SurfaceContainerDark is slightly lighter than SurfaceDark`() {
        assertEquals(Color(0xFF4A3828), SurfaceContainerDark)
        assertTrue(getPrimaryLuminance(SurfaceContainerDark) > getPrimaryLuminance(SurfaceDark))
    }

    @Test
    fun `SurfaceVariantDark is visible variant`() {
        assertEquals(Color(0xFF5C4838), SurfaceVariantDark)
        assertTrue(getPrimaryLuminance(SurfaceVariantDark) > getPrimaryLuminance(SurfaceDark))
    }

    // ==================== Dark Mode Outline Colors Tests ====================

    @Test
    fun `OutlineDark is gold for visibility`() {
        assertEquals(Color(0xFFD4A843), OutlineDark)
        assertEquals(Secondary, OutlineDark)
    }

    @Test
    fun `OutlineVariantDark is lighter brown`() {
        assertEquals(Color(0xFF8B6243), OutlineVariantDark)
        assertEquals(PrimaryLight, OutlineVariantDark)
    }

    // ==================== Map Colors Tests ====================

    @Test
    fun `MapLand color is muted parchment`() {
        assertEquals(Color(0xFFD4C4A8), MapLand)
    }

    @Test
    fun `MapOcean color is blue-gray`() {
        assertEquals(Color(0xFF7BA3B5), MapOcean)
    }

    @Test
    fun `MapBorder equals PrimaryDark`() {
        assertEquals(PrimaryDark, MapBorder)
    }

    @Test
    fun `MapHighlight equals Secondary`() {
        assertEquals(Secondary, MapHighlight)
    }

    // ==================== Status Colors Tests ====================

    @Test
    fun `SafetyLow is green`() {
        assertEquals(Color(0xFF4CAF50), SafetyLow)
    }

    @Test
    fun `SafetyMedium is yellow-amber`() {
        assertEquals(Color(0xFFFFC107), SafetyMedium)
    }

    @Test
    fun `SafetyHigh is orange-red`() {
        assertEquals(Color(0xFFFF5722), SafetyHigh)
    }

    @Test
    fun `Safety colors have distinct luminance values`() {
        val lowLum = getRelativeLuminance(SafetyLow)
        val medLum = getRelativeLuminance(SafetyMedium)
        val highLum = getRelativeLuminance(SafetyHigh)

        // They should all be different
        assertNotEquals(lowLum, medLum, 0.01)
        assertNotEquals(medLum, highLum, 0.01)
        assertNotEquals(lowLum, highLum, 0.01)
    }

    // ==================== Color Consistency Tests ====================

    @Test
    fun `all primary variants share same hue family`() {
        // All browns should have red > green > blue
        assertTrue(Primary.red > Primary.green)
        assertTrue(Primary.green > Primary.blue)
        assertTrue(PrimaryDark.red > PrimaryDark.green)
        assertTrue(PrimaryDark.green > PrimaryDark.blue)
        assertTrue(PrimaryLight.red > PrimaryLight.green)
        assertTrue(PrimaryLight.green > PrimaryLight.blue)
    }

    @Test
    fun `all secondary variants share same hue family`() {
        // All golds should have red > green > blue
        assertTrue(Secondary.red > Secondary.green)
        assertTrue(Secondary.green > Secondary.blue)
        assertTrue(SecondaryDark.red > SecondaryDark.green)
        assertTrue(SecondaryDark.green > SecondaryDark.blue)
        assertTrue(SecondaryLight.red > SecondaryLight.green)
        assertTrue(SecondaryLight.green > SecondaryLight.blue)
    }

    @Test
    fun `dark mode colors are not pure black`() {
        assertNotEquals(Color.Black, BackgroundDark)
        assertNotEquals(Color.Black, SurfaceDark)
        assertTrue(BackgroundDark.red > 0f)
        assertTrue(SurfaceDark.red > 0f)
    }

    @Test
    fun `light mode background is not pure white`() {
        assertNotEquals(Color.White, Background)
        assertNotEquals(Color.White, Surface)
    }

    // ==================== Helper Functions ====================

    /**
     * Simple luminance calculation based on primary color channel average.
     */
    private fun getPrimaryLuminance(color: Color): Float {
        return (color.red + color.green + color.blue) / 3f
    }

    /**
     * Calculate relative luminance according to WCAG 2.1 formula.
     * Used for contrast ratio calculations.
     */
    private fun getRelativeLuminance(color: Color): Double {
        fun linearize(channel: Float): Double {
            return if (channel <= 0.03928) {
                channel / 12.92
            } else {
                Math.pow(((channel + 0.055) / 1.055), 2.4)
            }
        }

        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }
}
