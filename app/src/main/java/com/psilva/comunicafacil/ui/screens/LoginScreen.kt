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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.R
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onIrARegistro: () -> Unit,
    onIrARecuperar: () -> Unit,
    onLoginExitoso: () -> Unit,
    usuariosViewModel: UsuariosViewModel
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    // Feedback persistente por campo
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    fun validarCampos(): Boolean {
        var ok = true
        val correoTrim = correo.trim()

        if (correoTrim.isBlank()) {
            errorCorreo = "El correo es obligatorio"
            ok = false
        } else if (!correoTrim.contains("@")) {
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
                        contentDescription = "Logo ComunicaFácil",
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
                onValueChange = { correo = it; errorCorreo = null },
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                isError = errorCorreo != null,
                supportingText = errorCorreo
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = clave,
                onValueChange = { clave = it; errorClave = null },
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
                    // limpia errores anteriores antes de validar
                    errorCorreo = null
                    errorClave = null

                    if (!validarCampos()) return@Button

                    alcance.launch {
                        val ok = usuariosViewModel.validarLogin(correo.trim(), clave)
                        if (ok) {
                            estadoSnackbar.showSnackbar(
                                message = "Acceso permitido",
                                duration = SnackbarDuration.Short
                            )
                            onLoginExitoso()
                        } else {
                            estadoSnackbar.showSnackbar(
                                message = "Correo o contraseña incorrectos",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                },
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
                    .clickable { onIrARegistro() }
                    .heightIn(min = 48.dp)
            )

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .heightIn(min = 48.dp)
                    .clickable { onIrARecuperar() }
            )
        }
    }
}
