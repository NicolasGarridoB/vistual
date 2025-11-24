package com.example.vistual.repository

import com.example.vistual.db.OutfitDao
import com.example.vistual.model.Outfit
import kotlinx.coroutines.flow.Flow

class OutfitRepository(private val outfitDao: OutfitDao) {

    val allOutfits: Flow<List<Outfit>> = outfitDao.getAllOutfits()

    suspend fun insert(outfit: Outfit) {
        outfitDao.insertOutfit(outfit)
    }

    suspend fun deleteById(outfitId: Int) {
        outfitDao.deleteOutfitById(outfitId)
    }

    suspend fun getOutfitById(outfitId: Int): Outfit? {
        return outfitDao.getOutfitById(outfitId)
    }
}
