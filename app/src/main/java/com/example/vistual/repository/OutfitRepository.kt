package com.example.vistual.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.vistual.db.OutfitDao
import com.example.vistual.model.Outfit
import com.example.vistual.network.ApiResult
import com.example.vistual.network.RetrofitInstance
import com.example.vistual.network.models.OutfitRequest
import com.example.vistual.network.safeApiCall
import kotlinx.coroutines.flow.Flow

/**
 * Repository para gestionar outfits/conjuntos.
 * Implementa el patrón Repository siguiendo MVVM.
 * 
 * Características:
 * - Gestiona BD interna (Room)
 * - Sincroniza con BD externa (Retrofit/API REST)
 * - Estrategia offline-first
 */
class OutfitRepository(
    private val outfitDao: OutfitDao,
    private val sharedPreferences: SharedPreferences
) {
    
    private val TAG = "OutfitRepository"
    
    // ==================== OPERACIONES DE LECTURA ====================
    
    /**
     * Obtiene todos los outfits (sin filtro de usuario)
     * @return Flow con lista de outfits
     */
    val allOutfits: Flow<List<Outfit>> = outfitDao.getAllOutfits()
    
    /**
     * Obtiene todos los outfits de un usuario específico
     * 
     * @param usuarioId ID del usuario
     * @return Flow con outfits del usuario
     */
    fun getAllOutfitsByUsuario(usuarioId: Int): Flow<List<Outfit>> {
        return outfitDao.getAllOutfitsByUsuario(usuarioId)
    }
    
    /**
     * Obtiene un outfit por su ID
     * 
     * @param outfitId ID del outfit
     * @return Outfit o null
     */
    suspend fun getOutfitById(outfitId: Int): Outfit? {
        return outfitDao.getOutfitById(outfitId)
    }
    
    /**
     * Busca outfits por nombre
     * 
     * @param usuarioId ID del usuario
     * @param query Texto a buscar
     * @return Flow con outfits que coinciden
     */
    fun searchOutfits(usuarioId: Int, query: String): Flow<List<Outfit>> {
        return outfitDao.searchOutfits(usuarioId, query)
    }
    
    /**
     * Obtiene el conteo de outfits
     * 
     * @param usuarioId ID del usuario
     * @return Número de outfits
     */
    suspend fun getContadorOutfits(usuarioId: Int): Int {
        return outfitDao.getContadorOutfits(usuarioId)
    }

    // ==================== OPERACIONES DE ESCRITURA ====================
    
    /**
     * Inserta un nuevo outfit (local y remoto)
     * 
     * @param outfit Outfit a insertar
     * @return Result indicando éxito o error
     */
    suspend fun insert(outfit: Outfit): Result<Unit> {
        return try {
            // 1. Guardar localmente primero
            outfitDao.insertOutfit(outfit)
            Log.d(TAG, "Outfit guardado localmente: ${outfit.nombre}")
            
            // 2. Sincronizar con servidor
            sincronizarOutfitConServidor(outfit)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar outfit", e)
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza un outfit existente
     * 
     * @param outfit Outfit con datos actualizados
     * @return Result indicando éxito o error
     */
    suspend fun update(outfit: Outfit): Result<Unit> {
        return try {
            outfitDao.updateOutfit(outfit)
            Log.d(TAG, "Outfit actualizado: ${outfit.nombre}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar outfit", e)
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un outfit por ID
     * 
     * @param outfitId ID del outfit a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun deleteById(outfitId: Int): Result<Unit> {
        return try {
            // Eliminar localmente
            outfitDao.deleteOutfitById(outfitId)
            Log.d(TAG, "Outfit eliminado localmente: ID $outfitId")
            
            // Sincronizar eliminación con servidor
            sincronizarEliminacionConServidor(outfitId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar outfit", e)
            Result.failure(e)
        }
    }

    // ==================== SINCRONIZACIÓN CON SERVIDOR ====================
    
    /**
     * Sincroniza outfits desde el servidor
     * 
     * @param usuarioId ID del usuario
     * @return Result con número de outfits sincronizados
     */
    suspend fun sincronizarConServidor(usuarioId: Int): Result<Int> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                Log.w(TAG, "No hay token, omitiendo sincronización")
                return Result.success(0)
            }
            
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.obtenerOutfits(token, usuarioId)
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    val outfitsRemote = apiResult.data.map { response ->
                        Outfit(
                            id = response.id,
                            nombre = response.nombre,
                            prendasIds = response.prendasIds,
                            usuarioId = response.usuarioId
                        )
                    }
                    
                    outfitDao.insertOutfits(outfitsRemote)
                    Log.d(TAG, "Sincronizados ${outfitsRemote.size} outfits desde servidor")
                    Result.success(outfitsRemote.size)
                }
                is ApiResult.Error -> {
                    Log.w(TAG, "Error al sincronizar: ${apiResult.message}")
                    Result.failure(Exception(apiResult.message))
                }
                else -> Result.failure(Exception("Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en sincronización", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sincroniza outfit con servidor en segundo plano
     */
    private suspend fun sincronizarOutfitConServidor(outfit: Outfit) {
        try {
            val token = getAuthToken() ?: return
            
            val request = OutfitRequest(
                nombre = outfit.nombre,
                prendasIds = outfit.prendasIds,
                usuarioId = outfit.usuarioId
            )
            
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.crearOutfit(token, request)
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    Log.d(TAG, "Outfit sincronizado con servidor: ${outfit.nombre}")
                }
                is ApiResult.Error -> {
                    Log.w(TAG, "No se pudo sincronizar outfit: ${apiResult.message}")
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar outfit", e)
        }
    }
    
    /**
     * Sincroniza eliminación con servidor
     */
    private suspend fun sincronizarEliminacionConServidor(outfitId: Int) {
        try {
            val token = getAuthToken() ?: return
            
            safeApiCall {
                RetrofitInstance.apiService.eliminarOutfit(token, outfitId)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar eliminación", e)
        }
    }
    
    // ==================== UTILIDADES ====================
    
    private fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
}
