package com.naaammme.bbspace.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.SchemeNeutral
import com.google.android.material.color.utilities.SchemeTonalSpot
import com.google.android.material.color.utilities.MaterialDynamicColors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun BiliTheme(
    config: ThemeConfig = ThemeConfig(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (config.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorSchemeMode = when {
        config.useDynamicColor -> if (darkTheme) ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight
        config.themeMode == ThemeMode.SYSTEM -> ColorSchemeMode.System
        config.themeMode == ThemeMode.DARK -> ColorSchemeMode.Dark
        config.themeMode == ThemeMode.LIGHT -> ColorSchemeMode.Light
        else -> ColorSchemeMode.System
    }

    val customColors = remember(config.seedColor, darkTheme, config.isPureBlack, config.swapBaseColors) {
        if (config.useDynamicColor) {
            null
        } else {
            createMiuixColorScheme(config.seedColor, darkTheme, config.isPureBlack, config.swapBaseColors)
        }
    }

    val controller = remember(colorSchemeMode, config.seedColor, customColors) {
        ThemeController(
            colorSchemeMode = colorSchemeMode,
            keyColor = if (config.useDynamicColor) config.seedColor else null,
            lightColors = if (!darkTheme) customColors ?: lightColorScheme() else lightColorScheme(),
            darkColors = if (darkTheme) customColors ?: darkColorScheme() else darkColorScheme()
        )
    }

    ProvideAnimations(config.animationSpeed) {
        ProvidePullRefresh(config.pullRefreshDistanceDp) {
            MiuixTheme(
                controller = controller,
                content = content
            )
        }
    }
}

private fun createMiuixColorScheme(
    seedColor: Color,
    isDark: Boolean,
    isPureBlack: Boolean,
    swapBaseColors: Boolean
): Colors {
    val base = if (seedColor.isPureBlack()) {
        if (isDark) createMonochromeMiuixDarkScheme() else createMonochromeMiuixLightScheme()
    } else {
        createMiuixSchemeFromSeed(seedColor, isDark)
    }

    val withPureBlack = if (isPureBlack && isDark) {
        base.copy(
            surface = Color.Black,
            background = Color.Black,
        )
    } else base

    return if (swapBaseColors) {
        withPureBlack.copy(
            background = withPureBlack.surfaceVariant,
            onBackground = withPureBlack.onSurfaceVariantSummary,
            surface = withPureBlack.surfaceVariant,
            onSurface = withPureBlack.onSurfaceVariantSummary,
            surfaceVariant = withPureBlack.background,
            onSurfaceVariantSummary = withPureBlack.onBackground,
        )
    } else withPureBlack
}

private fun createMiuixSchemeFromSeed(seedColor: Color, isDark: Boolean): Colors {
    val hct = Hct.fromInt(seedColor.toArgb())
    val scheme = if (hct.chroma < 5.0) {
        SchemeNeutral(hct, isDark, 0.0)
    } else {
        SchemeTonalSpot(hct, isDark, 0.0)
    }
    val c = MaterialDynamicColors()

    return if (isDark) {
        darkColorScheme(
            primary = Color(c.primary().getArgb(scheme)),
            onPrimary = Color(c.onPrimary().getArgb(scheme)),
            primaryContainer = Color(c.primaryContainer().getArgb(scheme)),
            onPrimaryContainer = Color(c.onPrimaryContainer().getArgb(scheme)),
            secondary = Color(c.secondary().getArgb(scheme)),
            onSecondary = Color(c.onSecondary().getArgb(scheme)),
            secondaryContainer = Color(c.secondaryContainer().getArgb(scheme)),
            onSecondaryContainer = Color(c.onSecondaryContainer().getArgb(scheme)),
            tertiaryContainer = Color(c.tertiaryContainer().getArgb(scheme)),
            onTertiaryContainer = Color(c.onTertiaryContainer().getArgb(scheme)),
            background = Color(c.background().getArgb(scheme)),
            onBackground = Color(c.onBackground().getArgb(scheme)),
            surface = Color(c.surface().getArgb(scheme)),
            onSurface = Color(c.onSurface().getArgb(scheme)),
            surfaceVariant = Color(c.surfaceVariant().getArgb(scheme)),
            onSurfaceVariantSummary = Color(c.onSurfaceVariant().getArgb(scheme)),
            error = Color(c.error().getArgb(scheme)),
            onError = Color(c.onError().getArgb(scheme)),
            errorContainer = Color(c.errorContainer().getArgb(scheme)),
            onErrorContainer = Color(c.onErrorContainer().getArgb(scheme)),
            outline = Color(c.outline().getArgb(scheme)),
            dividerLine = Color(c.outlineVariant().getArgb(scheme)),
            windowDimming = Color(c.scrim().getArgb(scheme)),
        )
    } else {
        lightColorScheme(
            primary = Color(c.primary().getArgb(scheme)),
            onPrimary = Color(c.onPrimary().getArgb(scheme)),
            primaryContainer = Color(c.primaryContainer().getArgb(scheme)),
            onPrimaryContainer = Color(c.onPrimaryContainer().getArgb(scheme)),
            secondary = Color(c.secondary().getArgb(scheme)),
            onSecondary = Color(c.onSecondary().getArgb(scheme)),
            secondaryContainer = Color(c.secondaryContainer().getArgb(scheme)),
            onSecondaryContainer = Color(c.onSecondaryContainer().getArgb(scheme)),
            tertiaryContainer = Color(c.tertiaryContainer().getArgb(scheme)),
            onTertiaryContainer = Color(c.onTertiaryContainer().getArgb(scheme)),
            background = Color(c.background().getArgb(scheme)),
            onBackground = Color(c.onBackground().getArgb(scheme)),
            surface = Color(c.surface().getArgb(scheme)),
            onSurface = Color(c.onSurface().getArgb(scheme)),
            surfaceVariant = Color(c.surfaceVariant().getArgb(scheme)),
            onSurfaceVariantSummary = Color(c.onSurfaceVariant().getArgb(scheme)),
            error = Color(c.error().getArgb(scheme)),
            onError = Color(c.onError().getArgb(scheme)),
            errorContainer = Color(c.errorContainer().getArgb(scheme)),
            onErrorContainer = Color(c.onErrorContainer().getArgb(scheme)),
            outline = Color(c.outline().getArgb(scheme)),
            dividerLine = Color(c.outlineVariant().getArgb(scheme)),
            windowDimming = Color(c.scrim().getArgb(scheme)),
        )
    }
}

private fun createMonochromeMiuixDarkScheme(): Colors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1A1A1A),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFE0E0E0),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF202020),
    onSecondaryContainer = Color.White,
    tertiaryContainer = Color(0xFF262626),
    onTertiaryContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariantSummary = Color(0xFFD0D0D0),
    error = Color(0xFFE0E0E0),
    onError = Color.Black,
    errorContainer = Color(0xFF2A2A2A),
    onErrorContainer = Color.White,
    outline = Color(0xFF808080),
    dividerLine = Color(0xFF404040),
    windowDimming = Color.Black,
)

private fun createMonochromeMiuixLightScheme(): Colors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAEAEA),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF2E2E2E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F0F0),
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Color(0xFFE6E6E6),
    onTertiaryContainer = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariantSummary = Color(0xFF505050),
    error = Color(0xFF2E2E2E),
    onError = Color.White,
    errorContainer = Color(0xFFE2E2E2),
    onErrorContainer = Color.Black,
    outline = Color(0xFF707070),
    dividerLine = Color(0xFFC8C8C8),
    windowDimming = Color.Black,
)

private fun Color.isPureBlack(): Boolean = toArgb() == Color.Black.toArgb()
