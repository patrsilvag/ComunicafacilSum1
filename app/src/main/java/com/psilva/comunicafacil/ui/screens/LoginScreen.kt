package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.psilva.comunicafacil.R
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.utils.validarCampo
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onIrARegistro: () -> Unit,
    onIrARecuperar: () -> Unit,
    onLoginExitoso: (Usuario) -> Unit,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    // Observamos el estado global del ViewModel
    val uiState by usuariosViewModel.uiState.collectAsState()

    // Estados locales de UI para los campos
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

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

    //  EFECTO LANZADO: Escucha mensajes del ViewModel (Errores traducidos)
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { texto ->
            // Corregido: Si no es error, usamos EXITO (verde) en lugar de INFO (azul)
            tipoMensaje = if (uiState.esError) TipoMensaje.ERROR else TipoMensaje.EXITO
            alcance.launch {
                estadoSnackbar.showSnackbar(texto)
                usuariosViewModel.limpiarMensaje()
            }
        }
    }

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

    // Usamos Box para que el Snackbar flote al final, igual que en Recover y Register
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingInterior ->
            Column(
                modifier = Modifier
                    .padding(paddingInterior)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo adaptativo
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
                        val logoRes = if (darkMode) R.drawable.logo_dark else R.drawable.logo_light
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = "Logo ComunicaFácil",
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }

                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Control de Accesibilidad (Modo Oscuro)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp).zIndex(1f)
                ) {
                    Text(text = "Contraste visual")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { onDarkModeChange(it) }
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
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
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

                // Botón de Ingreso
                if (uiState.cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            if (!validarCampos()) return@Button

                            usuariosViewModel.login(correo, clave) { resultado ->
                                resultado.onSuccess { usuario ->
                                    onLoginExitoso(usuario)
                                }
                            }
                        },
                        enabled = correoValido && clave.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Ingresar", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onIrARegistro) { Text("Crear cuenta") }
                TextButton(onClick = onIrARecuperar) { Text("¿Olvidaste tu contraseña?") }
            }
        }

        // Host del Snackbar fuera del Scaffold para asegurar visibilidad superior
        AppSnackbarHost(
            hostState = estadoSnackbar,
            tipoMensaje = tipoMensaje
        )
    }
}