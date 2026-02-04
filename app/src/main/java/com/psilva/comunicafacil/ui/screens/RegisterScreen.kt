package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.EmailField
import com.psilva.comunicafacil.ui.components.PasswordField
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.ResultadoRegistro
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
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }
    var errorTerminos by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun registrar() {
        errorCorreo = null; errorClave = null; errorTerminos = null
        if (correo.isBlank()) { errorCorreo = "El correo es obligatorio"; return }
        if (!aceptaTerminos) { errorTerminos = "Debe aceptar los términos"; return }

        alcance.launch {
            val usuarioNuevo = Usuario(correo.trim(), clave, tipoSeleccionado, aceptaTerminos, preferenciaSeleccionada)
            when (val resultado = usuariosViewModel.registrarUsuario(usuarioNuevo)) {
                is ResultadoRegistro.Ok -> {
                    tipoMensaje = TipoMensaje.EXITO
                    estadoSnackbar.showSnackbar(resultado.mensaje)
                    correo = ""; clave = ""; aceptaTerminos = false
                }
                is ResultadoRegistro.Error -> {
                    tipoMensaje = TipoMensaje.ERROR
                    estadoSnackbar.showSnackbar(resultado.mensaje)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingInterior ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterior)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                // INPUTS
                EmailField(
                    value = correo,
                    onValueChange = { correo = it; errorCorreo = null },
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth(),
                    onValidityChange = { correoValido = it }
                )
                if (errorCorreo != null) {
                    Text(errorCorreo!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

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

                Spacer(modifier = Modifier.height(16.dp))

                // COMBO BOX
                Text("Tipo de usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = tipoSeleccionado, onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { menuAbierto = true })
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        tiposUsuario.forEach { tipo ->
                            DropdownMenuItem(text = { Text(tipo) }, onClick = { tipoSeleccionado = tipo; menuAbierto = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RADIO BUTTONS
                Text("Accesibilidad Visual", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                opcionesPreferencia.forEach { opcion ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (opcion == preferenciaSeleccionada),
                            onClick = {
                                preferenciaSeleccionada = opcion
                                onFontSizeModeChange(if (opcion == "Lectura Aumentada") FontSizeMode.Aumentada else FontSizeMode.Normal)
                            }
                        )
                        Text(opcion)
                    }
                }

                // CHECKBOX
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
                    Text("Acepto los términos y condiciones")
                }
                if (errorTerminos != null) {
                    Text(errorTerminos!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // BOTONES (Ubicación original dentro del Column)
                Button(
                    onClick = { registrar() },
                    enabled = correoValido && clave.isNotBlank() && aceptaTerminos,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Registrar usuario")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onVolverLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = "Volver",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                // GRILLA / TABLA DE USUARIOS
                Text("Usuarios registrados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                usuariosViewModel.usuarios.forEach { usuario ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                Box(contentAlignment = Alignment.Center) { Text(usuario.correo.take(1).uppercase()) }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(usuario.correo, fontWeight = FontWeight.Bold)
                                Text(usuario.tipoUsuario, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMensaje)
    }
}