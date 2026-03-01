package com.psilva.comunicafacil.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistroExitoso: () -> Unit,
    onVolverLogin: () -> Unit,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit
) {
    val uiState by usuariosViewModel.uiState.collectAsStateWithLifecycle()
    val teclado = LocalSoftwareKeyboardController.current

    // Estados locales del formulario
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }

    val tipos = listOf("Estudiante", "Docente", "Administrativo")
    var tipoSeleccionado by remember { mutableStateOf(tipos[0]) }
    var menuExpandido by remember { mutableStateOf(false) }

    val preferencias = listOf("Lectura Normal", "Lectura Aumentada")
    var preferenciaSeleccionada by remember { mutableStateOf(preferencias[0]) }

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
    // Carga inicial
    LaunchedEffect(Unit) {
        usuariosViewModel.cargarUsuarios()
    }

    // --- 🎯 LOGICA DE MENSAJES (Igual que el original pero conectado al VM) ---
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { texto ->
            Log.d("UI_DEBUG", "Mostrando Snackbar: $texto")
            teclado?.hide()

            alcance.launch {
                // Esto obliga a Compose a procesar el cambio de color de tipoMensaje
                delay(50)
                estadoSnackbar.showSnackbar(texto)

                if (uiState.registroExitoso) {
                    delay(1000)
                    onRegistroExitoso()
                }
                usuariosViewModel.limpiarMensaje()
            }
        }
    }

    val formularioHabilitado = correoValido && clave.length >= 6 && aceptaTerminos && !uiState.cargando

    // Usamos Box para asegurar que el Snackbar flote al final, como en tu código original
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Surface(tonalElevation = 3.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Button(
                            onClick = {
                                usuariosViewModel.registrarUsuario(
                                    correo = correo.trim(),
                                    clave = clave,
                                    tipoUsuario = tipoSeleccionado,
                                    aceptaTerminos = aceptaTerminos,
                                    preferencia = preferenciaSeleccionada
                                )
                            },
                            enabled = formularioHabilitado,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (uiState.cargando) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Registrar", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                usuariosViewModel.limpiarMensaje()
                                onVolverLogin()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Volver al Login")
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Registro de usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                EmailField(
                    value = correo,
                    onValueChange = { correo = it },
                    imeAction = ImeAction.Next,
                    onValidityChange = { correoValido = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    value = clave,
                    onValueChange = { clave = it },
                    visible = claveVisible,
                    onToggleVisible = { claveVisible = !claveVisible },
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Tipo de usuario", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                    Box(Modifier.matchParentSize().clickable { menuExpandido = true })
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        tipos.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = { tipoSeleccionado = tipo; menuExpandido = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Preferencia de interfaz", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                preferencias.forEach { pref ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable {
                            preferenciaSeleccionada = pref
                            onFontSizeModeChange(if (pref == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal)
                        }.padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = (preferenciaSeleccionada == pref), onClick = {
                            preferenciaSeleccionada = pref
                            onFontSizeModeChange(if (pref == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal)
                        })
                        Text(pref)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
                    Text("Acepto los términos y condiciones", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Usuarios registrados recientemente", fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Row(Modifier.background(MaterialTheme.colorScheme.secondaryContainer).padding(12.dp)) {
                            Text("Correo", Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
                            Text("Tipo", Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
                        }
                        usuariosViewModel.usuariosRealtime.take(5).forEach { usuario ->
                            Row(Modifier.padding(12.dp)) {
                                Text(usuario.correo, Modifier.weight(1.2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(usuario.tipoUsuario, Modifier.weight(0.8f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- 🚀 ESTA ES LA CLAVE: El Snackbar fuera del Scaffold pero dentro del Box ---
        // Snackbar con Z-Index para asegurar visibilidad
        Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
            AppSnackbarHost(
                hostState = estadoSnackbar,
                tipoMensaje = if (uiState.esError) TipoMensaje.ERROR else TipoMensaje.EXITO
            )
        }
    }
}