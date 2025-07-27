package com.app.k2t.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary Colors - Warm Orange/Amber (represents warmth of kitchen, fire, cooking)
val PrimaryLight = Color(0xFFFF6B35)        // Vibrant orange-red
val OnPrimaryLight = Color(0xFFFFFFFF)      // White text on primary
val PrimaryContainerLight = Color(0xFFFFE5DB) // Very light orange
val OnPrimaryContainerLight = Color(0xFF2D1000) // Dark brown text

val PrimaryDark = Color(0xFFFF8A65)         // Softer orange for dark mode
val OnPrimaryDark = Color(0xFF1A0E00)       // Very dark brown text
val PrimaryContainerDark = Color(0xFF3E2723) // Warm dark brown container
val OnPrimaryContainerDark = Color(0xFFFFCCBC) // Soft peach text

// Secondary Colors - Forest Green (represents fresh ingredients, nature, herbs)
val SecondaryLight = Color(0xFF2E7D32)      // Forest green
val OnSecondaryLight = Color(0xFFFFFFFF)    // White text
val SecondaryContainerLight = Color(0xFFE8F5E8) // Very light green
val OnSecondaryContainerLight = Color(0xFF1B5E20) // Dark green text

val SecondaryDark = Color(0xFF68B36B)       // Muted green for dark mode
val OnSecondaryDark = Color(0xFF0D2F0F)     // Very dark green text
val SecondaryContainerDark = Color(0xFF2D4A2E) // Dark forest green container
val OnSecondaryContainerDark = Color(0xFFC8E6C9) // Soft mint text

// Tertiary Colors - Golden Yellow (represents luxury, quality, premium experience)
val TertiaryLight = Color(0xFFF57C00)       // Golden orange
val OnTertiaryLight = Color(0xFFFFFFFF)     // White text
val TertiaryContainerLight = Color(0xFFFFF3E0) // Very light golden
val OnTertiaryContainerLight = Color(0xFF3E2723) // Dark brown text

val TertiaryDark = Color(0xFFFFAB40)        // Warm golden for dark mode
val OnTertiaryDark = Color(0xFF1F1100)      // Very dark amber text
val TertiaryContainerDark = Color(0xFF4A3728) // Dark golden brown container
val OnTertiaryContainerDark = Color(0xFFFFE0B2) // Soft cream text

// Error Colors - Warm Red (for alerts, errors)
val ErrorLight = Color(0xFFD32F2F)          // Warm red
val OnErrorLight = Color(0xFFFFFFFF)        // White text
val ErrorContainerLight = Color(0xFFFFEBEE) // Very light red
val OnErrorContainerLight = Color(0xFFB71C1C) // Dark red text

val ErrorDark = Color(0xFFEF5350)           // Softer red for dark mode
val OnErrorDark = Color(0xFF1A0000)         // Very dark red text
val ErrorContainerDark = Color(0xFF4A2C2A)  // Dark red-brown container
val OnErrorContainerDark = Color(0xFFFFCDD2) // Soft pink text

// Background Colors
val BackgroundLight = Color(0xFFFFFBFF)     // Pure white with slight warmth
val OnBackgroundLight = Color(0xFF1C1B1F)  // Near black text
val SurfaceLight = Color(0xFFFFFBFF)       // Same as background
val OnSurfaceLight = Color(0xFF1C1B1F)     // Near black text

val BackgroundDark = Color(0xFF121212)     // True dark background
val OnBackgroundDark = Color(0xFFE8E3E7)   // Warm light gray text
val SurfaceDark = Color(0xFF1E1E1E)        // Slightly lighter surface
val OnSurfaceDark = Color(0xFFE8E3E7)      // Warm light gray text

// Surface Variants
val SurfaceVariantLight = Color(0xFFF4F4F4) // Light gray
val OnSurfaceVariantLight = Color(0xFF49454F) // Dark gray text
val SurfaceVariantDark = Color(0xFF2A2A2A)  // Medium dark gray with warmth
val OnSurfaceVariantDark = Color(0xFFB8B5BA) // Soft gray text

