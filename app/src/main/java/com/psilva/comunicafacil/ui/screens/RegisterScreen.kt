package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.data.ResultadoRegistro
import com.psilva.comunicafacil.utils.validarCampo
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onVolverLogin: () -> Unit,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var correoValido by remember { mutableStateOf(false) }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    val tiposUsuario = listOf("Estudiante", "Docente", "Apoderado")
    var tipoSeleccionado by remember { mutableStateOf(tiposUsuario.first()) }
    var menuAbierto by remember { mutableStateOf(false) }

    val opcionesPreferencia = listOf("Lectura Normal", "Lectura Aumentada")
    var preferenciaSeleccionada by remember { mutableStateOf(opcionesPreferencia.first()) }

    var aceptaTerminos by remember { mutableStateOf(false) }

    // Estados de error para validación visual
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }
    var errorTerminos by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMsg by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarFormulario(): Boolean {
        var ok = true

        ok = validarCampo(correo.trim().isNotBlank()) {
            errorCorreo = "El correo es obligatorio"
        } && ok

        ok = validarCampo(correoValido) {
            errorCorreo = "Ingrese un correo válido"
        } && ok

        ok = validarCampo(clave.isNotBlank()) {
            errorClave = "La contraseña es obligatoria"
        } && ok

        ok = validarCampo(aceptaTerminos) {
            errorTerminos = "Debe aceptar los términos"
        } && ok

        return ok
    }


    fun registrar() {
        // Limpiar errores previos
        errorCorreo = null
        errorClave = null
        errorTerminos = null

        if (!validarFormulario()) return

        // ✅ INTEGRACIÓN CORRECTA: Usamos el callback onResultado para mostrar el Snackbar
        usuariosViewModel.registrarUsuario(
            correo = correo,
            clave = clave,
            tipoUsuario = tipoSeleccionado,
            aceptaTerminos = aceptaTerminos,
            onResultado = { resultado ->
                alcance.launch {
                    when (resultado) {
                        is ResultadoRegistro.Ok -> {
                            tipoMsg = TipoMensaje.EXITO
                            estadoSnackbar.showSnackbar(resultado.mensaje)
                            // Limpiar formulario tras éxito
                            correo = ""; clave = ""; aceptaTerminos = false
                            onFontSizeModeChange(FontSizeMode.Normal)
                        }
                        is ResultadoRegistro.Error -> {
                            tipoMsg = TipoMensaje.ERROR
                            estadoSnackbar.showSnackbar(resultado.mensaje)
                        }
                    }
                }
            }
        )
    }

    val formularioHabilitado = correoValido && clave.isNotBlank() && aceptaTerminos

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { registrar() },
                        enabled = formularioHabilitado,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Registrar") }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onVolverLogin,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Volver") }
                }
            }
        ) { paddingInterior ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterior)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Registro de usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                EmailField(
                    value = correo,
                    onValueChange = { correo = it; errorCorreo = null },
                    imeAction = ImeAction.Next,
                    onValidityChange = { correoValido = it }
                )
                errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    value = clave,
                    onValueChange = { clave = it; errorClave = null },
                    visible = claveVisible,
                    onToggleVisible = { claveVisible = !claveVisible },
                    imeAction = ImeAction.Done,
                    isError = errorClave != null,
                    supportingText = errorClave
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Selector Tipo de Usuario (Optimizado)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        label = { Text("Tipo de usuario") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(Modifier.matchParentSize().clickable { menuAbierto = true })
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        tiposUsuario.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = { tipoSeleccionado = tipo; menuAbierto = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Accesibilidad Visual", style = MaterialTheme.typography.bodyMedium)
                opcionesPreferencia.forEach { opcion ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (opcion == preferenciaSeleccionada),
                            onClick = {
                                preferenciaSeleccionada = opcion
                                onFontSizeModeChange(if (opcion == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal)
                            }
                        )
                        Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it; errorTerminos = null })
                    Text(text = "Acepto los términos y condiciones")
                }
                errorTerminos?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

                Spacer(modifier = Modifier.height(24.dp))

                // Tabla de Usuarios Registrados
                Text("Usuarios registrados (${usuariosViewModel.usuarios.size}/5)", style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(12.dp)) {
                            Text("Correo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Tipo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        }
                        if (usuariosViewModel.usuarios.isEmpty()) {
                            Text("Aún no hay usuarios.", Modifier.padding(12.dp))
                        } else {
                            usuariosViewModel.usuarios.forEach { usuario ->
                                Row(Modifier.padding(12.dp)) {
                                    Text(usuario.correo, Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(usuario.tipoUsuario, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // El Snackbar debe estar al final del Box para que flote sobre todo
        AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMsg)
    }
}