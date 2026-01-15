package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onIrARegistro: () -> Unit,
                onIrARecuperar: () -> Unit,
                onLoginExitoso: () -> Unit,
                usuariosViewModel: UsuariosViewModel) {

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) }
    ) { paddingInterior ->

        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (claveVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    val descripcion = if (claveVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    }

                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(
                            imageVector = if (claveVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = descripcion
                        )
                    }
                }
            )


            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    alcance.launch {
                        when {
                            correo.isBlank() || clave.isBlank() -> {
                                estadoSnackbar.showSnackbar(
                                    "Ingrese correo y contraseña"
                                )
                            }
                            !correo.contains("@") -> {
                                estadoSnackbar.showSnackbar(
                                    "Ingrese un correo válido"
                                )
                            }
                            usuariosViewModel.validarLogin(correo, clave) -> {
                                estadoSnackbar.showSnackbar("Acceso permitido",duration = SnackbarDuration.Short)
                                onLoginExitoso()

                            }
                            else -> {
                                estadoSnackbar.showSnackbar(
                                    "Credenciales inválidas"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ingresar")
            }



            Text(
                text = "Crear cuenta",
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { onIrARegistro() }
            )

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onIrARecuperar() }
            )

        }
    }
}
