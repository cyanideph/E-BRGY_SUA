package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// MATERIAL 3 COLOR SCHEMES
// Brand Colors:
// - Deep Ocean Blue: #003366 (Primary)
// - Southern Sea Teal: #008080 (Secondary)
// - Warm Sun Gold: #FFD700 (Accent / Tertiary)
// - Warm Coastal White: #FAFAF9 (Surface Background)
// - Deep Navy: #1A2B3C (Text Color)
// ============================================================================

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF003366),              // Deep Ocean Blue
    onPrimary = Color.White,
    primaryContainer = DeepOceanContainer,
    onPrimaryContainer = OnDeepOceanContainer,
    inversePrimary = DeepOceanBlueDarkTheme,

    secondary = Color(0xFF008080),            // Southern Sea Teal
    onSecondary = Color.White,
    secondaryContainer = SouthernSeaTealContainer,
    onSecondaryContainer = OnSouthernSeaTealContainer,

    tertiary = Color(0xFFFFD700),             // Warm Sun Gold Accent
    onTertiary = Color(0xFF1A2B3C),           // Deep Navy
    tertiaryContainer = WarmSunGoldContainer,
    onTertiaryContainer = OnWarmSunGoldContainer,

    background = Color(0xFFFAFAF9),          // Warm Coastal White
    onBackground = Color(0xFF1A2B3C),        // Deep Navy Text
    surface = Color(0xFFFAFAF9),             // Warm Coastal White Surface
    onSurface = Color(0xFF1A2B3C),           // Deep Navy Text
    surfaceVariant = CoastalSurfaceVariant,
    onSurfaceVariant = DeepNavySecondary,
    surfaceTint = Color(0xFF003366),
    inverseSurface = Color(0xFF1A2B3C),
    inverseOnSurface = Color(0xFFFAFAF9),

    outline = CoastalBorderSoft,
    outlineVariant = Color(0xFFC7D3DC),
    scrim = Color(0x66000000),

    error = RestrainedCoralRed,
    onError = Color.White,
    errorContainer = RestrainedCoralRedContainer,
    onErrorContainer = Color(0xFF410002)
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DeepOceanBlueDarkTheme,         // Accessible light ocean blue tint for dark surfaces
    onPrimary = Color(0xFF001F3F),
    primaryContainer = Color(0xFF003366),     // Deep Ocean Blue core
    onPrimaryContainer = DeepOceanContainer,
    inversePrimary = Color(0xFF003366),

    secondary = SouthernSeaTealDarkTheme,     // Soft radiant teal (#4DB6AC) for contrast on dark
    onSecondary = Color(0xFF00201D),
    secondaryContainer = Color(0xFF004D40),   // Southern Sea Teal dark
    onSecondaryContainer = SouthernSeaTealContainer,

    tertiary = WarmSunGoldDarkTheme,          // Warm Sun Gold tint for dark mode
    onTertiary = Color(0xFF3E3000),
    tertiaryContainer = Color(0xFF5A4400),
    onTertiaryContainer = WarmSunGoldContainer,

    background = DarkBackground,              // Deep midnight navy (#0D1824)
    onBackground = DarkTextPrimary,           // High contrast coastal white text
    surface = DarkSurface,                    // Deep navy surface (#142334)
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    surfaceTint = DeepOceanBlueDarkTheme,
    inverseSurface = Color(0xFFFAFAF9),
    inverseOnSurface = Color(0xFF1A2B3C),

    outline = DarkBorderSoft,
    outlineVariant = Color(0xFF23384E),
    scrim = Color(0x99000000),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// ============================================================================
// CUSTOM MATERIAL 3 TYPOGRAPHY
// ============================================================================

val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 54.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================================================
// THEME COMPOSABLES
// ============================================================================

@Composable
fun EBarangaySuaTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}

/**
 * Default application theme matching template call-sites. Default is Light Mode.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    EBarangaySuaTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
