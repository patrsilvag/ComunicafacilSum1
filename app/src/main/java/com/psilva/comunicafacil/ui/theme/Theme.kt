package com.psilva.comunicafacil.ui.theme

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
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.ui.settings.LocalAccessibilitySettings

// --- Esquema para Contraste Visual (Modo Oscuro) ---
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = AppPrimary, // Usamos el azul profundo para resaltar bloques
    onPrimaryContainer = Color.White,
    secondary = DarkSecondary,     // Amarillo de alta visibilidad para el botón "Hablar"
    onSecondary = DarkOnSecondary,
    background = DarkBackground,   // Negro puro
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    outline = AppOutline
)

// --- Esquema para Lectura Normal (Modo Claro) ---
private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppOnPrimaryContainer,
    secondary = AppSecondary,      // Amarillo ámbar para accesibilidad
    onSecondary = AppOnSecondary,
    background = AppBackground,
    onBackground = AppOnBackground,
    surface = AppSurface,
    onSurface = AppOnSurface,
    outline = AppOutline
)

@Composable
fun ComunicafacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Mantiene accesibilidad/contraste estable
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

    val scale = when (LocalAccessibilitySettings.current.fontSizeMode) {
        FontSizeMode.Normal -> 1.0f
        FontSizeMode.Aumentada -> 1.15f
    }

    val scaledTypography = Typography.copy(
        bodyLarge = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * scale),
        bodyMedium = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * scale),
        bodySmall = Typography.bodySmall.copy(fontSize = Typography.bodySmall.fontSize * scale),
        titleLarge = Typography.titleLarge.copy(fontSize = Typography.titleLarge.fontSize * scale),
        titleMedium = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * scale),
        titleSmall = Typography.titleSmall.copy(fontSize = Typography.titleSmall.fontSize * scale),
        headlineLarge = Typography.headlineLarge.copy(fontSize = Typography.headlineLarge.fontSize * scale),
        headlineMedium = Typography.headlineMedium.copy(fontSize = Typography.headlineMedium.fontSize * scale),
        headlineSmall = Typography.headlineSmall.copy(fontSize = Typography.headlineSmall.fontSize * scale),
    )




    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}
