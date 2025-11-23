package com.example.vistual.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.PrendasState
import com.example.vistual.repository.PrendaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val prendaRepository: PrendaRepository) : ViewModel() {

    private val TAG = "MainViewModel"
    
    private val _prendasState = mutableStateOf(PrendasState())
    val prendasState: State<PrendasState> = _prendasState

    private var currentUserId: Int = -1

    fun inicializar(userId: Int) {
        Log.d(TAG, "Inicializando con userId: $userId (currentUserId anterior: $currentUserId)")
        if (currentUserId != userId) {
            currentUserId = userId
            cargarPrendas()
        } else {
            Log.d(TAG, "UserId no cambió, el Flow ya está activo")
        }
    }

    fun cargarPrendas() {
        Log.d(TAG, "cargarPrendas llamado con userId: $currentUserId")
        if (currentUserId == -1) {
            Log.w(TAG, "currentUserId es -1, no se pueden cargar prendas")
            return
        }

        // Lanzamos una coroutine que observa el Flow PERMANENTEMENTE
        // Room actualizará automáticamente cuando haya cambios en la BD
        viewModelScope.launch {
            Log.d(TAG, "Iniciando observación del Flow de prendas...")
            
            prendaRepository.getAllPrendas(currentUserId).collect { prendas ->
                Log.d(TAG, "📦 Flow emitió: ${prendas.size} prendas")
                prendas.forEach { prenda ->
                    Log.d(TAG, "  ├─ ${prenda.nombre} (id=${prenda.id}, userId=${prenda.usuarioId})")
                }
                Log.d(TAG, "  └─ Actualizando UI state...")
                _prendasState.value = PrendasState(prendas = prendas)
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