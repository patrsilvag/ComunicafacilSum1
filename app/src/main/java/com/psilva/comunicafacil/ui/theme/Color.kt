package com.psilva.comunicafacil.ui.theme

import androidx.compose.ui.graphics.Color

// --- Primario (Acción principal y estructura) ---
// Azul con mejor contraste para personas con baja visión.
val AppPrimary = Color(0xFF0056D2)
val AppOnPrimary = Color(0xFFFFFFFF)

// Contenedor primario (Uso: Pizarra de mensaje mostrado).
val AppPrimaryContainer = Color(0xFFD8E3FF)
val AppOnPrimaryContainer = Color(0xFF001B3F)

// --- Fondo / Superficies ---
val AppBackground = Color(0xFFFDFDFD)
val AppOnBackground = Color(0xFF191C1E)

val AppSurface = Color(0xFFFFFFFF)
val AppOnSurface = Color(0xFF191C1E)

// --- Secundario (Acción de alta visibilidad: "Hablar") ---
// Cambiamos el neutro por un Amarillo Ámbar (Color de accesibilidad universal).
val AppSecondary = Color(0xFFFFB300)
val AppOnSecondary = Color(0xFF000000) // Negro sobre amarillo para máximo contraste.

// Contenedor secundario
val AppSecondaryContainer = Color(0xFFFFECB3)
val AppOnSecondaryContainer = Color(0xFF261900)

// --- Error (Mensajes y validaciones) ---
val AppError = Color(0xFFBA1A1A)
val AppOnError = Color(0xFFFFFFFF)

val AppErrorContainer = Color(0xFFFFDAD6)
val AppOnErrorContainer = Color(0xFF410002)

// --- Bordes (OutlinedTextField) ---
// Un gris más oscuro para que los límites del campo sean más claros.
val AppOutline = Color(0xFF535F70)

// --- Snackbars semánticos (Se mantienen nombres por compatibilidad) ---
val AppSuccessContainer = Color(0xFFD1E7DD)
val AppOnSuccessContainer = Color(0xFF0F5132)

val AppInfoContainer = Color(0xFFE7F1FF)
val AppOnInfoContainer = Color(0xFF084298)

val AppErrorContainerCustom = Color(0xFFF8D7DA)
val AppOnErrorContainerCustom = Color(0xFF842029)

// --- Dark (Contraste visual activo) ---
// Negro puro para máximo contraste y reducción de brillo.
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF121212)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFFFFFFF)

// --- Definiciones faltantes para Modo Dark / Contraste Alto ---
val DarkPrimary = Color(0xFFD1E4FF)       // Azul claro para que resalte sobre negro
val DarkSecondary = Color(0xFFFFB300)     // Mantenemos el Amarillo Ámbar (esencial para accesibilidad)
val DarkOnSecondary = Color(0xFF000000)   // Negro sobre amarillo para máximo contraste

// Otros colores complementarios para el modo oscuro si los necesitas
val DarkPrimaryContainer = Color(0xFF00478F)
val DarkOnPrimaryContainer = Color(0xFFD8E3FF)