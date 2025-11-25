package com.example.vistual.repository

import com.example.vistual.api.ApiService
import com.example.vistual.api.models.PrendaDto
import com.example.vistual.db.PrendaDao
import com.example.vistual.model.Prenda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository para gestionar datos de prendas
 * Implementa patrón Repository de MVVM
 * Consume datos de:
 * - API REST (externa) mediante Retrofit
 * - Room Database (interna) para persistencia local
 */
class PrendaRepository(
    private val prendaDao: PrendaDao,
    private val apiService: ApiService,
    private val getAuthToken: () -> String?
) {

    /**
     * Obtiene todas las prendas de un usuario
     * Primero intenta sincronizar con el API, luego retorna datos locales
     */
    fun getAllPrendas(usuarioId: Int): Flow<List<Prenda>> {
        // Retornar Flow de Room para reactividad
        return prendaDao.getAllPrendas(usuarioId)
    }
    
    /**
     * Sincroniza prendas desde el API REST
     * Debe llamarse periódicamente o al inicio de la app
     */
    suspend fun syncPrendasFromApi(usuarioId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getAuthToken()
            if (token != null) {
                val response = apiService.getAllPrendas("Bearer $token")
                
                if (response.isSuccessful && response.body() != null) {
                    val prendasDto = response.body()!!
                    
                    // Convertir DTOs a entidades Room y guardar en local
                    val prendas = prendasDto.mapNotNull { dto: PrendaDto ->
                        try {
                            Prenda(
                                id = dto.id,
                                nombre = dto.nombre,
                                categoria = com.example.vistual.model.CategoriaPrenda.valueOf(dto.categoria.uppercase()),
                                color = com.example.vistual.model.ColorPrenda.valueOf(dto.color.uppercase()),
                                imagenPath = dto.imagenUrl ?: "",
                                usuarioId = dto.usuarioId
                            )
                        } catch (e: Exception) {
                            null // Ignorar prendas con categoría/color inválido
                        }
                    }
                    
                    // Actualizar base de datos local
                    prendas.forEach { prenda: Prenda ->
                        val existing = prendaDao.getPrendaById(prenda.id)
                        if (existing == null) {
                            prendaDao.insertPrenda(prenda)
                        }
                    }
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al sincronizar prendas del API"))
                }
            } else {
                Result.failure(Exception("No hay token de autenticación"))
            }
        } catch (e: Exception) {
            // Si falla la sincronización, no es crítico, se usan datos locales
            Result.failure(e)
        }
    }

    /**
     * Inserta una nueva prenda
     * 1. Intenta crear en el API REST
     * 2. Guarda en la base de datos local (Room)
     */
    suspend fun insert(prenda: Prenda): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getAuthToken()
            
            // Siempre guardar en local primero
            prendaDao.insertPrenda(prenda)
            
            // Intentar sincronizar con API
            if (token != null) {
                try {
                    val dto = PrendaDto(
                        nombre = prenda.nombre,
                        categoria = prenda.categoria.name,
                        color = prenda.color.name,
                        imagenUrl = prenda.imagenPath,
                        usuarioId = prenda.usuarioId
                    )
                    
                    val response = apiService.createPrenda(dto, "Bearer $token")
                    
                    if (response.isSuccessful && response.body() != null) {
                        // Actualizar ID si el API retorna uno diferente
                        val createdDto = response.body()!!
                        if (createdDto.id != prenda.id) {
                            prendaDao.deletePrendaById(prenda.id)
                            prendaDao.insertPrenda(prenda.copy(id = createdDto.id))
                        }
                    }
                } catch (apiError: Exception) {
                    // Si falla el API, está OK, ya está guardado en local
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una prenda por ID
     * 1. Intenta eliminar del API REST
     * 2. Elimina de la base de datos local (Room)
     */
    suspend fun deleteById(prendaId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getAuthToken()
            
            // Eliminar de local primero
            prendaDao.deletePrendaById(prendaId)
            
            // Intentar sincronizar eliminación con API
            if (token != null) {
                try {
                    apiService.deletePrenda(prendaId, "Bearer $token")
                } catch (apiError: Exception) {
                    // Si falla el API, está OK, ya está eliminado en local
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPrendaById(prendaId: Int): Prenda? {
        return prendaDao.getPrendaById(prendaId)
    }
}
