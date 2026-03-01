package com.psilva.comunicafacil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psilva.comunicafacil.model.UbicacionUsuario // Importa desde model
import com.psilva.comunicafacil.data.UbicacionRepository // Importa desde data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UbicacionViewModel(
    private val repository: UbicacionRepository = UbicacionRepository(),
    private val userId: String // Debes pasar el UID de Firebase cuando lo instancies
) : ViewModel() {

    private val _ubicacionActual = MutableStateFlow<UbicacionUsuario?>(null)
    val ubicacionActual: StateFlow<UbicacionUsuario?> = _ubicacionActual

    init {
        observarUbicacion()
    }

    private fun observarUbicacion() {
        viewModelScope.launch {
            repository.obtenerUbicacionRealTime(userId).collect {
                _ubicacionActual.value = it
            }
        }
    }

    fun procesarNuevaUbicacion(lat: Double, lon: Double, ciudad: String) {
        viewModelScope.launch {
            val nuevaUbicacion = UbicacionUsuario(
                latitud = lat,
                longitud = lon,
                ciudad = ciudad,
                ultimaActualizacion = System.currentTimeMillis()
            )
            repository.guardarOActualizarUbicacion(userId, nuevaUbicacion)
        }
    }

    fun borrarRastroUbicacion() {
        viewModelScope.launch {
            repository.eliminarUbicacion(userId)
        }
    }
}