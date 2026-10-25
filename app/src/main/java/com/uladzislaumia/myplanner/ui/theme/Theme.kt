package com.uladzislaumia.myplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.uladzislaumia.myplanner.R

@Composable
fun MyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = warmColorScheme(),
        typography = Typography,
        content = content,
    )
}

/**
 * Color scheme from color resources: values-night holds the dark variants, so it follows the system theme.
 * Dynamic (wallpaper) colors aren't used, they would clash with the app's warm palette.
 */
@Composable
private fun warmColorScheme(): ColorScheme {
    // Roles without a resource (error, inverse, scrim) keep the Material baseline of the current theme
    val baseline = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    val primary = colorResource(R.color.md_primary)
    return baseline.copy(
        primary = primary,
        onPrimary = colorResource(R.color.md_on_primary),
        primaryContainer = colorResource(R.color.md_primary_container),
        onPrimaryContainer = colorResource(R.color.md_on_primary_container),
        secondary = colorResource(R.color.md_secondary),
        onSecondary = colorResource(R.color.md_on_secondary),
        secondaryContainer = colorResource(R.color.md_secondary_container),
        onSecondaryContainer = colorResource(R.color.md_on_secondary_container),
        tertiary = colorResource(R.color.md_tertiary),
        onTertiary = colorResource(R.color.md_on_tertiary),
        tertiaryContainer = colorResource(R.color.md_tertiary_container),
        onTertiaryContainer = colorResource(R.color.md_on_tertiary_container),
        background = colorResource(R.color.md_background),
        onBackground = colorResource(R.color.md_on_background),
        surface = colorResource(R.color.md_surface),
        onSurface = colorResource(R.color.md_on_surface),
        surfaceVariant = colorResource(R.color.md_surface_variant),
        onSurfaceVariant = colorResource(R.color.md_on_surface_variant),
        surfaceTint = primary,
        outline = colorResource(R.color.md_outline),
        outlineVariant = colorResource(R.color.md_outline_variant),
        surfaceContainerLowest = colorResource(R.color.md_surface_container_lowest),
        surfaceContainerLow = colorResource(R.color.md_surface_container_low),
        surfaceContainer = colorResource(R.color.md_surface_container),
        surfaceContainerHigh = colorResource(R.color.md_surface_container_high),
        surfaceContainerHighest = colorResource(R.color.md_surface_container_highest),
    )
}
