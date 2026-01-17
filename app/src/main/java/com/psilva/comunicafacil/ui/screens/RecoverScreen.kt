package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.TipoMensaje


@Composable
fun RecoverScreen(
    onVolverLogin: () -> Unit,
    usuariosViewModel: UsuariosViewModel
) {
    var correo by remember { mutableStateOf("") }

    // Error persistente
    var errorCorreo by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarCorreo(): Boolean {
        val correoTrim = correo.trim()

        return when {
            correoTrim.isBlank() -> {
                errorCorreo = "El correo es obligatorio"
                false
            }
            !correoTrim.contains("@") -> {
                errorCorreo = "Ingrese un correo válido"
                false
            }
            else -> true
        }
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMensaje) }

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
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorCorreo != null,
                supportingText = {
                    if (errorCorreo != null) Text(errorCorreo!!)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Limpia error previo y valida
                    errorCorreo = null
                    if (!validarCorreo()) return@Button

                    alcance.launch {
                        val existe = usuariosViewModel.existeCorreo(correo.trim())
                        if (existe) {
                            tipoMensaje = TipoMensaje.EXITO
                            estadoSnackbar.showSnackbar("Correo encontrado. Recuperación simulada", duration = SnackbarDuration.Short)

                        } else {
                            estadoSnackbar.showSnackbar(
                                message = "Correo no registrado",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp) // ✅ mínimo táctil
            ) {
                Text("Enviar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onVolverLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp) // ✅ mínimo táctil
            ) {
                Text("Volver")
            }
        }
    }
}
