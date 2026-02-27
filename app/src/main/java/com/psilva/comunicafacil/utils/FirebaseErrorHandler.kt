package com.psilva.comunicafacil.utils

import com.google.firebase.auth.FirebaseAuthException

object FirebaseErrorHandler {
    fun getFriendlyMessage(exception: Exception?): String {
        // Obtenemos el mensaje completo de la excepción para buscar patrones de texto
        val fullMessage = exception?.message ?: ""

        // Prioridad 1: Búsqueda por palabras clave (Detección de errores de reCAPTCHA y Auth)
        return when {
            fullMessage.contains("already in use", ignoreCase = true) ||
                    fullMessage.contains("email-already-in-use", ignoreCase = true) ->
                "Este correo ya está registrado. Intenta iniciar sesión."

            fullMessage.contains("badly formatted", ignoreCase = true) ||
                    fullMessage.contains("invalid email", ignoreCase = true) ||
                    fullMessage.contains("invalid-email", ignoreCase = true) ->
                "El formato del correo no es válido."

            fullMessage.contains("user-not-found", ignoreCase = true) ||
                    fullMessage.contains("no user found", ignoreCase = true) ->
                "No existe ninguna cuenta con este correo electrónico."

            fullMessage.contains("invalid credential", ignoreCase = true) ||
                    fullMessage.contains("wrong password", ignoreCase = true) ||
                    fullMessage.contains("wrong-password", ignoreCase = true) ->
                "Correo o contraseña incorrectos."

            fullMessage.contains("weak password", ignoreCase = true) ||
                    fullMessage.contains("weak-password", ignoreCase = true) ->
                "La contraseña es muy corta (mínimo 6 caracteres)."

            fullMessage.contains("too-many-requests", ignoreCase = true) ->
                "Demasiados intentos. Inténtalo de nuevo más tarde."

            fullMessage.contains("network", ignoreCase = true) ->
                "Error de red. Revisa tu conexión a Internet."

            // Prioridad 2: Si no encuentra palabras clave, intenta por código de error específico de Firebase
            else -> {
                val errorCode = (exception as? FirebaseAuthException)?.errorCode
                when (errorCode) {
                    "auth/email-already-in-use" -> "Este correo ya está registrado."
                    "auth/invalid-email" -> "Correo no válido."
                    "auth/user-not-found" -> "Usuario no encontrado."
                    "auth/wrong-password" -> "Contraseña incorrecta."
                    "auth/too-many-requests" -> "Servicio temporalmente bloqueado."
                    else -> "Error: Comprueba tus datos e intenta nuevamente."
                }
            }
        }
    }
}