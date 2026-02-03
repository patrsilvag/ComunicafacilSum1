package com.psilva.comunicafacil.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

import androidx.compose.ui.semantics.role

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

import com.psilva.comunicafacil.ui.theme.AppOnPrimary

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LoginScreen(
    onIrARegistro: () -> Unit,
    onIrARecuperar: () -> Unit,
    onLoginExitoso: () -> Unit,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
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
                // Reemplaza el bloque del logo por este:
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(160.dp),
                        // Aplicamos el filtro para que en modo oscuro el blanco no moleste
                        colorFilter = if (darkMode) {
                            ColorFilter.tint(color = AppOnPrimary, blendMode = BlendMode.Modulate)
                        } else null
                    )
                }
            }

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineLarge, // Visibilidad para discapacidad visual
                color = MaterialTheme.colorScheme.primary, // Azul profundo
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Contraste visual"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = darkMode,
                    onCheckedChange = { onDarkModeChange(it) },
                    modifier = Modifier.semantics {
                        contentDescription =
                            if (darkMode)
                                "Desactivar contraste alto"
                            else
                                "Activar contraste alto"
                    }
                )
            }

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

// Este bloque de error ahora se verá con el color profesional de tu paleta
            if (!errorCorreo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = errorCorreo!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 12.dp) // Un pequeño margen para que alinee con el label
                )
            }

        // Aumentamos a 24.dp para que no se vea "amontonado" (Estilo MD3)
            Spacer(modifier = Modifier.height(24.dp))

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
