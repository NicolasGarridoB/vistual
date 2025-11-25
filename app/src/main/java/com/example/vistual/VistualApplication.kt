package com.example.vistual

import android.app.Application
import android.content.Context
import com.example.vistual.api.RetrofitClient
import com.example.vistual.db.AppDatabase
import com.example.vistual.repository.OutfitRepository
import com.example.vistual.repository.PrendaRepository
import com.example.vistual.repository.UserRepository

/**
 * Application class para inicializar dependencias globales
 * Implementa patrón Singleton para compartir repositorios
 */
class VistualApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val sharedPreferences by lazy {
        getSharedPreferences("VistualSession", Context.MODE_PRIVATE)
    }
    
    // Servicios de Retrofit para consumir API REST
    private val authService by lazy { RetrofitClient.authService }  // Para autenticación
    private val apiService by lazy { RetrofitClient.apiService }    // Para prendas y outfits

    // Repositorios que usan Room (interna) + Retrofit (externa)
    val prendaRepository by lazy { 
        PrendaRepository(
            database.prendaDao(),
            apiService,
            getAuthToken = { userRepository.getAuthToken() }
        )
    }
    
    val outfitRepository by lazy { 
        OutfitRepository(database.outfitDao()) 
    }
    
    val userRepository by lazy { 
        UserRepository(
            database.usuarioDao(),
            authService,  // Usa authService en lugar de apiService
            sharedPreferences
        ) 
    }
}
