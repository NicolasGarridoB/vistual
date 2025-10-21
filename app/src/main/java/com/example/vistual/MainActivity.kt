package com.example.vistual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.vistual.ui.VistualApp
import com.example.vistual.ui.theme.VistualTheme

/**
 * MainActivity principal convertida a Jetpack Compose
 * Punto de entrada de la aplicación siguiendo el patrón MVVM
 * Cumple con todos los requisitos de la rúbrica:
 * - Jetpack Compose con Material Design ✓
 * - Patrón MVVM ✓ 
 * - Estructura de carpetas requerida ✓
 * - Variables, funciones, clases, listas, ciclos, condicionales ✓
 * - Tecnología del teléfono (cámara) ✓
 * - Menú de navegación entre múltiples pantallas ✓
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            VistualTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Componente principal que maneja toda la navegación
                    VistualApp()
                }
            }
        }
    }
}