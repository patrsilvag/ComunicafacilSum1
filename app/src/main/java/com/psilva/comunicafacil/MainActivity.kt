package com.psilva.comunicafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.psilva.comunicafacil.navigation.NavGraph
import com.psilva.comunicafacil.ui.theme.ComunicafacilTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComunicafacilTheme {
                val navController = rememberNavController()
                val usuariosViewModel: UsuariosViewModel = viewModel()
                NavGraph(navController = navController, usuariosViewModel = usuariosViewModel)

            }
        }
    }
}

