package com.example.vistual.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vistual.model.Prenda
import kotlinx.coroutines.flow.Flow

@Dao
interface PrendaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrenda(prenda: Prenda)

    @Query("SELECT * FROM prendas WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun getAllPrendas(usuarioId: Int): Flow<List<Prenda>>

    @Query("SELECT * FROM prendas WHERE id = :prendaId")
    suspend fun getPrendaById(prendaId: Int): Prenda?

    @Query("DELETE FROM prendas WHERE id = :prendaId")
    suspend fun deletePrendaById(prendaId: Int)
}
