// Archivo: viewModel/ClosetViewModel.kt
package com.example.vistual.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.data.AppDatabase         // <-- Import correcto
import com.example.vistual.data.ClothingType
import com.example.vistual.repository.ClosetRepository
import kotlinx.coroutines.launch

class ClosetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClosetRepository

    init {
        // Obtenemos el DAO usando la nueva clase AppDatabase
        val clothingDao = AppDatabase.getInstance(application).clothingDao() // <-- LÍNEA CORREGIDA
        repository = ClosetRepository(clothingDao)
    }

    val topItems = repository.getTopItems()
    val bottomItems = repository.getBottomItems()

    fun addClothingItem(imageUri: String, type: ClothingType) {
        viewModelScope.launch {
            repository.insertItem(imageUri, type)
        }
    }
}

