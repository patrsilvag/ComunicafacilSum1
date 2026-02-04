package com.psilva.comunicafacil.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale
import com.psilva.comunicafacil.ui.components.AppSnackbarHost
import com.psilva.comunicafacil.ui.components.TipoMensaje
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.ui.settings.LocalAccessibilitySettings

@Composable
fun HomeScreen(onCerrarSesion: () -> Unit) {
    var mensajeIngreso by remember { mutableStateOf("") }
    var mensajeMostrado by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    // --- TTS Logic ---
    val context = LocalContext.current
    var ttsListo by remember { mutableStateOf(false) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    val fontSizeMode = LocalAccessibilitySettings.current.fontSizeMode
    val localeEsCL = Locale.Builder().setLanguage("es").setRegion("CL").build()

    DisposableEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val resultado = tts.value?.setLanguage(localeEsCL)
                ttsListo = resultado != TextToSpeech.LANG_MISSING_DATA &&
                        resultado != TextToSpeech.LANG_NOT_SUPPORTED
            } else {
                ttsListo = false
            }
        }
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            tts.value = null
        }
    }

    fun hablarMensaje() {
        val texto = mensajeMostrado.trim()
        if (texto.isBlank()) return
        if (!ttsListo) {
            alcance.launch {
                tipoMensaje = TipoMensaje.INFO
                estadoSnackbar.showSnackbar("TTS no disponible en este dispositivo")
            }
            return
        }
        tts.value?.stop()
        tts.value?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "comunicafacil_mensaje")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMensaje) }
        ) { paddingInterior ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterior)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // CABECERA PROFESIONAL
                Text(
                    text = "Comunicador",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    text = when (fontSizeMode) {
                        FontSizeMode.Normal -> "Lectura: Normal"
                        FontSizeMode.Aumentada -> "Lectura: Aumentada"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // INPUT DE MENSAJE (Unificado a 16.dp)
                OutlinedTextField(
                    value = mensajeIngreso,
                    onValueChange = { mensajeIngreso = it; errorMensaje = null },
                    label = { Text("Escribe tu mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    isError = errorMensaje != null,
                    supportingText = { if (errorMensaje != null) Text(errorMensaje!!) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (mensajeIngreso.trim().isBlank()) {
                            errorMensaje = "Escriba un mensaje antes de mostrarlo"
                        } else {
                            mensajeMostrado = mensajeIngreso.trim()
                            mensajeIngreso = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Mostrar en pantalla", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // PIZARRA DE COMUNICACIÓN (ElevatedCard para jerarquía)
                Text("Mensaje para comunicar:", style = MaterialTheme.typography.titleMedium)

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (mensajeMostrado.isBlank()) "..." else mensajeMostrado,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN ACCIÓN PRINCIPAL (USANDO AMARILLO ÁMBAR DEL TEMA)
                Button(
                    onClick = { hablarMensaje() },
                    enabled = mensajeMostrado.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary, // Amarillo Ámbar de Theme.kt
                        contentColor = MaterialTheme.colorScheme.onSecondary // Negro de Theme.kt
                    )
                ) {
                    Text("Hablar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // CIERRE DE SESIÓN
                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}