package com.psilva.comunicafacil.viewmodel

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
import com.psilva.comunicafacil.utils.normalizarCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuariosDataSource = UsuariosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // 🔥 NUEVO: Lista para la simulación local con datos reales de Firebase
    var usuariosRealtime by mutableStateOf<List<Usuario>>(emptyList())
        private set

    //private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val database by lazy { FirebaseDatabase.getInstance().getReference("usuarios") }

    // 🔥 NUEVO: Escucha cambios en Firebase para llenar la tabla de la UI
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
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun registrarUsuario(
        correo: String,
        clave: String,
        tipoUsuario: String,
        aceptaTerminos: Boolean,
        preferencia: String
    ) {
        // --- 🧪 INICIO DE VALIDACIONES PREVIAS (Para JUnit) ---

        // Regex para validar formato de correo
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

        if (!correo.matches(emailRegex)) {
            _uiState.value = RegisterUiState(
                mensaje = "Formato de correo inválido",
                esError = true
            )
            return
        }

        if (clave.length < 6) {
            _uiState.value = RegisterUiState(
                mensaje = "La contraseña debe tener al menos 6 caracteres",
                esError = true
            )
            return
        }

        if (!aceptaTerminos) {
            _uiState.value = RegisterUiState(
                mensaje = "Debes aceptar los términos y condiciones",
                esError = true
            )
            return
        }

        // --- 🧪 FIN DE VALIDACIONES ---
        viewModelScope.launch {
            _uiState.value = RegisterUiState(cargando = true)
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
                    _uiState.value = RegisterUiState(
                        cargando = false,
                        mensaje = "Usuario registrado correctamente",
                        registroExitoso = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = RegisterUiState(
                        cargando = false,
                        mensaje = error.message ?: "Error desconocido",
                        esError = true
                    )
                }
            )
        }
    }

    fun login(correo: String, clave: String, onResultado: (Result<Usuario>) -> Unit) {
        viewModelScope.launch {
            onResultado(repository.login(correo, clave))
        }
    }

    fun recuperarPassword(correo: String, onResultado: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResultado(repository.recuperarPassword(correo))
        }
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }
}