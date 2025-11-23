package com.example.vistual.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.Outfit
import com.example.vistual.repository.OutfitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OutfitViewModel(private val repository: OutfitRepository) : ViewModel() {

    // Flow para obtener todos los outfits y exponerlo como StateFlow
    val allOutfits: StateFlow<List<Outfit>> = repository.allOutfits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Guarda un nuevo outfit en la base de datos.
     * @param nombre El nombre personalizado para el outfit.
     * @param prendasIds La lista de IDs de las prendas que componen el outfit.
     */
    fun saveOutfit(nombre: String, prendasIds: List<Int>) {
        viewModelScope.launch {
            if (nombre.isNotBlank() && prendasIds.isNotEmpty()) {
                val newOutfit = Outfit(nombre = nombre, prendasIds = prendasIds)
                repository.insert(newOutfit)
            }
        }
    }

    /**
     * Elimina un outfit de la base de datos por su ID.
     * @param outfitId El ID del outfit a eliminar.
     */
    fun deleteOutfit(outfitId: Int) {
        viewModelScope.launch {
            repository.deleteById(outfitId)
        }
    }
}
