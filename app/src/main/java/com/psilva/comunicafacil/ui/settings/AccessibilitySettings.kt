package com.psilva.comunicafacil.ui.settings

import androidx.compose.runtime.compositionLocalOf

enum class FontSizeMode { Normal, Aumentada }

data class AccessibilitySettings(
    val fontSizeMode: FontSizeMode = FontSizeMode.Normal
)

val LocalAccessibilitySettings = compositionLocalOf { AccessibilitySettings() }


