package com.example.vistual.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vistual.model.Outfit
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: Outfit)

    @Query("SELECT * FROM outfits ORDER BY nombre ASC")
    fun getAllOutfits(): Flow<List<Outfit>>

    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    suspend fun getOutfitById(outfitId: Int): Outfit?

    @Query("DELETE FROM outfits WHERE id = :outfitId")
    suspend fun deleteOutfitById(outfitId: Int)
}
