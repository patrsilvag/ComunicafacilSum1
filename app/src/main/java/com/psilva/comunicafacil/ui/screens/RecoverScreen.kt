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
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun RecoverScreen(
    onVolverLogin: () -> Unit,
    usuariosViewModel: UsuariosViewModel
) {
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }

    // Error persistente (solo para caso "vacío" o errores de negocio)
    var errorCorreo by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarAntesDeEnviar(): Boolean {
        val correoTrim = correo.trim()
        if (correoTrim.isBlank()) {
            errorCorreo = "El correo es obligatorio"
            return false
        }
        if (!correoValido) {
            // EmailField ya muestra "Ingrese un correo válido", pero dejamos este guardrail
            errorCorreo = "Ingrese un correo válido"
            return false
        }
        return true
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
                style = MaterialTheme.typography.headlineLarge, // Fuente grande para accesibilidad
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary, // Azul profundo
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa tu correo y te enviaremos las instrucciones para restablecer tu clave.",
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

                    alcance.launch {
                        val existe = usuariosViewModel.existeCorreo(correo.trim())
                        if (existe) {
                            tipoMensaje = TipoMensaje.EXITO
                            estadoSnackbar.showSnackbar(
                                message = "Correo encontrado. Recuperación simulada",
                                duration = SnackbarDuration.Short
                            )
                        } else {
                            tipoMensaje = TipoMensaje.ERROR
                            estadoSnackbar.showSnackbar(
                                message = "Correo no registrado",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                enabled = correoValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Altura mejorada para accesibilidad motriz
                shape = RoundedCornerShape(16.dp), // Esquinas modernas de Material 3
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Azul profundo
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
                    .height(56.dp), // Consistencia en el tamaño de los botones
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Borde definido en Color.kt
            ) {
                Text(
                    text = "Volver",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary // Texto en azul para coherencia visual
                )
            }
        }
    }
}
