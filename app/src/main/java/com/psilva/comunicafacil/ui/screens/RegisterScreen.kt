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
    var errorTipoUsuario by remember { mutableStateOf<String?>(null) }
    var errorTerminos by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    fun validarFormulario(): Boolean {
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

        if (tipoSeleccionado.isBlank()) {
            errorTipoUsuario = "Seleccione un tipo de usuario"
            ok = false
        }

        if (!aceptaTerminos) {
            errorTerminos = "Debe aceptar los términos para continuar"
            ok = false
        }

        return ok
    }

    fun registrar() {
        errorCorreo = null
        errorClave = null
        errorTipoUsuario = null
        errorTerminos = null

        if (!validarFormulario()) return

        alcance.launch {
            val usuarioNuevo = Usuario(
                correo = correo.trim(),
                clave = clave,
                tipoUsuario = tipoSeleccionado,
                aceptaTerminos = aceptaTerminos,
                preferencia = preferenciaSeleccionada
            )

            when (val resultado = usuariosViewModel.registrarUsuario(usuarioNuevo)) {
                is ResultadoRegistro.Ok -> {
                    tipoMensaje = TipoMensaje.EXITO
                    estadoSnackbar.showSnackbar(
                        message = resultado.mensaje,
                        duration = SnackbarDuration.Short
                    )

                    correo = ""
                    clave = ""
                    aceptaTerminos = false

                    // Se restablece la preferencia de lectura y el escalado global de tipografía.
                    preferenciaSeleccionada = opcionesPreferencia.first()
                    onFontSizeModeChange(FontSizeMode.Normal)

                    tipoSeleccionado = tiposUsuario.first()
                }

                is ResultadoRegistro.Error -> {
                    tipoMensaje = TipoMensaje.ERROR
                    estadoSnackbar.showSnackbar(
                        message = resultado.mensaje,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    val formularioHabilitado =
        correoValido &&
                clave.isNotBlank() &&
                tipoSeleccionado.isNotBlank() &&
                aceptaTerminos

    // Se dibuja el Scaffold y, por encima, el Snackbar como overlay para que no sea recortado por bottomBar/teclado.
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { registrar() },
                        enabled = formularioHabilitado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Registrar")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onVolverLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Volver")
                    }
                }
            }
        ) { paddingInterior ->
            val scrollEstado = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterior)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollEstado)
            ) {
                Text(
                    text = "Registro de usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                // Se presenta un selector desplegable para elegir el tipo de usuario, con indicador visual y descripción para TalkBack.
                val tipoUsuarioInteraction = remember { MutableInteractionSource() }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        label = { Text("Tipo de usuario") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorTipoUsuario != null,
                        supportingText = {
                            if (errorTipoUsuario != null) Text(errorTipoUsuario!!)
                        }
                    )

                    // Se agrega una capa transparente para capturar el toque en toda el área, facilitando el uso para personas con discapacidad motora y TalkBack.
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .semantics {
                                role = Role.Button
                                contentDescription =
                                    "Tipo de usuario. Opción actual: $tipoSeleccionado. Doble toque para cambiar."
                            }
                            .clickable(
                                interactionSource = tipoUsuarioInteraction,
                                indication = null
                            ) { menuAbierto = true }
                    )

                    // Se despliega un menú de opciones para seleccionar el tipo de usuario, cerrándose al elegir una opción o al perder foco.
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false },
                        properties = PopupProperties(focusable = true)
                    ) {
                        tiposUsuario.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    errorTipoUsuario = null
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Accesibilidad Visual", style = MaterialTheme.typography.bodyMedium)

                // Se muestran RadioButtons para elegir la preferencia de lectura y ajustar el tamaño de tipografía global.
                opcionesPreferencia.forEach { opcion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (opcion == preferenciaSeleccionada),
                            onClick = {
                                preferenciaSeleccionada = opcion
                                onFontSizeModeChange(
                                    if (opcion == "Lectura Aumentada") FontSizeMode.Aumentada
                                    else FontSizeMode.Normal
                                )
                            }
                        )
                        Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Se incorpora un Checkbox para confirmar aceptación de términos, requisito para habilitar el registro.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = aceptaTerminos,
                        onCheckedChange = {
                            aceptaTerminos = it
                            if (it) errorTerminos = null
                        }
                    )
                    Text(text = "Acepto los términos y condiciones")
                }

                if (errorTerminos != null) {
                    Text(
                        text = errorTerminos!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Usuarios registrados (${usuariosViewModel.usuarios.size}/5)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.medium
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Correo",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Tipo",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        if (usuariosViewModel.usuarios.isEmpty()) {
                            Text(
                                text = "Aún no hay usuarios registrados.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        } else {
                            usuariosViewModel.usuarios.forEachIndexed { index, usuario ->
                                val rowBg =
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = usuario.correo,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = usuario.tipoUsuario,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (index < usuariosViewModel.usuarios.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.height(150.dp))
            }
        }

        // Se muestra el Snackbar como overlay superior para evitar que quede oculto por teclado o bottomBar.
        AppSnackbarHost(
            hostState = estadoSnackbar,
            tipoMensaje = tipoMensaje
        )
    }
}
