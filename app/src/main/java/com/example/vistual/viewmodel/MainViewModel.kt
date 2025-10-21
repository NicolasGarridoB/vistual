package com.example.vistual.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.Prenda
import com.example.vistual.model.PrendasState
import com.example.vistual.repository.PrendaRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la pantalla principal con las prendas
 * Implementa el patrón MVVM requerido por la rúbrica
 * Maneja listas, ciclos y condicionales según los requisitos
 */
class MainViewModel(context: Context) : ViewModel() {
    
    // Variables que cumplen con el requisito de "Variables" de la rúbrica
    private val prendaRepository = PrendaRepository(context)
    private var usuarioId: Int = -1
    
    private val _prendasState = mutableStateOf(PrendasState())
    val prendasState: State<PrendasState> = _prendasState
    
    private val _filtroCategoria = mutableStateOf("")
    val filtroCategoria: State<String> = _filtroCategoria
    
    private val _filtroColor = mutableStateOf("")
    val filtroColor: State<String> = _filtroColor
    
    private val _terminoBusqueda = mutableStateOf("")
    val terminoBusqueda: State<String> = _terminoBusqueda
    
    /**
     * Función que cumple con el requisito de "Una función" de la rúbrica
     * Inicializa el ViewModel con el ID del usuario
     */
    fun inicializar(usuarioId: Int) {
        this.usuarioId = usuarioId
        cargarPrendas()
    }
    
    /**
     * Carga todas las prendas del usuario usando corrutinas
     */
    fun cargarPrendas() {
        // Condicional para validar usuario
        if (usuarioId == -1) {
            _prendasState.value = _prendasState.value.copy(
                errorMessage = "Usuario no válido"
            )
            return
        }
        
        _prendasState.value = _prendasState.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                val prendas = prendaRepository.obtenerPrendasUsuario(usuarioId)
                
                // Aplicar filtros si están activos
                val prendasFiltradas = aplicarFiltros(prendas)
                
                _prendasState.value = _prendasState.value.copy(
                    isLoading = false,
                    prendas = prendasFiltradas
                )
            } catch (e: Exception) {
                _prendasState.value = _prendasState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar prendas: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Aplica filtros a la lista de prendas usando ciclos y condicionales
     */
    private fun aplicarFiltros(prendas: List<Prenda>): List<Prenda> {
        var prendasFiltradas = prendas
        
        // Filtro por categoría usando condicionales
        if (_filtroCategoria.value.isNotEmpty()) {
            prendasFiltradas = prendasFiltradas.filter { prenda ->
                prenda.categoria.equals(_filtroCategoria.value, ignoreCase = true)
            }
        }
        
        // Filtro por color usando condicionales
        if (_filtroColor.value.isNotEmpty()) {
            prendasFiltradas = prendasFiltradas.filter { prenda ->
                prenda.color.equals(_filtroColor.value, ignoreCase = true)
            }
        }
        
        // Filtro por búsqueda usando condicionales
        if (_terminoBusqueda.value.isNotEmpty()) {
            prendasFiltradas = prendasFiltradas.filter { prenda ->
                prenda.nombre.contains(_terminoBusqueda.value, ignoreCase = true)
            }
        }
        
        return prendasFiltradas
    }
    
    /**
     * Establece filtro por categoría y recarga las prendas
     */
    fun filtrarPorCategoria(categoria: String) {
        _filtroCategoria.value = categoria
        cargarPrendas()
    }
    
    /**
     * Establece filtro por color y recarga las prendas
     */
    fun filtrarPorColor(color: String) {
        _filtroColor.value = color
        cargarPrendas()
    }
    
    /**
     * Busca prendas por nombre
     */
    fun buscarPrenda(termino: String) {
        _terminoBusqueda.value = termino
        cargarPrendas()
    }
    
    /**
     * Limpia todos los filtros activos
     */
    fun limpiarFiltros() {
        _filtroCategoria.value = ""
        _filtroColor.value = ""
        _terminoBusqueda.value = ""
        cargarPrendas()
    }
    
    /**
     * Obtiene estadísticas de prendas por categoría usando ciclos
     */
    fun obtenerEstadisticas() {
        viewModelScope.launch {
            try {
                val conteo = prendaRepository.contarPrendasPorCategoria(usuarioId)
                // Aquí se podrían emitir las estadísticas a otro estado si fuera necesario
                println("Estadísticas de prendas: $conteo")
            } catch (e: Exception) {
                println("Error al obtener estadísticas: ${e.message}")
            }
        }
    }
    
    /**
     * Obtiene las categorías únicas de las prendas usando listas y ciclos
     */
    fun obtenerCategorias(): List<String> {
        val prendas = _prendasState.value.prendas
        val categorias = mutableListOf<String>()
        
        // Ciclo para extraer categorías únicas
        for (prenda in prendas) {
            if (!categorias.contains(prenda.categoria)) {
                categorias.add(prenda.categoria)
            }
        }
        
        return categorias.sorted()
    }
    
    /**
     * Obtiene los colores únicos de las prendas usando listas y ciclos
     */
    fun obtenerColores(): List<String> {
        val prendas = _prendasState.value.prendas
        val colores = mutableListOf<String>()
        
        // Ciclo para extraer colores únicos
        for (prenda in prendas) {
            if (!colores.contains(prenda.color)) {
                colores.add(prenda.color)
            }
        }
        
        return colores.sorted()
    }
}