package com.example.vistual.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.vistual.db.PrendaDao
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.ColorPrenda
import com.example.vistual.model.Prenda
import com.example.vistual.network.ApiResult
import com.example.vistual.network.RetrofitInstance
import com.example.vistual.network.models.PrendaRequest
import com.example.vistual.network.safeApiCall
import kotlinx.coroutines.flow.Flow

/**
 * Repository para gestionar prendas.
 * Implementa el patrón Repository siguiendo MVVM.
 * 
 * Características:
 * - Gestiona BD interna (Room) para acceso local rápido
 * - Sincroniza con BD externa (Retrofit/API REST)
 * - Maneja operaciones offline-first (primero local, luego sincroniza)
 * - Proporciona Flow para observación reactiva de datos
 */
class PrendaRepository(
    private val prendaDao: PrendaDao,
    private val sharedPreferences: SharedPreferences
) {
    
    private val TAG = "PrendaRepository"
    
    // ==================== OPERACIONES DE LECTURA ====================
    
    /**
     * Obtiene todas las prendas de un usuario desde BD local
     * Retorna un Flow que emite automáticamente cuando hay cambios
     * 
     * @param usuarioId ID del usuario
     * @return Flow con lista de prendas
     */
    fun getAllPrendas(usuarioId: Int): Flow<List<Prenda>> {
        return prendaDao.getAllPrendas(usuarioId)
    }
    
    /**
     * Obtiene prendas filtradas por categoría
     * 
     * @param usuarioId ID del usuario
     * @param categoria Categoría a filtrar
     * @return Flow con prendas de esa categoría
     */
    fun getPrendasByCategoria(usuarioId: Int, categoria: CategoriaPrenda): Flow<List<Prenda>> {
        return prendaDao.getPrendasByCategoria(usuarioId, categoria)
    }
    
    /**
     * Busca prendas por nombre
     * 
     * @param usuarioId ID del usuario
     * @param query Texto a buscar
     * @return Flow con prendas que coinciden
     */
    fun searchPrendas(usuarioId: Int, query: String): Flow<List<Prenda>> {
        return prendaDao.searchPrendas(usuarioId, query)
    }
    
    /**
     * Obtiene una prenda específica por ID
     * 
     * @param prendaId ID de la prenda
     * @return Prenda o null
     */
    suspend fun getPrendaById(prendaId: Int): Prenda? {
        return prendaDao.getPrendaById(prendaId)
    }
    
    /**
     * Obtiene el conteo total de prendas
     * 
     * @param usuarioId ID del usuario
     * @return Número de prendas
     */
    suspend fun getContadorPrendas(usuarioId: Int): Int {
        return prendaDao.getContadorPrendas(usuarioId)
    }

    // ==================== OPERACIONES DE ESCRITURA ====================
    
    /**
     * Inserta una nueva prenda (BD local y externa)
     * Estrategia: Guardar local primero, luego sincronizar con servidor
     * 
     * @param prenda Prenda a insertar
     * @return Result indicando éxito o error
     */
    suspend fun insert(prenda: Prenda): Result<Unit> {
        return try {
            // 1. Guardar en BD local primero (offline-first)
            prendaDao.insertPrenda(prenda)
            Log.d(TAG, "Prenda guardada localmente: ${prenda.nombre}")
            
            // 2. Intentar sincronizar con servidor
            sincronizarPrendaConServidor(prenda)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar prenda", e)
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza una prenda existente
     * 
     * @param prenda Prenda con datos actualizados
     * @return Result indicando éxito o error
     */
    suspend fun update(prenda: Prenda): Result<Unit> {
        return try {
            prendaDao.updatePrenda(prenda)
            Log.d(TAG, "Prenda actualizada: ${prenda.nombre}")
            
            // Intentar sincronizar actualización con servidor
            sincronizarActualizacionConServidor(prenda)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar prenda", e)
            Result.failure(e)
        }
    }
    
    /**
     * Elimina una prenda por ID
     * 
     * @param prendaId ID de la prenda a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun deleteById(prendaId: Int): Result<Unit> {
        return try {
            // Eliminar de BD local
            prendaDao.deletePrendaById(prendaId)
            Log.d(TAG, "Prenda eliminada localmente: ID $prendaId")
            
            // Intentar eliminar del servidor
            sincronizarEliminacionConServidor(prendaId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar prenda", e)
            Result.failure(e)
        }
    }

    // ==================== SINCRONIZACIÓN CON SERVIDOR ====================
    
    /**
     * Sincroniza todas las prendas desde el servidor
     * Útil para la primera carga o para actualizar datos
     * 
     * @param usuarioId ID del usuario
     * @return Result con número de prendas sincronizadas
     */
    suspend fun sincronizarConServidor(usuarioId: Int): Result<Int> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                Log.w(TAG, "No hay token, omitiendo sincronización")
                return Result.success(0)
            }
            
            // Obtener prendas del servidor
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.obtenerPrendas(token, usuarioId)
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    val prendasRemote = apiResult.data.map { response ->
                        Prenda(
                            id = response.id,
                            nombre = response.nombre,
                            categoria = CategoriaPrenda.valueOf(response.categoria),
                            color = ColorPrenda.valueOf(response.color),
                            imagenPath = response.imagenPath,
                            usuarioId = response.usuarioId,
                            fechaCreacion = response.fechaCreacion
                        )
                    }
                    
                    // Insertar todas las prendas del servidor
                    prendaDao.insertPrendas(prendasRemote)
                    Log.d(TAG, "Sincronizadas ${prendasRemote.size} prendas desde servidor")
                    Result.success(prendasRemote.size)
                }
                is ApiResult.Error -> {
                    Log.w(TAG, "Error al sincronizar: ${apiResult.message}")
                    Result.failure(Exception(apiResult.message))
                }
                else -> Result.failure(Exception("Error desconocido en sincronización"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en sincronización", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sincroniza una prenda nueva con el servidor en segundo plano
     */
    private suspend fun sincronizarPrendaConServidor(prenda: Prenda) {
        try {
            val token = getAuthToken() ?: return
            
            val request = PrendaRequest(
                nombre = prenda.nombre,
                categoria = prenda.categoria.name,
                color = prenda.color.name,
                imagenPath = prenda.imagenPath,
                usuarioId = prenda.usuarioId
            )
            
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.crearPrenda(token, request)
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    Log.d(TAG, "Prenda sincronizada con servidor: ${prenda.nombre}")
                    // Opcionalmente actualizar el ID local con el ID del servidor
                }
                is ApiResult.Error -> {
                    Log.w(TAG, "No se pudo sincronizar prenda con servidor: ${apiResult.message}")
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar prenda", e)
        }
    }
    
    /**
     * Sincroniza actualización de prenda con servidor
     */
    private suspend fun sincronizarActualizacionConServidor(prenda: Prenda) {
        try {
            val token = getAuthToken() ?: return
            
            val request = PrendaRequest(
                nombre = prenda.nombre,
                categoria = prenda.categoria.name,
                color = prenda.color.name,
                imagenPath = prenda.imagenPath,
                usuarioId = prenda.usuarioId
            )
            
            safeApiCall {
                RetrofitInstance.apiService.actualizarPrenda(token, prenda.id, request)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar actualización", e)
        }
    }
    
    /**
     * Sincroniza eliminación con servidor
     */
    private suspend fun sincronizarEliminacionConServidor(prendaId: Int) {
        try {
            val token = getAuthToken() ?: return
            
            safeApiCall {
                RetrofitInstance.apiService.eliminarPrenda(token, prendaId)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar eliminación", e)
        }
    }
    
    // ==================== UTILIDADES ====================
    
    /**
     * Obtiene el token de autenticación de SharedPreferences
     */
    private fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
}
