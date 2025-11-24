package com.example.vistual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.vistual.ui.VistualApp

/**
 * Activity principal de la aplicación
 * Usa Jetpack Compose para toda la UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Instalar la pantalla de bienvenida
        installSplashScreen()

        enableEdgeToEdge()
        setContent {
            VistualApp()
        }
    }
}
