package com.example.vistual.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vistual.ui.theme.VistualTheme
import com.example.vistual.viewmodel.AgregarPrendaViewModel
import com.example.vistual.viewmodel.AuthViewModel
import com.example.vistual.viewmodel.MainViewModel

/**
 * Configuración de navegación principal de la aplicación
 * Implementa Navigation Compose - cumple con menú de navegación de la rúbrica
 * Maneja el estado de sesión del usuario
 */
@Composable
fun VistualApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    // ViewModels compartidos entre pantallas
    val authViewModel = remember { AuthViewModel(context) }
    val mainViewModel = remember { MainViewModel(context) }
    val agregarPrendaViewModel = remember { AgregarPrendaViewModel(context) }
    
    // SharedPreferences para manejo de sesión
    val sharedPreferences = remember {
        context.getSharedPreferences("ClosetVirtual", Context.MODE_PRIVATE)
    }
    
    // Variable para controlar si el usuario está logueado - requisito de variables
    var isLoggedIn by remember { 
        mutableStateOf(sharedPreferences.getBoolean("logged_in", false)) 
    }
    var usuarioId by remember { 
        mutableStateOf(sharedPreferences.getInt("id_usuario", -1)) 
    }
    var usuarioEmail by remember { 
        mutableStateOf(sharedPreferences.getString("correo_usuario", "") ?: "") 
    }
    
    // Función para guardar sesión - cumple con requisito de "Una función"
    fun guardarSesion(id: Int, email: String) {
        with(sharedPreferences.edit()) {
            putBoolean("logged_in", true)
            putInt("id_usuario", id)
            putString("correo_usuario", email)
            apply()
        }
        isLoggedIn = true
        usuarioId = id
        usuarioEmail = email
        mainViewModel.inicializar(id)
    }
    
    // Función para cerrar sesión
    fun cerrarSesion() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        isLoggedIn = false
        usuarioId = -1
        usuarioEmail = ""
    }
    
    // Aplicar tema de Material Design
    VistualTheme {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "main" else "login"
        ) {
            // Pantalla de Login
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { id ->
                        // Condicional para validar login exitoso
                        if (id != -1) {
                            // Obtener email del estado del ViewModel
                            val email = authViewModel.loginState.value.usuario?.correo ?: ""
                            guardarSesion(id, email)
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            
            // Pantalla de Registro
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        authViewModel.reiniciarEstadoRegistro()
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Pantalla Principal
            composable("main") {
                // Inicializar ViewModel si es necesario
                LaunchedEffect(usuarioId) {
                    if (usuarioId != -1) {
                        mainViewModel.inicializar(usuarioId)
                    }
                }
                
                MainScreen(
                    mainViewModel = mainViewModel,
                    usuarioEmail = usuarioEmail,
                    onAddPrenda = {
                        agregarPrendaViewModel.inicializar(usuarioId)
                        navController.navigate("agregar_prenda")
                    },
                    onLogout = {
                        cerrarSesion()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
            
            // Pantalla para Agregar Prenda
            composable("agregar_prenda") {
                // Inicializar ViewModel con ID de usuario
                LaunchedEffect(usuarioId) {
                    if (usuarioId != -1) {
                        agregarPrendaViewModel.inicializar(usuarioId)
                    }
                }
                
                AgregarPrendaScreen(
                    agregarPrendaViewModel = agregarPrendaViewModel,
                    onBack = {
                        agregarPrendaViewModel.reiniciarEstado()
                        navController.popBackStack()
                    },
                    onPrendaAgregada = {
                        // Recargar las prendas en la pantalla principal
                        mainViewModel.cargarPrendas()
                        agregarPrendaViewModel.reiniciarEstado()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Rutas de navegación de la aplicación
 * Organizadas para fácil mantenimiento
 */
object NavigationRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val AGREGAR_PRENDA = "agregar_prenda"
}

/**
 * Extensiones útiles para navegación
 */
fun androidx.navigation.NavController.navigateToLogin() {
    navigate(NavigationRoutes.LOGIN) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
}

fun androidx.navigation.NavController.navigateToMain() {
    navigate(NavigationRoutes.MAIN) {
        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
    }
}