package com.psilva.comunicafacil.ui.components

import android.util.Patterns
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    onValidityChange: ((Boolean) -> Unit)? = null, // 👈 clave
    supportingText: String? = null
) {
    val esValido by remember(value) {
        mutableStateOf(
            Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
        )
    }

    // Notifica al padre si lo pide
    LaunchedEffect(esValido) {
        onValidityChange?.invoke(esValido)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null, // null porque el label ya describe el campo
                tint = MaterialTheme.colorScheme.primary // Usamos tu azul profesional
            )
        },
        modifier = modifier,
        singleLine = true,
        isError = value.isNotBlank() && !esValido,
        supportingText = {
            when {
                !supportingText.isNullOrBlank() -> Text(supportingText)
                value.isNotBlank() && !esValido -> Text("Ingrese un correo válido")
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        )
    )
}
