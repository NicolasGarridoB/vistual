package com.example.vistual.db

import androidx.room.*
import com.example.vistual.model.Outfit
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad Outfit.
 * Define las operaciones de base de datos para outfits/conjuntos (BD Interna con Room).
 */
@Dao
interface OutfitDao {

    /**
     * Inserta un nuevo outfit en la base de datos local
     * @param outfit Outfit a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: Outfit)

    /**
     * Inserta múltiples outfits (útil para sincronización)
     * @param outfits Lista de outfits a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfits(outfits: List<Outfit>)

    /**
     * Actualiza un outfit existente
     * @param outfit Outfit con datos actualizados
     */
    @Update
    suspend fun updateOutfit(outfit: Outfit)

    /**
     * Elimina un outfit específico
     * @param outfit Outfit a eliminar
     */
    @Delete
    suspend fun deleteOutfit(outfit: Outfit)

    /**
     * Obtiene todos los outfits de un usuario
     * Ordenados alfabéticamente por nombre
     * @param usuarioId ID del usuario
     * @return Flow con lista de outfits
     */
    @Query("SELECT * FROM outfits WHERE usuarioId = :usuarioId ORDER BY nombre ASC")
    fun getAllOutfitsByUsuario(usuarioId: Int): Flow<List<Outfit>>

    /**
     * Obtiene todos los outfits (sin filtro de usuario)
     * @return Flow con todos los outfits
     */
    @Query("SELECT * FROM outfits ORDER BY nombre ASC")
    fun getAllOutfits(): Flow<List<Outfit>>

    /**
     * Obtiene un outfit específico por su ID
     * @param outfitId ID del outfit
     * @return Outfit encontrado o null
     */
    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    suspend fun getOutfitById(outfitId: Int): Outfit?

    /**
     * Elimina un outfit por su ID
     * @param outfitId ID del outfit a eliminar
     */
    @Query("DELETE FROM outfits WHERE id = :outfitId")
    suspend fun deleteOutfitById(outfitId: Int)

    /**
     * Obtiene el conteo de outfits de un usuario
     * @param usuarioId ID del usuario
     * @return Número de outfits
     */
    @Query("SELECT COUNT(*) FROM outfits WHERE usuarioId = :usuarioId")
    suspend fun getContadorOutfits(usuarioId: Int): Int

    /**
     * Elimina todos los outfits de un usuario
     * @param usuarioId ID del usuario
     */
    @Query("DELETE FROM outfits WHERE usuarioId = :usuarioId")
    suspend fun deleteAllOutfitsByUsuario(usuarioId: Int)

    /**
     * Busca outfits por nombre
     * @param usuarioId ID del usuario
     * @param searchQuery Texto a buscar
     * @return Flow con outfits que coinciden
     */
    @Query("SELECT * FROM outfits WHERE usuarioId = :usuarioId AND nombre LIKE '%' || :searchQuery || '%' ORDER BY nombre ASC")
    fun searchOutfits(usuarioId: Int, searchQuery: String): Flow<List<Outfit>>
}
