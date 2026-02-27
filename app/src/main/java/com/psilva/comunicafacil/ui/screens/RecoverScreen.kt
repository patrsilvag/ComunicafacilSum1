package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.utils.validarCampo
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun RecoverScreen(
    onVolverLogin: () -> Unit,
    usuariosViewModel: UsuariosViewModel
) {
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarAntesDeEnviar(): Boolean {
        var ok = true

        ok = validarCampo(correo.trim().isNotBlank()) {
            errorCorreo = "El correo es obligatorio"
        } && ok

        ok = validarCampo(correoValido) {
            errorCorreo = "Ingrese un correo válido"
        } && ok

        return ok
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                hostState = estadoSnackbar,
                tipoMensaje = tipoMensaje
            )
        }
    ) { paddingInterior ->

        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa tu correo y te enviaremos instrucciones de recuperación.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            EmailField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
                imeAction = ImeAction.Done,
                modifier = Modifier.fillMaxWidth(),
                onValidityChange = { correoValido = it }
            )

            if (!errorCorreo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorCorreo!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    errorCorreo = null
                    if (!validarAntesDeEnviar()) return@Button

                    // Llamada asíncrona al ViewModel
                    usuariosViewModel.recuperarPassword(correo.trim()) { resultado ->
                        alcance.launch {
                            resultado.fold(
                                onSuccess = {
                                    // Firebase ya envió el correo si llegamos aquí
                                    tipoMensaje = TipoMensaje.EXITO
                                    estadoSnackbar.showSnackbar(
                                        message = "Si el correo está registrado, recibirás un mensaje pronto.",
                                        duration = SnackbarDuration.Long
                                    )
                                },
                                onFailure = { error ->
                                    // Manejo de errores de red o Firebase
                                    tipoMensaje = TipoMensaje.ERROR
                                    estadoSnackbar.showSnackbar(
                                        message = error.message ?: "Error al procesar la solicitud",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            )
                        }
                    }
                },
                enabled = correoValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Enviar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onVolverLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    text = "Volver",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}