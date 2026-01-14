package com.psilva.comunicafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.psilva.comunicafacil.ui.screens.LoginScreen
import com.psilva.comunicafacil.ui.screens.RegisterScreen
import com.psilva.comunicafacil.ui.theme.ComunicafacilTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComunicafacilTheme {
             //   LoginScreen()
                RegisterScreen()
            }
        }
    }
}

