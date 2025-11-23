package com.example.vistual.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vistual.model.Usuario
import com.example.vistual.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Pruebas unitarias para AuthViewModel
 * 
 * Verifica:
 * - Estados de UI (loading, success, error)
 * - Lógica de login y registro
 * - Manejo de errores
 * - Interacción con Repository
 * 
 * Utiliza reglas especiales para testing de:
 * - LiveData (InstantTaskExecutorRule)
 * - Coroutines (TestDispatcher)
 */
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    // Rule para ejecutar LiveData de forma síncrona en tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Dispatcher de test para coroutines
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: AuthViewModel

    private val testUsuario = Usuario(
        id = 1,
        nombre = "Test User",
        correo = "test@example.com",
        password = "password123"
    )

    @Before
    fun setup() {
        // Configurar dispatcher de test
        Dispatchers.setMain(testDispatcher)
        
        // Crear mocks
        userRepository = mockk()
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        // Restaurar dispatcher original
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `login exitoso actualiza estado correctamente`() = runTest {
        // Given: Repository devuelve éxito
        coEvery { 
            userRepository.validarCredenciales(testUsuario.correo, testUsuario.password) 
        } returns Result.success(testUsuario)

        // When: Hacemos login
        viewModel.login(testUsuario.correo, testUsuario.password)
        advanceUntilIdle() // Espera a que terminen todas las coroutines

        // Then: Estado es logged in
        val state = viewModel.loginState.value
        assertThat(state.isLoggedIn).isTrue()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
        assertThat(state.usuario).isEqualTo(testUsuario)
    }

    @Test
    fun `login con credenciales incorrectas muestra error`() = runTest {
        // Given: Repository devuelve error
        val errorMsg = "Credenciales incorrectas"
        coEvery { 
            userRepository.validarCredenciales(any(), any()) 
        } returns Result.failure(Exception(errorMsg))

        // When: Hacemos login
        viewModel.login("wrong@example.com", "wrongpass")
        advanceUntilIdle()

        // Then: Estado tiene error
        val state = viewModel.loginState.value
        assertThat(state.isLoggedIn).isFalse()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMsg)
    }

    @Test
    fun `login con campos vacíos muestra error de validación`() = runTest {
        // When: Intentamos login con campos vacíos
        viewModel.login("", "")
        advanceUntilIdle()

        // Then: Error de validación
        val state = viewModel.loginState.value
        assertThat(state.errorMessage).contains("campos")
        
        // No debería llamar al repository
        coVerify(exactly = 0) { userRepository.validarCredenciales(any(), any()) }
    }

    @Test
    fun `registro exitoso actualiza estado`() = runTest {
        // Given: Repository registra exitosamente
        coEvery { 
            userRepository.registrarUsuario(any()) 
        } returns Result.success(Unit)

        // When: Registramos usuario
        viewModel.register(testUsuario.nombre, testUsuario.correo, testUsuario.password)
        advanceUntilIdle()

        // Then: Estado de registro es exitoso
        val state = viewModel.registerState.value
        assertThat(state.isRegistered).isTrue()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `registro con correo existente muestra error`() = runTest {
        // Given: Repository devuelve error
        val errorMsg = "El correo ya está registrado"
        coEvery { 
            userRepository.registrarUsuario(any()) 
        } returns Result.failure(Exception(errorMsg))

        // When: Intentamos registrar
        viewModel.register("Test", "existing@example.com", "pass")
        advanceUntilIdle()

        // Then: Estado tiene error
        val state = viewModel.registerState.value
        assertThat(state.isRegistered).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMsg)
    }

    @Test
    fun `registro con campos vacíos muestra error`() = runTest {
        // When: Intentamos registro con campos vacíos
        viewModel.register("", "", "")
        advanceUntilIdle()

        // Then: Error de validación
        val state = viewModel.registerState.value
        assertThat(state.errorMessage).isNotNull()
        
        // No debería llamar al repository
        coVerify(exactly = 0) { userRepository.registrarUsuario(any()) }
    }

    @Test
    fun `logout llama a repository`() {
        // When: Hacemos logout
        viewModel.logout()

        // Then: Se llama al repository
        verify { userRepository.logout() }
    }

    @Test
    fun `checkLoginStatus detecta usuario logueado`() = runTest {
        // Given: Hay usuario logueado
        every { userRepository.getLoggedInUser() } returns testUsuario

        // When: Verificamos estado
        viewModel.checkLoginStatus()
        advanceUntilIdle()

        // Then: Estado actualizado
        val state = viewModel.loginState.value
        assertThat(state.isLoggedIn).isTrue()
        assertThat(state.usuario).isEqualTo(testUsuario)
    }

    @Test
    fun `estado de loading se activa durante operaciones`() = runTest {
        // Given: Repository tiene delay simulado
        coEvery { 
            userRepository.validarCredenciales(any(), any()) 
        } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(testUsuario)
        }

        // When: Iniciamos login
        viewModel.login(testUsuario.correo, testUsuario.password)
        
        // Then: Loading es true inmediatamente
        assertThat(viewModel.loginState.value.isLoading).isTrue()
        
        // After: Loading es false al terminar
        advanceUntilIdle()
        assertThat(viewModel.loginState.value.isLoading).isFalse()
    }
}
