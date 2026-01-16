package com.psilva.comunicafacil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppOnPrimaryContainer,

    secondary = AppSecondary,
    onSecondary = AppOnSecondary,
    secondaryContainer = AppSecondaryContainer,
    onSecondaryContainer = AppOnSecondaryContainer,

    error = AppError,
    onError = AppOnError,
    errorContainer = AppErrorContainer,
    onErrorContainer = AppOnErrorContainer,

    background = AppBackground,
    onBackground = AppOnBackground,

    surface = AppSurface,
    onSurface = AppOnSurface,

    outline = AppOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppOnPrimaryContainer,

    secondary = AppSecondary,
    onSecondary = AppOnSecondary,
    secondaryContainer = AppSecondaryContainer,
    onSecondaryContainer = AppOnSecondaryContainer,

    error = AppError,
    onError = AppOnError,
    errorContainer = AppErrorContainer,
    onErrorContainer = AppOnErrorContainer,

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
