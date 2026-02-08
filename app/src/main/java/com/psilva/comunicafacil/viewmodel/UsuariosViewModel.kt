package com.psilva.comunicafacil.viewmodel

import androidx.lifecycle.ViewModel
import com.psilva.comunicafacil.data.ResultadoRegistro
import com.psilva.comunicafacil.data.UsuariosDataSource
import com.psilva.comunicafacil.data.UsuariosRepository
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.utils.normalizarCorreo

class UsuariosViewModel(
    private val repository: UsuariosDataSource = UsuariosRepository()
) : ViewModel() {

    val usuarios: List<Usuario>
        get() = repository.obtenerUsuarios()

    fun registrarUsuario(
        correo: String,
        clave: String,
        tipoUsuario: String,
        aceptaTerminos: Boolean,
        preferencia: String, // Recibe el String
        onResultado: (ResultadoRegistro) -> Unit
    ) {
        val usuario = Usuario(
            correo = correo.normalizarCorreo(),
            clave = clave,
            tipoUsuario = tipoUsuario,
            aceptaTerminos = aceptaTerminos,
            preferencia = preferencia // Se asigna al modelo
        )

        repository.registrarUsuario(usuario).fold(
            onSuccess = {
                onResultado(ResultadoRegistro.Ok("Usuario registrado con éxito"))
            },
            onFailure = { error ->
                onResultado(
                    ResultadoRegistro.Error(
                        error.message ?: "Error desconocido"
                    )
                )
            }
        )
    }

   // fun validarLogin(correo: String, clave: String): Boolean =
        // repository.validarCredenciales(correo, clave)

    fun existeCorreo(correo: String): Boolean =
        repository.existeCorreo(correo)

    fun login(correo: String, clave: String): Usuario? {
        return repository.obtenerUsuarioPorCredenciales(correo, clave)
    }

}
