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
import java.util.Locale

@Composable
fun HomeScreen(onCerrarSesion: () -> Unit) {

    var mensajeIngreso by remember { mutableStateOf("") }
    var mensajeMostrado by remember { mutableStateOf("") }

    // --- TTS ---
    val context = LocalContext.current
    var ttsListo by remember { mutableStateOf(false) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val resultado = tts.value?.setLanguage(Locale("es", "ES"))
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

    fun hablarMensaje() {
        val texto = mensajeMostrado.trim()
        if (texto.isBlank() || !ttsListo) return

        tts.value?.stop()
        tts.value?.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "comunicafacil_mensaje"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Comunicador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = mensajeIngreso,
            onValueChange = { mensajeIngreso = it },
            label = { Text("Escribe tu mensaje") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                mensajeMostrado = mensajeIngreso.trim()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
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

        //  Botón TTS
        Button(
            onClick = { hablarMensaje() },
            enabled = ttsListo && mensajeMostrado.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Hablar")
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { onCerrarSesion() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}
