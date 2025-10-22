package com.example.vistual.repository

import com.example.vistual.models.DBHelper
import com.example.vistual.models.ClothingItem
import com.example.vistual.models.ClothingType
import com.example.vistual.models.isTop
import com.example.vistual.models.isBottom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClosetRepository(private val dbHelper: DBHelper, private val userId: Int) {

    // Función para obtener las prendas superiores
    fun getTopItems(): Flow<List<ClothingItem>> = flow {
        val allItems = withContext(Dispatchers.IO) {
            dbHelper.obtenerPrendasUsuario(userId)
        }
        emit(allItems.filter { it.isTop() })
    }

    // Función para obtener las prendas inferiores
    fun getBottomItems(): Flow<List<ClothingItem>> = flow {
        val allItems = withContext(Dispatchers.IO) {
            dbHelper.obtenerPrendasUsuario(userId)
        }
        emit(allItems.filter { it.isBottom() })
    }

    // Función para insertar una nueva prenda
    suspend fun insertItem(imageUri: String, type: ClothingType) {
        withContext(Dispatchers.IO) {
            dbHelper.agregarPrenda(
                nombre = "Prenda ${type.displayName}",
                categoria = type.displayName,
                color = "Sin especificar",
                rutaImagen = imageUri,
                idUsuario = userId
            )
        }
    }

    // Puedes añadir más funciones aquí si las necesitas (ej: borrar, actualizar)
    // suspend fun deleteItem(item: ClothingItem) {
    //     clothingDao.deleteItem(item)
    // }
}
