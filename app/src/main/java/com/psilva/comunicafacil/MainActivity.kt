package com.psilva.comunicafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.psilva.comunicafacil.navigation.NavGraph
import com.psilva.comunicafacil.ui.settings.AccessibilitySettings
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.ui.settings.LocalAccessibilitySettings
import com.psilva.comunicafacil.ui.theme.ComunicafacilTheme
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Estados centrales para gestionar la accesibilidad en toda la app
            var fontSizeMode by remember { mutableStateOf(FontSizeMode.Normal) }
            var darkMode by remember { mutableStateOf(false) }

            // Proveedor de contexto para que las pantallas hijos (como HomeScreen)
            // apliquen el tamaño de fuente sin necesidad de recibirlo por parámetro.
            CompositionLocalProvider(
                LocalAccessibilitySettings provides AccessibilitySettings(fontSizeMode = fontSizeMode)
            ) {
                // Aplicación del Tema con soporte para Modo Oscuro dinámico
                ComunicafacilTheme(darkTheme = darkMode) {
                    val navController = rememberNavController()

                    // Inicialización del ViewModel compartido para la sesión
                    val usuariosViewModel: UsuariosViewModel = viewModel()

                    // NavGraph configurado para inyectar los callbacks de accesibilidad
                    NavGraph(
                        navController = navController,
                        usuariosViewModel = usuariosViewModel,
                        onFontSizeModeChange = { fontSizeMode = it },
                        darkMode = darkMode,
                        onDarkModeChange = { darkMode = it }
                    )
                }
            }
        }
    }
}