package com.example.vistual.repository

import com.example.vistual.db.PrendaDao
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.ColorPrenda
import com.example.vistual.model.Prenda
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat

/**
 * Pruebas unitarias para PrendaRepository
 * 
 * Verifica:
 * - Operaciones CRUD funcionan correctamente
 * - Interacción correcta con el DAO
 * - Manejo de errores
 * 
 * Utiliza:
 * - MockK para crear mocks de dependencias
 * - Truth para assertions más legibles
 * - Coroutines test para testing asíncrono
 */
class PrendaRepositoryTest {

    // Mocks
    private lateinit var prendaDao: PrendaDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: PrendaRepository

    // Datos de prueba
    private val testPrenda = Prenda(
        id = 1,
        nombre = "Camisa Azul",
        categoria = CategoriaPrenda.CAMISA,
        color = ColorPrenda.AZUL,
        imagenPath = "/test/path.jpg",
        usuarioId = 1,
        fechaCreacion = "1234567890"
    )

    @Before
    fun setup() {
        // Crear mocks
        prendaDao = mockk()
        sharedPreferences = mockk(relaxed = true) // relaxed = no necesita definir todos los métodos
        
        // Crear repository con mocks
        repository = PrendaRepository(prendaDao, sharedPreferences)
    }

    @After
    fun tearDown() {
        // Limpiar mocks después de cada test
        clearAllMocks()
    }

    @Test
    fun `getAllPrendas devuelve Flow con lista de prendas`() = runTest {
        // Given: DAO devuelve un Flow con prendas
        val prendas = listOf(testPrenda)
        every { prendaDao.getAllPrendas(1) } returns flowOf(prendas)

        // When: Solicitamos todas las prendas
        val result = repository.getAllPrendas(1)

        // Then: Verificamos que se obtiene el Flow correcto
        result.collect { prendasResult ->
            assertThat(prendasResult).isEqualTo(prendas)
        }
        verify { prendaDao.getAllPrendas(1) }
    }

    @Test
    fun `insert guarda prenda en base de datos local`() = runTest {
        // Given: DAO insertará la prenda exitosamente
        coEvery { prendaDao.insertPrenda(testPrenda) } just Runs

        // When: Insertamos una prenda
        val result = repository.insert(testPrenda)

        // Then: Operación exitosa
        assertThat(result.isSuccess).isTrue()
        coVerify { prendaDao.insertPrenda(testPrenda) }
    }

    @Test
    fun `insert maneja errores correctamente`() = runTest {
        // Given: DAO lanza excepción
        val exception = Exception("Error de BD")
        coEvery { prendaDao.insertPrenda(testPrenda) } throws exception

        // When: Intentamos insertar
        val result = repository.insert(testPrenda)

        // Then: Result contiene el error
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `deleteById elimina prenda correctamente`() = runTest {
        // Given: DAO eliminará la prenda
        coEvery { prendaDao.deletePrendaById(1) } just Runs

        // When: Eliminamos prenda
        val result = repository.deleteById(1)

        // Then: Operación exitosa
        assertThat(result.isSuccess).isTrue()
        coVerify { prendaDao.deletePrendaById(1) }
    }

    @Test
    fun `getPrendaById devuelve prenda correcta`() = runTest {
        // Given: DAO devuelve una prenda
        coEvery { prendaDao.getPrendaById(1) } returns testPrenda

        // When: Buscamos por ID
        val result = repository.getPrendaById(1)

        // Then: Obtenemos la prenda correcta
        assertThat(result).isEqualTo(testPrenda)
        coVerify { prendaDao.getPrendaById(1) }
    }

    @Test
    fun `getPrendaById devuelve null si no existe`() = runTest {
        // Given: DAO devuelve null
        coEvery { prendaDao.getPrendaById(999) } returns null

        // When: Buscamos prenda inexistente
        val result = repository.getPrendaById(999)

        // Then: Obtenemos null
        assertThat(result).isNull()
    }

    @Test
    fun `getContadorPrendas devuelve número correcto`() = runTest {
        // Given: Hay 5 prendas
        coEvery { prendaDao.getContadorPrendas(1) } returns 5

        // When: Obtenemos contador
        val result = repository.getContadorPrendas(1)

        // Then: Obtenemos 5
        assertThat(result).isEqualTo(5)
    }
}
