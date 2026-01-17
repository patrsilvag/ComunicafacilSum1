package com.psilva.comunicafacil.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        modifier = modifier,
        singleLine = true,
        isError = isError,
        supportingText = {
            if (!supportingText.isNullOrBlank()) {
                Text(supportingText)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        )
    )
}
