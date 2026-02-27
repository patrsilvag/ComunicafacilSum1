package com.psilva.comunicafacil

import com.psilva.comunicafacil.data.UsuariosDataSource
import com.psilva.comunicafacil.data.UsuariosRepository
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.ui.state.RegisterUiState
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuariosViewModelTest {

    private lateinit var viewModel: UsuariosViewModel
    private val repository = mockk<UsuariosRepository>() // Mockeamos el repositorio
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Pasamos el mock al constructor del ViewModel
        viewModel = UsuariosViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Test 1 - Validar formato correo antes de Firebase Auth`() = runTest {
        // Acción: Proporcionamos los 5 parámetros requeridos con un correo inválido
        viewModel.registrarUsuario(
            correo = "correo_sin_arroba",
            clave = "123456",
            tipoUsuario = "Paciente",
            aceptaTerminos = true,
            preferencia = "Visual"
        )
        advanceUntilIdle()

        // Verificación usando las propiedades de RegisterUiState
        val estado = viewModel.uiState.value
        assertTrue("El estado debería indicar un error", estado.esError)
        assertEquals("Formato de correo inválido", estado.mensaje)
    }

    @Test
    fun `Test 2 - Registro exitoso cambia estado del ViewModel`() = runTest {
        // GIVEN: Un repositorio que siempre devuelve éxito
        coEvery { repository.registrarUsuario(any()) } returns Result.success(Unit)

        // WHEN: Se llama a registrarUsuario con datos válidos
        viewModel.registrarUsuario(
            correo = "alumno@inacap.cl",
            clave = "password123",
            tipoUsuario = "Paciente",
            aceptaTerminos = true,
            preferencia = "Vocal"
        )

        // Avanzamos el reloj virtual para que la corrutina termine
        advanceUntilIdle()

        // THEN: El estado debe reflejar que no hay carga, no hay error y hubo éxito
        val estadoFinal = viewModel.uiState.value

        assertTrue("El registro debe ser exitoso", estadoFinal.registroExitoso)
        assertTrue("No debería haber error", !estadoFinal.esError)
        assertTrue("Ya no debería estar cargando", !estadoFinal.cargando)
        assertEquals("Usuario registrado correctamente", estadoFinal.mensaje)
    }

    @Test
    fun `Test Extra - Validar largo de clave`() = runTest {
        // Acción: Clave muy corta
        viewModel.registrarUsuario(
            correo = "test@ejemplo.com",
            clave = "123",
            tipoUsuario = "Paciente",
            aceptaTerminos = true,
            preferencia = "Visual"
        )
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.esError)
        assertEquals("La contraseña debe tener al menos 6 caracteres", estado.mensaje)
    }
}