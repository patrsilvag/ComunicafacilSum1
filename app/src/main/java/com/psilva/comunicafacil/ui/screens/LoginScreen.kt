package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.R
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onIrARegistro: () -> Unit,
    onIrARecuperar: () -> Unit,
    onLoginExitoso: () -> Unit,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }

    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarCampos(): Boolean {
        var ok = true
        val correoTrim = correo.trim()

        if (correoTrim.isBlank()) {
            errorCorreo = "El correo es obligatorio"
            ok = false
        } else if (!correoValido) {
            errorCorreo = "Ingrese un correo válido"
            ok = false
        }

        if (clave.isBlank()) {
            errorClave = "La contraseña es obligatoria"
            ok = false
        }

        return ok
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMensaje) }
    ) { paddingInterior ->
        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .width(280.dp)
                    .height(140.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                    )
                }
            }

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            EmailField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
                imeAction = ImeAction.Next,
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

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = clave,
                onValueChange = {
                    clave = it
                    errorClave = null
                },
                visible = claveVisible,
                onToggleVisible = { claveVisible = !claveVisible },
                imeAction = ImeAction.Done,
                modifier = Modifier.fillMaxWidth(),
                isError = errorClave != null,
                supportingText = errorClave
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorCorreo = null
                    errorClave = null

                    if (!validarCampos()) return@Button

                    alcance.launch {
                        val usuario = usuariosViewModel.obtenerUsuarioPorCredenciales(
                            correo = correo.trim(),
                            clave = clave
                        )

                        if (usuario != null) {
                            val modo = if (usuario.preferencia == "Lectura Aumentada") {
                                FontSizeMode.Aumentada
                            } else {
                                FontSizeMode.Normal
                            }
                            onFontSizeModeChange(modo)

                            tipoMensaje = TipoMensaje.EXITO
                            estadoSnackbar.showSnackbar(
                                message = "Acceso permitido",
                                duration = SnackbarDuration.Short
                            )
                            onLoginExitoso()
                        } else {
                            tipoMensaje = TipoMensaje.ERROR
                            estadoSnackbar.showSnackbar(
                                message = "Correo o contraseña incorrectos",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                enabled = correoValido && clave.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear cuenta",
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Crear cuenta. Doble toque para registrarse."
                    }
                    .clickable { onIrARegistro() }
            )

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .heightIn(min = 48.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Recuperar contraseña. Doble toque para continuar."
                    }
                    .clickable { onIrARecuperar() }
            )
        }
    }
}
