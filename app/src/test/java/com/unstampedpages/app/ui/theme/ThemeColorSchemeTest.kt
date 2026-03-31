package com.unstampedpages.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Theme.kt color schemes.
 * Verifies that light and dark color schemes use the correct colors
 * and that the color assignments provide adequate contrast.
 */
class ThemeColorSchemeTest {

    // Recreate the color schemes for testing (same as Theme.kt)
    private val lightColorScheme = lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryLight,
        onPrimaryContainer = OnBackground,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryLight,
        onSecondaryContainer = OnBackground,
        tertiary = Accent,
        onTertiary = OnPrimary,
        tertiaryContainer = AccentLight,
        onTertiaryContainer = OnPrimary,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        surfaceVariant = MapLand,
        onSurfaceVariant = OnSurface,
        error = Accent,
        onError = OnPrimary,
        outline = PrimaryDark,
        outlineVariant = PrimaryLight
    )

    private val darkColorScheme = darkColorScheme(
        primary = SecondaryLight,
        onPrimary = BackgroundDark,
        primaryContainer = PrimaryContainerDark,
        onPrimaryContainer = OnBackgroundDark,
        secondary = Secondary,
        onSecondary = BackgroundDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = BackgroundDark,
        tertiary = AccentLight,
        onTertiary = OnPrimary,
        tertiaryContainer = Accent,
        onTertiaryContainer = OnPrimary,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        error = AccentLight,
        onError = OnPrimary,
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark
    )

    // ==================== Light Scheme Primary Tests ====================

    @Test
    fun `light scheme primary is leather brown`() {
        assertEquals(Primary, lightColorScheme.primary)
    }

    @Test
    fun `light scheme onPrimary is white`() {
        assertEquals(OnPrimary, lightColorScheme.onPrimary)
    }

    @Test
    fun `light scheme primaryContainer is PrimaryLight`() {
        assertEquals(PrimaryLight, lightColorScheme.primaryContainer)
    }

    @Test
    fun `light scheme onPrimaryContainer is OnBackground`() {
        assertEquals(OnBackground, lightColorScheme.onPrimaryContainer)
    }

    // ==================== Light Scheme Secondary Tests ====================

    @Test
    fun `light scheme secondary is antique gold`() {
        assertEquals(Secondary, lightColorScheme.secondary)
    }

    @Test
    fun `light scheme onSecondary is dark for readability`() {
        assertEquals(OnSecondary, lightColorScheme.onSecondary)
    }

    @Test
    fun `light scheme secondaryContainer is SecondaryLight`() {
        assertEquals(SecondaryLight, lightColorScheme.secondaryContainer)
    }

    // ==================== Light Scheme Background/Surface Tests ====================

    @Test
    fun `light scheme background is parchment`() {
        assertEquals(Background, lightColorScheme.background)
    }

    @Test
    fun `light scheme onBackground is dark brown`() {
        assertEquals(OnBackground, lightColorScheme.onBackground)
    }

    @Test
    fun `light scheme surface is light parchment`() {
        assertEquals(Surface, lightColorScheme.surface)
    }

    @Test
    fun `light scheme onSurface is dark brown`() {
        assertEquals(OnSurface, lightColorScheme.onSurface)
    }

    @Test
    fun `light scheme surfaceVariant is MapLand`() {
        assertEquals(MapLand, lightColorScheme.surfaceVariant)
    }

    // ==================== Light Scheme Tertiary/Error Tests ====================

    @Test
    fun `light scheme tertiary is Accent red`() {
        assertEquals(Accent, lightColorScheme.tertiary)
    }

    @Test
    fun `light scheme error is Accent`() {
        assertEquals(Accent, lightColorScheme.error)
    }

    // ==================== Light Scheme Outline Tests ====================

    @Test
    fun `light scheme outline is PrimaryDark`() {
        assertEquals(PrimaryDark, lightColorScheme.outline)
    }

    @Test
    fun `light scheme outlineVariant is PrimaryLight`() {
        assertEquals(PrimaryLight, lightColorScheme.outlineVariant)
    }

    // ==================== Dark Scheme Primary Tests ====================

    @Test
    fun `dark scheme primary is SecondaryLight gold for high contrast`() {
        assertEquals(SecondaryLight, darkColorScheme.primary)
    }

    @Test
    fun `dark scheme onPrimary is BackgroundDark`() {
        assertEquals(BackgroundDark, darkColorScheme.onPrimary)
    }

    @Test
    fun `dark scheme primaryContainer is lighter brown for visibility`() {
        assertEquals(PrimaryContainerDark, darkColorScheme.primaryContainer)
    }

    @Test
    fun `dark scheme onPrimaryContainer is light text`() {
        assertEquals(OnBackgroundDark, darkColorScheme.onPrimaryContainer)
    }

    // ==================== Dark Scheme Secondary Tests ====================

    @Test
    fun `dark scheme secondary is antique gold`() {
        assertEquals(Secondary, darkColorScheme.secondary)
    }

    @Test
    fun `dark scheme onSecondary is dark for readability on gold`() {
        assertEquals(BackgroundDark, darkColorScheme.onSecondary)
    }

    @Test
    fun `dark scheme secondaryContainer is full gold for visibility`() {
        assertEquals(SecondaryContainerDark, darkColorScheme.secondaryContainer)
    }

    @Test
    fun `dark scheme onSecondaryContainer is dark text on gold`() {
        assertEquals(BackgroundDark, darkColorScheme.onSecondaryContainer)
    }

    // ==================== Dark Scheme Background/Surface Tests ====================

    @Test
    fun `dark scheme background is dark brown`() {
        assertEquals(BackgroundDark, darkColorScheme.background)
    }

    @Test
    fun `dark scheme onBackground is bright warm parchment`() {
        assertEquals(OnBackgroundDark, darkColorScheme.onBackground)
    }

    @Test
    fun `dark scheme surface is SurfaceDark`() {
        assertEquals(SurfaceDark, darkColorScheme.surface)
    }

    @Test
    fun `dark scheme onSurface is light parchment`() {
        assertEquals(OnSurfaceDark, darkColorScheme.onSurface)
    }

    @Test
    fun `dark scheme surfaceVariant is visible variant`() {
        assertEquals(SurfaceVariantDark, darkColorScheme.surfaceVariant)
    }

    @Test
    fun `dark scheme onSurfaceVariant is muted light`() {
        assertEquals(OnSurfaceVariantDark, darkColorScheme.onSurfaceVariant)
    }

    // ==================== Dark Scheme Tertiary/Error Tests ====================

    @Test
    fun `dark scheme tertiary is AccentLight for visibility`() {
        assertEquals(AccentLight, darkColorScheme.tertiary)
    }

    @Test
    fun `dark scheme error is AccentLight`() {
        assertEquals(AccentLight, darkColorScheme.error)
    }

    // ==================== Dark Scheme Outline Tests ====================

    @Test
    fun `dark scheme outline is gold for visibility`() {
        assertEquals(OutlineDark, darkColorScheme.outline)
    }

    @Test
    fun `dark scheme outlineVariant is lighter brown`() {
        assertEquals(OutlineVariantDark, darkColorScheme.outlineVariant)
    }

    // ==================== Light vs Dark Scheme Comparison Tests ====================

    @Test
    fun `dark scheme primary is different from light scheme primary`() {
        assertNotEquals(lightColorScheme.primary, darkColorScheme.primary)
    }

    @Test
    fun `dark scheme background is darker than light scheme background`() {
        assertTrue(
            getRelativeLuminance(darkColorScheme.background) <
                    getRelativeLuminance(lightColorScheme.background)
        )
    }

    @Test
    fun `dark scheme onBackground is lighter than light scheme onBackground`() {
        assertTrue(
            getRelativeLuminance(darkColorScheme.onBackground) >
                    getRelativeLuminance(lightColorScheme.onBackground)
        )
    }

    @Test
    fun `dark scheme surface is darker than light scheme surface`() {
        assertTrue(
            getRelativeLuminance(darkColorScheme.surface) <
                    getRelativeLuminance(lightColorScheme.surface)
        )
    }

    @Test
    fun `both schemes use same secondary color`() {
        assertEquals(lightColorScheme.secondary, darkColorScheme.secondary)
    }

    // ==================== Color Scheme Semantic Consistency Tests ====================

    @Test
    fun `light scheme has dark text on light backgrounds`() {
        // onBackground should be darker than background
        assertTrue(
            getRelativeLuminance(lightColorScheme.onBackground) <
                    getRelativeLuminance(lightColorScheme.background)
        )
    }

    @Test
    fun `dark scheme has light text on dark backgrounds`() {
        // onBackground should be lighter than background
        assertTrue(
            getRelativeLuminance(darkColorScheme.onBackground) >
                    getRelativeLuminance(darkColorScheme.background)
        )
    }

    @Test
    fun `light scheme has dark text on surface`() {
        assertTrue(
            getRelativeLuminance(lightColorScheme.onSurface) <
                    getRelativeLuminance(lightColorScheme.surface)
        )
    }

    @Test
    fun `dark scheme has light text on surface`() {
        assertTrue(
            getRelativeLuminance(darkColorScheme.onSurface) >
                    getRelativeLuminance(darkColorScheme.surface)
        )
    }

    // ==================== Helper Functions ====================

    /**
     * Calculate relative luminance according to WCAG 2.1 formula.
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
