package com.wyrix.browser.core.designsystem

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WyrixNeonBlue,
    secondary = WyrixNeonViolet,
    tertiary = WyrixElectricGreen,
    background = WyrixBlack,
    surface = WyrixDarkSurface,
    surfaceVariant = WyrixPanel,
    onPrimary = WyrixBlack,
    onSecondary = WyrixBlack,
    onBackground = WyrixTextPrimary,
    onSurface = WyrixTextPrimary,
    error = WyrixAlertRed
)

@Composable
fun WyrixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
