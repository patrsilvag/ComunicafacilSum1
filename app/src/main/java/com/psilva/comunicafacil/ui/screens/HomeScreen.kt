package com.psilva.comunicafacil.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

val String.isSpeakable: Boolean get() = this.trim().isNotBlank()

@Composable
fun HomeScreen(onCerrarSesion: () -> Unit) {
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()
    val estadoSnackbar = remember { SnackbarHostState() }
    val TAG = "STT_DEBUG" // Etiqueta para Logcat

    // --- Estados de Mensajería ---
    var mensajeIngreso by remember { mutableStateOf("") }
    var mensajeMostrado by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var tipoMensaje by remember { mutableStateOf(TipoMensaje.INFO) }

    // --- Estados de Reconocimiento de Voz (Semana 8: Feedback Dinámico) ---
    var estaEscuchando by remember { mutableStateOf(false) }
    var textoEstadoStt by remember { mutableStateOf("Presione el micrófono para hablar") }

    // --- TTS Logic ---
    var ttsListo by remember { mutableStateOf(false) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val fontSizeMode = LocalAccessibilitySettings.current.fontSizeMode
    val localeEsCL = Locale.Builder().setLanguage("es").setRegion("CL").build()

    // --- Configuración SpeechToText ---
    val intentReconocimiento = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }
    }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    DisposableEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.setLanguage(localeEsCL)
                ttsListo = true
            }
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech: Sistema listo")
                estaEscuchando = true
                textoEstadoStt = "Listo para hablar, te escucho..."
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech: Usuario comenzó a hablar")
                textoEstadoStt = "Escuchando... 🎤"
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Log opcional para ver niveles de audio en Logcat
                if (rmsdB > 5) Log.v(TAG, "onRmsChanged: Nivel de audio detectado")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived: Recibiendo datos de audio")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech: Fin de captura de audio")
                estaEscuchando = false
                textoEstadoStt = "Procesando audio... 🔄"
            }

            override fun onError(error: Int) {
                Log.e(TAG, "onError: Código de error STT: $error")
                estaEscuchando = false
                textoEstadoStt = "Error al reconocer voz"
                alcance.launch {
                    tipoMensaje = TipoMensaje.ERROR
                    estadoSnackbar.showSnackbar("Error en micrófono o reconocimiento")
                }
            }

            override fun onResults(results: Bundle?) {
                Log.d(TAG, "onResults: Éxito en el reconocimiento")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    mensajeIngreso = matches[0]
                    textoEstadoStt = "Texto capturado con éxito ✅"
                }
                estaEscuchando = false
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults: Procesando fragmentos...")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent: Evento código $eventType")
            }
        }

        speechRecognizer.setRecognitionListener(listener)

        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            speechRecognizer.destroy()
        }
    }

    // Funciones de acción
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

                // INPUT CON FEEDBACK DINÁMICO (Semana 8)
                OutlinedTextField(
                    value = mensajeIngreso,
                    onValueChange = { mensajeIngreso = it; errorMensaje = null },
                    label = { Text("Escribe o usa el micrófono") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    isError = errorMensaje != null,
                    supportingText = {
                        Text(
                            text = textoEstadoStt,
                            color = if (estaEscuchando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (!estaEscuchando) {
                                speechRecognizer.startListening(intentReconocimiento)
                            } else {
                                speechRecognizer.stopListening()
                            }
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

                // --- BLOQUE DE GEOLOCALIZACIÓN ---
                val contextUbicacion = LocalContext.current
                val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(contextUbicacion) }
                var textoCoordenadas by remember { mutableStateOf("Ubicación: No obtenida") }

                // Launcher para solicitar permisos en tiempo real
                @SuppressLint("MissingPermission")
                val launcherPermisosGps = rememberLauncherForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
                ) { permisos ->
                    val concedido = permisos[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            permisos[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

                    if (concedido) {
                        try {
                            // VERIFICACIÓN DE SEGURIDAD EXPLICITA (Esto quita el error)
                            if (androidx.core.app.ActivityCompat.checkSelfPermission(
                                    contextUbicacion,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                                androidx.core.app.ActivityCompat.checkSelfPermission(
                                    contextUbicacion,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                    if (loc != null) {
                                        textoCoordenadas = "Lat: ${loc.latitude}, Lon: ${loc.longitude}"
                                        Log.d("GPS_DEBUG", "Ubicación obtenida: $textoCoordenadas")
                                    } else {
                                        textoCoordenadas = "GPS activo, buscando señal..."
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("GPS_DEBUG", "Error: ${e.message}")
                        }
                    } else {
                        textoCoordenadas = "Permiso de ubicación denegado"
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Interfaz de Usuario para la ubicación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📍 Localización del Dispositivo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = textoCoordenadas,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Button(
                            onClick = {
                                launcherPermisosGps.launch(arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                ))
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ubicar")
                        }
                    }
                }

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