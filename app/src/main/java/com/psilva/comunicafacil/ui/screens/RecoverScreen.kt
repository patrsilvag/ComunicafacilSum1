package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun RecoverScreen() {

    var correo by remember { mutableStateOf("") }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

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
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    alcance.launch {
                        when {
                            correo.isBlank() ->
                                estadoSnackbar.showSnackbar("Ingrese su correo")

                            !correo.contains("@") ->
                                estadoSnackbar.showSnackbar("Ingrese un correo válido")

                            else ->
                                estadoSnackbar.showSnackbar(
                                    "Solicitud registrada (validación básica)"
                                )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Enviar")
            }
        }
    }
}
