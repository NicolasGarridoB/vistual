package com.example.vistual.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vistual.repository.OutfitRepository
import com.example.vistual.repository.PrendaRepository
import com.example.vistual.repository.UserRepository

class ViewModelFactory(
    private val prendaRepository: PrendaRepository,
    private val outfitRepository: OutfitRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(prendaRepository) as T
        }
        if (modelClass.isAssignableFrom(AgregarPrendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarPrendaViewModel(prendaRepository) as T
        }
        if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitViewModel(outfitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
