package com.psilva.comunicafacil.data

sealed class ResultadoRegistro {
    data class Ok(val mensaje: String) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}
