package com.example.vistual.repository

import com.example.vistual.data.ClothingDao // <-- 1. Importa tu DAO
import com.example.vistual.data.ClothingItem
import com.example.vistual.data.ClothingType
import kotlinx.coroutines.flow.Flow

// 2. Añade el DAO como un parámetro privado en el constructor
class ClosetRepository(private val clothingDao: ClothingDao) {

    // 3. Crea una función para obtener las prendas superiores.
    // Esta función simplemente llama al método correspondiente del DAO.
    // Devuelve un Flow para que la UI se actualice automáticamente.
    fun getTopItems(): Flow<List<ClothingItem>> {
        return clothingDao.getTopItems()
    }

    // 4. Haz lo mismo para las prendas inferiores.
    fun getBottomItems(): Flow<List<ClothingItem>> {
        return clothingDao.getBottomItems()
    }

    // 5. Crea una función para insertar una nueva prenda.
    // La marcamos como 'suspend' porque es una operación de escritura
    // y debe ejecutarse en una corrutina sin bloquear el hilo principal.
    suspend fun insertItem(imageUri: String, type: ClothingType) {
        val newItem = ClothingItem(imageUri = imageUri, type = type)
        clothingDao.insertItem(newItem)
    }

    // Puedes añadir más funciones aquí si las necesitas (ej: borrar, actualizar)
    // suspend fun deleteItem(item: ClothingItem) {
    //     clothingDao.deleteItem(item)
    // }
}
