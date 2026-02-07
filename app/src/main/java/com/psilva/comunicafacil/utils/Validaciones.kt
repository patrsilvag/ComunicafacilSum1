package com.psilva.comunicafacil.utils

inline fun validarCampo(
    condicion: Boolean,
    onError: () -> Unit
): Boolean {
    return if (!condicion) {
        onError()
        false
    } else {
        true
    }
}