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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.zIndex
import com.psilva.comunicafacil.utils.validarCampo

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

        ok = validarCampo(correo.isNotBlank()) {
            errorCorreo = "El correo es obligatorio"
        } && ok

        ok = validarCampo(clave.isNotBlank()) {
            errorClave = "La contraseña es obligatoria"
        } && ok

        return ok
    }


    Scaffold(
        snackbarHost = { AppSnackbarHost(estadoSnackbar, tipoMensaje) }
    ) { paddingInterior ->
        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO  Integrado con Card y soporte para Modo Oscuro
            Card(
                modifier = Modifier
                    .width(280.dp)
                    .height(140.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Asegúrate que el archivo se llame logo.png en drawable
                        contentDescription = "Logo ComunicaFácil",
                        modifier = Modifier.size(160.dp),
                        colorFilter = if (darkMode) {
                            ColorFilter.tint(
                                color = MaterialTheme.colorScheme.onBackground
                              //  blendMode = BlendMode.SrcIn
                            )
                        } else null
                    )
                }
            }

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Switch de Contraste Visual para Accesibilidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .zIndex(1f)
            ) {
                Text(text = "Contraste visual")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = darkMode,
                    onCheckedChange = {
                        onDarkModeChange(it)
                    }
                )
            }

            EmailField(
                value = correo,
                onValueChange = { correo = it; errorCorreo = null },
                imeAction = ImeAction.Next,
                onValidityChange = { correoValido = it },
                modifier = Modifier.fillMaxWidth()
            )
            errorCorreo?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = clave,
                onValueChange = { clave = it; errorClave = null },
                visible = claveVisible,
                onToggleVisible = { claveVisible = !claveVisible },
                imeAction = ImeAction.Done,
                isError = errorClave != null,
                supportingText = errorClave,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!validarCampos()) return@Button

                    if (usuariosViewModel.validarLogin(correo, clave)) {
                        onLoginExitoso()
                    } else {
                        alcance.launch {
                            tipoMensaje = TipoMensaje.ERROR
                            estadoSnackbar.showSnackbar("Credenciales incorrectas")
                        }
                    }
                },
                enabled = correoValido && clave.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ingresar", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onIrARegistro) { Text("Crear cuenta") }
            TextButton(onClick = onIrARecuperar) { Text("¿Olvidaste tu contraseña?") }
        }
    }
}