package com.example.ferrepaccha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.ferrepaccha.interfaz.ComponenteCatalogoCliente
import com.example.ferrepaccha.ui.PantallaInicio
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.ferrepaccha.interfaz.PantallaAdmin


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavegacion()
        }
    }
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "admin" // cambio temporalmente para que inicie en el panel de administrador
    ) {
        // Ruta 1: Pantalla de inicio
        composable("inicio") {
            PantallaInicio(
                onNavegarAlCatalogo = {
                    navController.navigate("catalogo")
                }
            )
        }

        // Ruta 2: Catalogo de productos
        composable("catalogo") {
            ComponenteCatalogoCliente(
                onFlechaRegresar = {
                    navController.popBackStack()
                },
                onAgregarAlCarrito = {
                    // Pendiente para carrito
                }
            )
        }

        // 🔺 NUEVA RUTA 3: PANEL DE ADMINISTRACIÓN
        composable("admin") {
            val adminViewModel: com.example.ferrepaccha.interfaz.AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            PantallaAdmin(
                viewModel = adminViewModel
            ) 
        }
    }
}

