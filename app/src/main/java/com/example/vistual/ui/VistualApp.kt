package com.example.vistual.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vistual.VistualApplication
import com.example.vistual.ui.theme.VistualTheme
import com.example.vistual.viewmodel.AuthViewModel
import com.example.vistual.viewmodel.MainViewModel
import com.example.vistual.viewmodel.AgregarPrendaViewModel
import com.example.vistual.viewmodel.OutfitViewModel
import com.example.vistual.viewmodel.ViewModelFactory

/**
 * Configuración de navegación principal de la aplicación
 */
@Composable
fun VistualApp() {
    val context = LocalContext.current
    val application = context.applicationContext as VistualApplication
    val navController = rememberNavController()

    // ViewModelFactory para crear todos los ViewModels
    val factory = ViewModelFactory(
        prendaRepository = application.prendaRepository,
        outfitRepository = application.outfitRepository,
        userRepository = application.userRepository
    )

    // Instancias de ViewModel obtenidas de la factory
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val agregarPrendaViewModel: AgregarPrendaViewModel = viewModel(factory = factory)
    val outfitViewModel: OutfitViewModel = viewModel(factory = factory)

    VistualTheme {
        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isLoggedIn()) NavigationRoutes.MAIN else NavigationRoutes.LOGIN
        ) {
            composable(NavigationRoutes.LOGIN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { navController.navigateToMain() },
                    onNavigateToRegister = { navController.navigate(NavigationRoutes.REGISTER) }
                )
            }
            composable(NavigationRoutes.REGISTER) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = { navController.navigateToLogin() },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(NavigationRoutes.MAIN) {
                val userId = authViewModel.currentUser?.id ?: -1
                LaunchedEffect(userId) {
                    if (userId != -1) {
                        mainViewModel.inicializar(userId)
                    }
                }
                MainScreen(
                    mainViewModel = mainViewModel,
                    usuarioEmail = authViewModel.currentUserEmail(),
                    onAddPrenda = { navController.navigate(NavigationRoutes.AGREGAR_PRENDA) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigateToLogin()
                    },
                    onNavigateToSavedOutfits = { navController.navigate(NavigationRoutes.SAVED_OUTFITS) },
                    onNavigateToCarouselOutfit = { navController.navigate(NavigationRoutes.CAROUSEL_OUTFIT) }
                )
            }
            composable(NavigationRoutes.AGREGAR_PRENDA) {
                val userId = authViewModel.currentUser?.id ?: -1
                LaunchedEffect(userId) {
                    if (userId != -1) {
                        agregarPrendaViewModel.inicializar(userId)
                    }
                }
                AgregarPrendaScreen(
                    agregarPrendaViewModel = agregarPrendaViewModel,
                    onBack = { navController.popBackStack() },
                    onPrendaAgregada = {
                        mainViewModel.cargarPrendas()
                        navController.popBackStack()
                    }
                )
            }
            composable(NavigationRoutes.SAVED_OUTFITS) {
                SavedOutfitsScreen(
                    outfitViewModel = outfitViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(NavigationRoutes.CAROUSEL_OUTFIT) {
                CarouselOutfitScreen(
                    mainViewModel = mainViewModel,
                    outfitViewModel = outfitViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddPrenda = { navController.navigate(NavigationRoutes.AGREGAR_PRENDA) }
                )
            }
        }
    }
}

object NavigationRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val AGREGAR_PRENDA = "agregar_prenda"
    const val SAVED_OUTFITS = "saved_outfits"
    const val CAROUSEL_OUTFIT = "carousel_outfit"
}

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
