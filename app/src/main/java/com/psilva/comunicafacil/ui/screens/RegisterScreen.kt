package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
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

    // 🔥 Carga inicial de usuarios desde Firebase
    LaunchedEffect(Unit) {
        usuariosViewModel.cargarUsuarios()
    }

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

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            estadoSnackbar.showSnackbar(it)
            if (uiState.registroExitoso) {
                onRegistroExitoso()
            }
            usuariosViewModel.limpiarMensaje()
        }
    }

    val formularioHabilitado = correoValido && clave.length >= 6 && aceptaTerminos && !uiState.cargando

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
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
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (uiState.cargando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Registrar")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onVolverLogin,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Volver")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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

                Text("Tipo de usuario", style = MaterialTheme.typography.titleSmall)
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { menuExpandido = true }) {
                                Icon(Icons.Default.ArrowDropDown, "Ver opciones")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        tipos.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Preferencia de interfaz", style = MaterialTheme.typography.titleSmall)
                preferencias.forEach { pref ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                preferenciaSeleccionada = pref
                                val modo = if (pref == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal
                                onFontSizeModeChange(modo)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (preferenciaSeleccionada == pref),
                            onClick = {
                                preferenciaSeleccionada = pref
                                val modo = if (pref == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal
                                onFontSizeModeChange(modo)
                            }
                        )
                        Text(pref, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
                    Text("Acepto los términos y condiciones", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 🔥 SECCIÓN CORREGIDA: Ahora muestra datos reales de Firebase
                Text("Usuarios registrados (Realtime Database)", fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(12.dp)) {
                            Text("Correo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Tipo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        }

                        // Dibujamos cada usuario que viene de Firebase
                        usuariosViewModel.usuariosRealtime.forEach { usuario ->
                            Row(Modifier.padding(12.dp).fillMaxWidth()) {
                                Text(usuario.correo, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(usuario.tipoUsuario, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        if (usuariosViewModel.usuariosRealtime.isEmpty()) {
                            Text("No hay usuarios registrados", Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        AppSnackbarHost(
            hostState = estadoSnackbar,
            tipoMensaje = if(uiState.esError) TipoMensaje.ERROR else TipoMensaje.EXITO
        )
    }
}