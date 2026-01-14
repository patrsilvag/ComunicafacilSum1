package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun RegisterScreen() {

    val estadoSnackbar = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) }
    ) { paddingInterior ->

        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Registro de usuario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

        }
    }
}