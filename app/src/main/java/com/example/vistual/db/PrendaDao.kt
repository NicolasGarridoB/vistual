package com.example.vistual.db

import androidx.room.*
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.Prenda
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad Prenda.
 * Define las operaciones de base de datos para prendas (BD Interna con Room).
 * 
 * Utiliza Flow para observar cambios en tiempo real y suspend para operaciones asíncronas.
 */
@Dao
interface PrendaDao {

    /**
     * Inserta una nueva prenda en la base de datos local
     * Si ya existe una prenda con el mismo ID, la reemplaza
     * @param prenda Prenda a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrenda(prenda: Prenda)

    /**
     * Inserta múltiples prendas (útil para sincronización masiva)
     * @param prendas Lista de prendas a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrendas(prendas: List<Prenda>)

    /**
     * Actualiza una prenda existente
     * @param prenda Prenda con datos actualizados
     */
    @Update
    suspend fun updatePrenda(prenda: Prenda)

    /**
     * Elimina una prenda específica
     * @param prenda Prenda a eliminar
     */
    @Delete
    suspend fun deletePrenda(prenda: Prenda)

    /**
     * Obtiene todas las prendas de un usuario específico
     * Ordenadas por fecha de creación (más recientes primero)
     * @param usuarioId ID del usuario
     * @return Flow que emite la lista actualizada automáticamente
     */
    @Query("SELECT * FROM prendas WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun getAllPrendas(usuarioId: Int): Flow<List<Prenda>>

    /**
     * Obtiene una prenda específica por su ID
     * @param prendaId ID de la prenda
     * @return Prenda encontrada o null
     */
    @Query("SELECT * FROM prendas WHERE id = :prendaId")
    suspend fun getPrendaById(prendaId: Int): Prenda?

    /**
     * Elimina una prenda por su ID
     * @param prendaId ID de la prenda a eliminar
     */
    @Query("DELETE FROM prendas WHERE id = :prendaId")
    suspend fun deletePrendaById(prendaId: Int)

    /**
     * Obtiene prendas filtradas por categoría
     * @param usuarioId ID del usuario
     * @param categoria Categoría de prenda
     * @return Flow con prendas de esa categoría
     */
    @Query("SELECT * FROM prendas WHERE usuarioId = :usuarioId AND categoria = :categoria ORDER BY fechaCreacion DESC")
    fun getPrendasByCategoria(usuarioId: Int, categoria: CategoriaPrenda): Flow<List<Prenda>>

    /**
     * Obtiene el conteo total de prendas de un usuario
     * @param usuarioId ID del usuario
     * @return Número total de prendas
     */
    @Query("SELECT COUNT(*) FROM prendas WHERE usuarioId = :usuarioId")
    suspend fun getContadorPrendas(usuarioId: Int): Int

    /**
     * Elimina todas las prendas de un usuario (útil para logout/reset)
     * @param usuarioId ID del usuario
     */
    @Query("DELETE FROM prendas WHERE usuarioId = :usuarioId")
    suspend fun deleteAllPrendasByUsuario(usuarioId: Int)

    /**
     * Busca prendas por nombre (búsqueda parcial)
     * @param usuarioId ID del usuario
     * @param searchQuery Texto a buscar en el nombre
     * @return Flow con prendas que coinciden
     */
    @Query("SELECT * FROM prendas WHERE usuarioId = :usuarioId AND nombre LIKE '%' || :searchQuery || '%' ORDER BY fechaCreacion DESC")
    fun searchPrendas(usuarioId: Int, searchQuery: String): Flow<List<Prenda>>
}
