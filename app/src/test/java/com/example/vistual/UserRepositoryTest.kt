package com.example.vistual.repository

import android.content.SharedPreferences
import com.example.vistual.db.UsuarioDao
import com.example.vistual.model.Usuario
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Pruebas unitarias para UserRepository
 * 
 * Verifica:
 * - Registro de usuarios
 * - Validación de credenciales
 * - Gestión de sesión
 * - Manejo de SharedPreferences
 */
class UserRepositoryTest {

    private lateinit var usuarioDao: UsuarioDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repository: UserRepository

    private val testUsuario = Usuario(
        id = 1,
        nombre = "Test User",
        correo = "test@example.com",
        password = "password123"
    )

    @Before
    fun setup() {
        usuarioDao = mockk()
        editor = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true)
        
        // Mock del editor de SharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.apply() } just Runs
        
        repository = UserRepository(usuarioDao, sharedPreferences)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `registrarUsuario exitoso cuando correo no existe`() = runTest {
        // Given: No existe usuario con ese correo
        coEvery { usuarioDao.getUsuarioByCorreo(testUsuario.correo) } returns null
        coEvery { usuarioDao.insertUsuario(testUsuario) } returns 1L

        // When: Registramos usuario
        val result = repository.registrarUsuario(testUsuario)

        // Then: Operación exitosa
        assertThat(result.isSuccess).isTrue()
        coVerify { usuarioDao.insertUsuario(testUsuario) }
    }

    @Test
    fun `registrarUsuario falla cuando correo ya existe localmente`() = runTest {
        // Given: Ya existe usuario con ese correo
        coEvery { usuarioDao.getUsuarioByCorreo(testUsuario.correo) } returns testUsuario

        // When: Intentamos registrar
        val result = repository.registrarUsuario(testUsuario)

        // Then: Falla con mensaje apropiado
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("ya está registrado")
        coVerify(exactly = 0) { usuarioDao.insertUsuario(any()) }
    }

    @Test
    fun `validarCredenciales guarda sesión cuando son correctas`() = runTest {
        // Given: Credenciales correctas
        coEvery { 
            usuarioDao.validarCredenciales(testUsuario.correo, testUsuario.password) 
        } returns testUsuario

        // When: Validamos credenciales
        val result = repository.validarCredenciales(testUsuario.correo, testUsuario.password)

        // Then: Éxito y sesión guardada
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testUsuario)
        verify { editor.putInt("user_id", testUsuario.id) }
        verify { editor.putString("user_email", testUsuario.correo) }
        verify { editor.apply() }
    }

    @Test
    fun `validarCredenciales falla cuando credenciales son incorrectas`() = runTest {
        // Given: Credenciales incorrectas
        coEvery { 
            usuarioDao.validarCredenciales(any(), any()) 
        } returns null

        // When: Validamos
        val result = repository.validarCredenciales("wrong@example.com", "wrongpass")

        // Then: Falla
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("incorrectas")
    }

    @Test
    fun `logout limpia SharedPreferences`() {
        // When: Cerramos sesión
        repository.logout()

        // Then: Se eliminan datos de sesión
        verify { editor.remove("user_id") }
        verify { editor.remove("user_email") }
        verify { editor.apply() }
    }

    @Test
    fun `getLoggedInUser devuelve usuario cuando hay sesión activa`() {
        // Given: Hay sesión guardada
        every { sharedPreferences.getInt("user_id", -1) } returns 1
        every { sharedPreferences.getString("user_email", null) } returns "test@example.com"
        every { sharedPreferences.getString("user_name", null) } returns "Test User"

        // When: Obtenemos usuario logueado
        val user = repository.getLoggedInUser()

        // Then: Obtenemos datos correctos
        assertThat(user).isNotNull()
        assertThat(user?.id).isEqualTo(1)
        assertThat(user?.correo).isEqualTo("test@example.com")
    }

    @Test
    fun `getLoggedInUser devuelve null cuando no hay sesión`() {
        // Given: No hay sesión
        every { sharedPreferences.getInt("user_id", -1) } returns -1
        every { sharedPreferences.getString("user_email", null) } returns null

        // When: Intentamos obtener usuario
        val user = repository.getLoggedInUser()

        // Then: Obtenemos null
        assertThat(user).isNull()
    }

    @Test
    fun `isUserLoggedIn devuelve true cuando hay sesión`() {
        // Given: Hay sesión
        every { sharedPreferences.getInt("user_id", -1) } returns 1

        // When: Verificamos
        val isLoggedIn = repository.isUserLoggedIn()

        // Then: Es true
        assertThat(isLoggedIn).isTrue()
    }

    @Test
    fun `isUserLoggedIn devuelve false cuando no hay sesión`() {
        // Given: No hay sesión
        every { sharedPreferences.getInt("user_id", -1) } returns -1

        // When: Verificamos
        val isLoggedIn = repository.isUserLoggedIn()

        // Then: Es false
        assertThat(isLoggedIn).isFalse()
    }
}
