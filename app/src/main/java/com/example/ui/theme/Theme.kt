package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AzumiaoColorScheme = lightColorScheme(
    primary = SakuraPink,
    onPrimary = SoftCreamWhite,
    secondary = LavenderMist,
    onSecondary = SoftCreamWhite,
    tertiary = PeachBlush,
    onTertiary = DeepPlumInk,
    background = SakuraPink,
    onBackground = SoftCreamWhite,
    surface = SoftCreamWhite,
    onSurface = DeepPlumInk
)

@Composable
fun AzumiaoSecretTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AzumiaoColorScheme,
        typography = AzumiaoTypography,
        content = content
    )
}
