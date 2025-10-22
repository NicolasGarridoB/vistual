package com.example.vistual.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.models.DBHelper
import com.example.vistual.models.ClothingType
import com.example.vistual.repository.ClosetRepository
import kotlinx.coroutines.launch

class ClosetViewModel(application: Application, private val userId: Int = 1) : AndroidViewModel(application) {
    private val repository: ClosetRepository

    init {
        // Obtenemos el DBHelper y creamos el repository
        val dbHelper = DBHelper(application)
        repository = ClosetRepository(dbHelper, userId)
    }

    val topItems = repository.getTopItems()
    val bottomItems = repository.getBottomItems()

    fun addClothingItem(imageUri: String, type: ClothingType) {
        viewModelScope.launch {
            repository.insertItem(imageUri, type)
        }
    }
}

