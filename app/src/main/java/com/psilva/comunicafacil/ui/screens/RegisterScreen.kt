package com.psilva.comunicafacil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.PopupProperties

import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import com.psilva.comunicafacil.model.Usuario
import kotlinx.coroutines.launch
import com.psilva.comunicafacil.viewmodel.ResultadoRegistro


import androidx.compose.ui.text.style.TextOverflow


@Composable
fun RegisterScreen(onVolverLogin: () -> Unit,
                   usuariosViewModel: UsuariosViewModel) {

    // --- VARIABLES DE ESTADO ---
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    // Estado para Dropdown (Tipo de Usuario)
    val tiposUsuario = listOf("Estudiante", "Docente", "Apoderado")
    var tipoSeleccionado by remember { mutableStateOf(tiposUsuario.first()) }
    var menuAbierto by remember { mutableStateOf(false) }

    // Estado para RadioButtons (Preferencia)
    val opcionesPreferencia = listOf("Lectura Normal", "Lectura Aumentada")
    var preferenciaSeleccionada by remember { mutableStateOf(opcionesPreferencia.first()) }

    // Estado para Checkbox
    var aceptaTerminos by remember { mutableStateOf(false) }

    val estadoSnackbar = remember { SnackbarHostState() }

    val alcance = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) }
    ) { paddingInterior ->
        val scrollEstado = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingInterior)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollEstado)
        ) {

            Text(
                text = "Registro de usuario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. INPUTS (Ya los tenías)
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. DROPDOWN (Combo Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { menuAbierto = true } // el click lo captura el contenedor
            ) {

                OutlinedTextField(
                    value = tipoSeleccionado,
                    onValueChange = {},
                    label = { Text("Tipo de usuario") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { menuAbierto = true }
                )

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
                                menuAbierto = false
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // 3. RADIO BUTTONS
            Text("Accesibilidad Visual", style = MaterialTheme.typography.bodyMedium)

            opcionesPreferencia.forEach { opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (opcion == preferenciaSeleccionada),
                        onClick = { preferenciaSeleccionada = opcion }
                    )
                    Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. CHECKBOX
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it }
                )
                Text(text = "Acepto los términos y condiciones")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Usuarios registrados (${usuariosViewModel.usuarios.size}/5)",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Encabezado tipo tabla
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Correo",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tipo",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filas
            if (usuariosViewModel.usuarios.isEmpty()) {
                Text(
                    text = "Aún no hay usuarios registrados.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                usuariosViewModel.usuarios.forEach { usuario ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = usuario.correo,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = usuario.tipoUsuario,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    HorizontalDivider()
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    alcance.launch {
                        val usuarioNuevo = Usuario(
                            correo = correo,
                            clave = clave,
                            tipoUsuario = tipoSeleccionado,
                            aceptaTerminos = aceptaTerminos,
                            preferencia = preferenciaSeleccionada
                        )

                        val resultado = usuariosViewModel.registrarUsuario(usuarioNuevo)

                        when (resultado) {
                            is ResultadoRegistro.Ok -> {
                                estadoSnackbar.showSnackbar(resultado.mensaje)
                                // Limpieza básica (opcional, pero recomendable)
                                correo = ""
                                clave = ""
                                aceptaTerminos = false
                                preferenciaSeleccionada = opcionesPreferencia.first()
                                tipoSeleccionado = tiposUsuario.first()
                            }
                            is ResultadoRegistro.Error -> {
                                estadoSnackbar.showSnackbar(resultado.mensaje)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { onVolverLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volver")
            }
        }

    }
}