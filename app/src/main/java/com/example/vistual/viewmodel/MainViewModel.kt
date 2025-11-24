package com.example.vistual.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.Prenda
import com.example.vistual.model.PrendasState
import com.example.vistual.repository.PrendaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val prendaRepository: PrendaRepository) : ViewModel() {

    private val _prendasState = mutableStateOf(PrendasState())
    val prendasState: State<PrendasState> = _prendasState
    
    private val _categoriaSeleccionada = mutableStateOf<CategoriaPrenda?>(null)
    val categoriaSeleccionada: State<CategoriaPrenda?> = _categoriaSeleccionada

    private var currentUserId: Int = -1
    private var todasLasPrendas: List<Prenda> = emptyList()

    fun inicializar(userId: Int) {
        if (currentUserId != userId) {
            currentUserId = userId
            cargarPrendas()
        }
    }

    fun cargarPrendas() {
        if (currentUserId == -1) return

        viewModelScope.launch {
            prendaRepository.getAllPrendas(currentUserId).collect { prendas ->
                todasLasPrendas = prendas
                aplicarFiltro()
            }
        }
    }
    
    fun seleccionarCategoria(categoria: CategoriaPrenda?) {
        _categoriaSeleccionada.value = categoria
        aplicarFiltro()
    }
    
    private fun aplicarFiltro() {
        val prendasFiltradas = if (_categoriaSeleccionada.value != null) {
            todasLasPrendas.filter { it.categoria == _categoriaSeleccionada.value }
        } else {
            todasLasPrendas
        }
        _prendasState.value = PrendasState(prendas = prendasFiltradas)
    }
    
    fun obtenerPrendasPorCategoria(): Map<CategoriaPrenda, List<Prenda>> {
        return todasLasPrendas.groupBy { it.categoria }
    }

    fun buscarPrenda(termino: String) {
        val prendasFiltradas = todasLasPrendas.filter { 
            it.nombre.contains(termino, ignoreCase = true)
        }
        _prendasState.value = PrendasState(prendas = prendasFiltradas)
    }

    fun limpiarFiltros() {
        _categoriaSeleccionada.value = null
        aplicarFiltro()
    }
}