package com.example.vistual.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.PrendasState
import com.example.vistual.repository.PrendaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val prendaRepository: PrendaRepository) : ViewModel() {

    private val _prendasState = mutableStateOf(PrendasState())
    val prendasState: State<PrendasState> = _prendasState

    private var currentUserId: Int = -1

    fun inicializar(userId: Int) {
        if (currentUserId != userId) {
            currentUserId = userId
            cargarPrendas()
        }
    }

    fun cargarPrendas() {
        if (currentUserId == -1) return

        viewModelScope.launch {
            prendaRepository.getAllPrendas(currentUserId).collect {
                _prendasState.value = PrendasState(prendas = it)
            }
        }
    }

    fun buscarPrenda(termino: String) {
        // La lógica de búsqueda ahora se puede manejar directamente en el UI o 
        // se puede implementar un filtro más avanzado aquí si es necesario.
    }

    fun limpiarFiltros() {
        cargarPrendas() // Simplemente recargamos la lista original
    }
}