package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    // 1. Observar el estado centralizado
    val uiState by usuariosViewModel.uiState.collectAsStateWithLifecycle()
    val teclado = LocalSoftwareKeyboardController.current

    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }
    var errorCorreoLocal by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    // Esto asegura que CADA VEZ que la pantalla se muestra, la pizarra se borra
    LaunchedEffect(Unit) {
        usuariosViewModel.limpiarMensaje()
    }
    // LIMPIEZA AUTOMÁTICA AL SALIR
    DisposableEffect(Unit) {
        onDispose {
            usuariosViewModel.limpiarMensaje()
        }
    }

    // 2. Efecto para reaccionar a mensajes del ViewModel (Traducciones de Firebase)
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { texto ->
            teclado?.hide()
            alcance.launch {
                estadoSnackbar.showSnackbar(texto)
                usuariosViewModel.limpiarMensaje()
            }
        }
    }

    // Usamos Box para asegurar que el Snackbar tenga prioridad visual (Z-Index)
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // El Scaffold queda limpio de snackbarHost para manejarlo en el Box
        ) { paddingInterior ->

            Column(
                modifier = Modifier
                    .padding(paddingInterior)
                    .padding(24.dp)
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
                        errorCorreoLocal = null
                    },
                    imeAction = ImeAction.Done,
                    modifier = Modifier.fillMaxWidth(),
                    onValidityChange = { correoValido = it }
                )

                // Error de validación local (formato)
                if (errorCorreoLocal != null) {
                    Text(
                        text = errorCorreoLocal!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!correoValido) {
                            errorCorreoLocal = "Ingrese un correo válido"
                            return@Button
                        }

                        // Llamamos al ViewModel (él se encarga de traducir el error)
                        usuariosViewModel.recuperarPassword(correo.trim()) { _ ->
                            // El resultado ya se maneja vía uiState.mensaje en el LaunchedEffect
                        }
                    },
                    enabled = correoValido && !uiState.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Enviar instrucciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        usuariosViewModel.limpiarMensaje()
                        onVolverLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "Volver al inicio",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // 3. Snackbar posicionado al final del Box para que flote sobre el Scaffold
        AppSnackbarHost(
            hostState = estadoSnackbar,
            tipoMensaje = if (uiState.esError) TipoMensaje.ERROR else TipoMensaje.EXITO
        )
    }
}