// Outline Colors
val OutlineLight = Color(0xFF79747E)        // Medium gray
val OutlineVariantLight = Color(0xFFCAC4D0) // Light gray
val OutlineDark = Color(0xFF6F6F6F)         // Softer gray for dark mode
val OutlineVariantDark = Color(0xFF3A3A3A)  // Darker gray for dark mode

// Additional Custom Colors for Restaurant App
object K2TColors {
    // Status Colors
    val SuccessLight = Color(0xFF2E7D32)     // Green for success states
    val SuccessDark = Color(0xFF66BB6A)      // Softer green for dark mode

    val WarningLight = Color(0xFFF57C00)     // Orange for warnings
    val WarningDark = Color(0xFFFFB74D)      // Softer orange for dark mode

    val InfoLight = Color(0xFF1976D2)        // Blue for information
    val InfoDark = Color(0xFF64B5F6)         // Softer blue for dark mode

    // Role-specific Colors
    val TableLight = Color(0xFF8BC34A)       // Light green for table role
    val TableDark = Color(0xFF7CB342)        // Muted green for dark mode

    val WaiterLight = Color(0xFF2196F3)      // Blue for waiter role
    val WaiterDark = Color(0xFF42A5F5)       // Softer blue for dark mode

    val ChefLight = Color(0xFFFF5722)        // Orange-red for chef role
    val ChefDark = Color(0xFFFF7043)         // Softer orange-red for dark mode

    val AdminLight = Color(0xFF9C27B0)       // Purple for admin role
    val AdminDark = Color(0xFFBA68C8)        // Softer purple for dark mode

    // Food Category Colors
    val AppetizerLight = Color(0xFFFFEB3B)   // Yellow for appetizers
    val AppetizerDark = Color(0xFFFFF176)    // Softer yellow for dark mode

    val MainCourseLight = Color(0xFFFF5722)  // Orange-red for main courses
    val MainCourseDark = Color(0xFFFF7043)   // Softer orange-red for dark mode

    val DessertLight = Color(0xFFE91E63)     // Pink for desserts
    val DessertDark = Color(0xFFF06292)      // Softer pink for dark mode

    val BeverageLight = Color(0xFF03A9F4)    // Light blue for beverages
    val BeverageDark = Color(0xFF29B6F6)     // Softer blue for dark mode
}

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
)

@Composable
fun K2TTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Extension functions for easy access to custom colors
@Composable
fun getSuccessColor(): Color = if (isSystemInDarkTheme()) K2TColors.SuccessDark else K2TColors.SuccessLight

@Composable
fun getWarningColor(): Color = if (isSystemInDarkTheme()) K2TColors.WarningDark else K2TColors.WarningLight

@Composable
fun getInfoColor(): Color = if (isSystemInDarkTheme()) K2TColors.InfoDark else K2TColors.InfoLight

@Composable
fun getRoleColor(role: String): Color {
    return when (role.lowercase()) {
        "table" -> if (isSystemInDarkTheme()) K2TColors.TableDark else K2TColors.TableLight
        "waiter" -> if (isSystemInDarkTheme()) K2TColors.WaiterDark else K2TColors.WaiterLight
        "chef" -> if (isSystemInDarkTheme()) K2TColors.ChefDark else K2TColors.ChefLight
        "admin" -> if (isSystemInDarkTheme()) K2TColors.AdminDark else K2TColors.AdminLight
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "appetizer", "starter" -> if (isSystemInDarkTheme()) K2TColors.AppetizerDark else K2TColors.AppetizerLight
        "main", "main course", "entree" -> if (isSystemInDarkTheme()) K2TColors.MainCourseDark else K2TColors.MainCourseLight
        "dessert", "sweet" -> if (isSystemInDarkTheme()) K2TColors.DessertDark else K2TColors.DessertLight
        "beverage", "drink" -> if (isSystemInDarkTheme()) K2TColors.BeverageDark else K2TColors.BeverageLight
        else -> MaterialTheme.colorScheme.tertiary
    }
}