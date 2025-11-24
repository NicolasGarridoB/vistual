package com.example.vistual.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.AgregarPrendaState
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.ColorPrenda
import com.example.vistual.model.Prenda
import com.example.vistual.repository.PrendaRepository
import kotlinx.coroutines.launch

class AgregarPrendaViewModel(private val prendaRepository: PrendaRepository) : ViewModel() {

    private val _agregarPrendaState = mutableStateOf(AgregarPrendaState())
    val agregarPrendaState: State<AgregarPrendaState> = _agregarPrendaState

    private val _nombrePrenda = mutableStateOf("")
    val nombrePrenda: State<String> = _nombrePrenda

    private val _categoriaSeleccionada = mutableStateOf(CategoriaPrenda.PARTE_SUPERIOR)
    val categoriaSeleccionada: State<CategoriaPrenda> = _categoriaSeleccionada

    private val _colorSeleccionado = mutableStateOf(ColorPrenda.BLANCO)
    val colorSeleccionado: State<ColorPrenda> = _colorSeleccionado

    private val _imagenPath = mutableStateOf("")
    val imagenPath: State<String> = _imagenPath

    private var currentUserId: Int = -1

    fun inicializar(userId: Int) {
        currentUserId = userId
    }

    fun actualizarNombre(nombre: String) {
        _nombrePrenda.value = nombre
    }

    fun actualizarCategoria(categoria: CategoriaPrenda) {
        _categoriaSeleccionada.value = categoria
    }

    fun actualizarColor(color: ColorPrenda) {
        _colorSeleccionado.value = color
    }

    fun actualizarImagenPath(path: String) {
        _imagenPath.value = path
    }

    fun agregarPrenda() {
        if (_nombrePrenda.value.isBlank() || _imagenPath.value.isBlank() || currentUserId == -1) {
            _agregarPrendaState.value = AgregarPrendaState(errorMessage = "Todos los campos son obligatorios.")
            return
        }

        viewModelScope.launch {
            _agregarPrendaState.value = AgregarPrendaState(isLoading = true)
            try {
                val nuevaPrenda = Prenda(
                    nombre = _nombrePrenda.value.trim(),
                    categoria = _categoriaSeleccionada.value,
                    color = _colorSeleccionado.value,
                    imagenPath = _imagenPath.value,
                    usuarioId = currentUserId
                )
                prendaRepository.insert(nuevaPrenda)
                _agregarPrendaState.value = AgregarPrendaState(isSuccess = true)
            } catch (e: Exception) {
                _agregarPrendaState.value = AgregarPrendaState(errorMessage = "Error al guardar la prenda: ${e.message}")
            }
        }
    }
    
    fun reiniciarEstado() {
        _nombrePrenda.value = ""
        _categoriaSeleccionada.value = CategoriaPrenda.PARTE_SUPERIOR
        _colorSeleccionado.value = ColorPrenda.BLANCO
        _imagenPath.value = ""
        _agregarPrendaState.value = AgregarPrendaState()
    }

     fun limpiarError() {
        _agregarPrendaState.value = _agregarPrendaState.value.copy(errorMessage = null)
    }

    fun obtenerCategorias(): List<CategoriaPrenda> = CategoriaPrenda.values().toList()

    fun obtenerColores(): List<ColorPrenda> = ColorPrenda.values().toList()
}