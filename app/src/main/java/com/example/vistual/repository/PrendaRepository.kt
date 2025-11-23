package com.example.vistual.repository

import com.example.vistual.db.PrendaDao
import com.example.vistual.model.Prenda
import kotlinx.coroutines.flow.Flow

class PrendaRepository(private val prendaDao: PrendaDao) {

    fun getAllPrendas(usuarioId: Int): Flow<List<Prenda>> {
        return prendaDao.getAllPrendas(usuarioId)
    }

    suspend fun insert(prenda: Prenda) {
        prendaDao.insertPrenda(prenda)
    }

    suspend fun deleteById(prendaId: Int) {
        prendaDao.deletePrendaById(prendaId)
    }

    suspend fun getPrendaById(prendaId: Int): Prenda? {
        return prendaDao.getPrendaById(prendaId)
    }
}
