package com.app.k2t.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext



// Orange Color Palette
val Orange50 = Color(0xFFFFF3E0)
val Orange100 = Color(0xFFFFE0B2)
val Orange200 = Color(0xFFFFCC80)
val Orange300 = Color(0xFFFFB74D)
val Orange400 = Color(0xFFFFA726)
val Orange500 = Color(0xFFFF9800)
val Orange600 = Color(0xFFFB8C00)
val Orange700 = Color(0xFFF57C00)
val Orange800 = Color(0xFFEF6C00)
val Orange900 = Color(0xFFE65100)

// Dark Theme Colors
val DarkSurface = Color(0xFF121212)
val DarkSurfaceVariant = Color(0xFF1E1E1E)
val DarkBackground = Color(0xFF0F0F0F)
val DarkOnSurface = Color(0xFFE0E0E0)
val DarkOnBackground = Color(0xFFE0E0E0)

val DarkColorScheme = darkColorScheme(
    primary = Orange400,
    onPrimary = Color.Black,
    primaryContainer = Orange700,
    onPrimaryContainer = Orange100,
    secondary = Orange300,
    onSecondary = Color.Black,
    secondaryContainer = Orange800,
    onSecondaryContainer = Orange100,
    tertiary = Orange200,
    onTertiary = Color.Black,
    tertiaryContainer = Orange900,
    onTertiaryContainer = Orange50,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Orange600,
    surfaceDim = Color(0xFF111318),
    surfaceBright = Color(0xFF383B40),
    surfaceContainerLowest = Color(0xFF0C0E13),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF272A2F),
    surfaceContainerHighest = Color(0xFF32353A)
)


//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

@Composable
fun K2TTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}