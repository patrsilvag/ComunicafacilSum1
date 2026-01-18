package com.psilva.comunicafacil.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

    // ✅ Error persistente para el input
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Snackbar para feedback global (TTS no listo, etc.)
    val estadoSnackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    // --- TTS ---
    val context = LocalContext.current
    var ttsListo by remember { mutableStateOf(false) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    val fontSizeMode = LocalAccessibilitySettings.current.fontSizeMode

    val localeEsCL = Locale.Builder()
        .setLanguage("es")
        .setRegion("CL")
        .build()

    DisposableEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
               // val resultado = tts.value?.setLanguage(Locale("es", "ES"))
                val resultado = tts.value?.setLanguage(localeEsCL)
                ttsListo =
                    resultado != TextToSpeech.LANG_MISSING_DATA &&
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

    fun validarMensaje(): Boolean {
        val texto = mensajeIngreso.trim()
        return if (texto.isBlank()) {
            errorMensaje = "Escriba un mensaje antes de mostrarlo"
            false
        } else true
    }

    fun mostrarMensaje() {
        errorMensaje = null
        if (!validarMensaje()) return
        mensajeMostrado = mensajeIngreso.trim()
    }

    fun hablarMensaje() {
        val texto = mensajeMostrado.trim()
        if (texto.isBlank()) return

        if (!ttsListo) {
            alcance.launch {
                tipoMensaje = TipoMensaje.INFO
                estadoSnackbar.showSnackbar(
                    message = "TTS no disponible en este dispositivo/emulador",
                    duration = SnackbarDuration.Short
                )

            }
            return
        }

        tts.value?.stop()
        tts.value?.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "comunicafacil_mensaje"
        )
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = estadoSnackbar, tipoMensaje = tipoMensaje) }

    ) { paddingInterior ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterior)
                .padding(16.dp)
        ) {

            Column {
                Text(
                    text = "Comunicador",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = when (fontSizeMode) {
                        FontSizeMode.Normal -> "Lectura: Normal"
                        FontSizeMode.Aumentada -> "Lectura: Aumentada"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = mensajeIngreso,
                onValueChange = {
                    mensajeIngreso = it
                    errorMensaje = null
                },
                label = { Text("Escribe tu mensaje") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = errorMensaje != null,
                supportingText = {
                    if (errorMensaje != null) Text(errorMensaje!!)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarMensaje() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Mostrar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mensaje mostrado:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (mensajeMostrado.isBlank())
                        "Aquí aparecerá el mensaje para comunicar."
                    else
                        mensajeMostrado,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Acción principal: hablar el mensaje mostrado
            Button(
                onClick = { hablarMensaje() },
                enabled = mensajeMostrado.isNotBlank(), // ttsListo se maneja con snackbar si falla
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Hablar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { onCerrarSesion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
