package com.psilva.comunicafacil.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.psilva.comunicafacil.data.UsuariosDataSource
import com.psilva.comunicafacil.data.UsuariosRepository
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.ui.state.RegisterUiState
import com.psilva.comunicafacil.utils.FirebaseErrorHandler
import com.psilva.comunicafacil.utils.normalizarCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuariosDataSource = UsuariosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    var usuariosRealtime by mutableStateOf<List<Usuario>>(emptyList())
        private set

    private val database by lazy { FirebaseDatabase.getInstance().getReference("usuarios") }

    fun cargarUsuarios() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Usuario>()
                snapshot.children.forEach { child ->
                    val user = child.getValue(Usuario::class.java)
                    user?.let { lista.add(it) }
                }
                usuariosRealtime = lista
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("DB_ERROR", "Error al cargar usuarios: ${error.message}")
            }
        })
    }

    fun registrarUsuario(
        correo: String,
        clave: String,
        tipoUsuario: String,
        aceptaTerminos: Boolean,
        preferencia: String
    ) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

        if (!correo.matches(emailRegex)) {
            _uiState.update { it.copy(mensaje = "Formato de correo inválido", esError = true) }
            return
        }

        if (clave.length < 6) {
            _uiState.update { it.copy(mensaje = "La contraseña debe tener al menos 6 caracteres", esError = true) }
            return
        }

        if (!aceptaTerminos) {
            _uiState.update { it.copy(mensaje = "Debes aceptar los términos y condiciones", esError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }

            val usuario = Usuario(
                correo = correo.normalizarCorreo(),
                clave = clave,
                tipoUsuario = tipoUsuario,
                aceptaTerminos = aceptaTerminos,
                preferencia = preferencia
            )

            val resultado = repository.registrarUsuario(usuario)

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = "Usuario registrado correctamente",
                            registroExitoso = true,
                            esError = false
                        )
                    }
                },
                onFailure = { error ->
                    val mensajeTraducido = FirebaseErrorHandler.getFriendlyMessage(error as? Exception)
                    Log.e("AUTH_DEBUG", "Error en Registro: $mensajeTraducido")

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = mensajeTraducido,
                            esError = true,
                            registroExitoso = false
                        )
                    }
                }
            )
        }
    }

    fun login(correo: String, clave: String, onResultado: (Result<Usuario>) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            val resultado = repository.login(correo, clave)

            resultado.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(cargando = false, esError = false) }
                    onResultado(Result.success(user))
                },
                onFailure = { error ->
                    val mensajeTraducido = FirebaseErrorHandler.getFriendlyMessage(error as? Exception)
                    Log.e("AUTH_DEBUG", "Error en Login: $mensajeTraducido")

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = mensajeTraducido,
                            esError = true
                        )
                    }
                    onResultado(Result.failure(Exception(mensajeTraducido)))
                }
            )
        }
    }

    fun recuperarPassword(correo: String, onResultado: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            val resultado = repository.recuperarPassword(correo)

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = "Se ha enviado un correo para restablecer tu contraseña",
                            esError = false
                        )
                    }
                    onResultado(Result.success(Unit))
                },
                onFailure = { error ->
                    val mensajeTraducido = FirebaseErrorHandler.getFriendlyMessage(error as? Exception)
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = mensajeTraducido,
                            esError = true
                        )
                    }
                    onResultado(Result.failure(Exception(mensajeTraducido)))
                }
            )
        }
    }

    fun limpiarMensaje() {
        _uiState.update {
            it.copy(
                mensaje = null,
                esError = false,
                cargando = false, // Añade esto para asegurar reset completo
                registroExitoso = false
            )
        }
    }
}