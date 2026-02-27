package com.psilva.comunicafacil.ui.screens

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
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

// PROPIEDAD DE EXTENSIÓN: Optimiza la validación de texto
val String.isSpeakable: Boolean get() = this.trim().isNotBlank()

@Composable
fun HomeScreen(onCerrarSesion: () -> Unit) {
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()
    val estadoSnackbar = remember { SnackbarHostState() }

    // --- Estados de Mensajería ---
    var mensajeIngreso by remember { mutableStateOf("") }
    var mensajeMostrado by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    // --- Estados de Reconocimiento de Voz (Semana 8) ---
    var estaEscuchando by remember { mutableStateOf(false) }
    var textoEstadoStt by remember { mutableStateOf("Presione el micrófono para hablar") }

    // --- TTS Logic ---
    var ttsListo by remember { mutableStateOf(false) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val fontSizeMode = LocalAccessibilitySettings.current.fontSizeMode
    val localeEsCL = Locale.Builder().setLanguage("es").setRegion("CL").build()

    // --- Configuración SpeechToText (Semana 8: Criterio Técnico) ---
    val intentReconocimiento = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }
    }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    DisposableEffect(Unit) {
        // Inicializar TTS
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.setLanguage(localeEsCL)
                ttsListo = true
            }
        }

        // Listener de Reconocimiento (Criterios Semana 8)
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                estaEscuchando = true
                textoEstadoStt = "Listo para hablar..." // Estado: Listo para hablar
            }

            override fun onResults(results: Bundle?) {
                estaEscuchando = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    mensajeIngreso = matches[0] // Audio transformado a texto
                    textoEstadoStt = "Texto capturado"
                }
            }

            override fun onError(error: Int) {
                estaEscuchando = false
                textoEstadoStt = "Error en el reconocimiento"
                alcance.launch {
                    tipoMensaje = TipoMensaje.ERROR
                    estadoSnackbar.showSnackbar("Error al acceder al micrófono")
                }
            }

            // Métodos obligatorios de la interfaz
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { estaEscuchando = false }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)

        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            speechRecognizer.destroy()
        }
    }

    fun procesarYMostrarMensaje() {
        errorMensaje = null
        if (!mensajeIngreso.isSpeakable) {
            errorMensaje = "Escriba o dicte un mensaje antes de mostrarlo"
            return
        }
        mensajeMostrado = mensajeIngreso.trim()
        mensajeIngreso = ""
    }

    fun hablarMensaje() {
        if (!mensajeMostrado.isSpeakable || !ttsListo) return
        tts.value?.speak(mensajeMostrado, TextToSpeech.QUEUE_FLUSH, null, "comunicafacil_id")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { AppSnackbarHost(estadoSnackbar, tipoMensaje) }
        ) { paddingInterior ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterior)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Comunicador",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Text(
                    text = if (fontSizeMode == FontSizeMode.Aumentada) "Lectura: Aumentada" else "Lectura: Normal",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                // INPUT DE MENSAJE CON MICRÓFONO (Semana 8)
                OutlinedTextField(
                    value = mensajeIngreso,
                    onValueChange = { mensajeIngreso = it; errorMensaje = null },
                    label = { Text("Escribe o usa el micrófono") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    isError = errorMensaje != null,
                    supportingText = { Text(if (estaEscuchando) "Escuchando..." else textoEstadoStt) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (!estaEscuchando) speechRecognizer.startListening(intentReconocimiento)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Micrófono",
                                tint = if (estaEscuchando) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { procesarYMostrarMensaje() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Mostrar en pantalla", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // PIZARRA DE COMUNICACIÓN
                Text("Mensaje para comunicar:", style = MaterialTheme.typography.titleMedium)

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (mensajeMostrado.isEmpty()) "..." else mensajeMostrado,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { hablarMensaje() },
                    enabled = mensajeMostrado.isSpeakable,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Hablar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(40.dp))

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