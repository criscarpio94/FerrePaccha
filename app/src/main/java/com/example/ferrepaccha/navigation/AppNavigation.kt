package com.example.ferrepaccha.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.PantallaAdmin
import com.example.ferrepaccha.ui.cliente.CatalogoPantalla
import com.example.ferrepaccha.ui.cliente.PantallaInicio
import com.example.ferrepaccha.ui.cliente.ProductoViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val productoViewModel: ProductoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                onNavegarAlCatalogo = {
                    navController.navigate("catalogo")
                }
            )
        }

        composable("catalogo") {
            CatalogoPantalla(
                onFlechaRegresar = {
                    navController.popBackStack()
                },
                onAgregarAlCarrito = { },
                onNavegarAAdmin = {
                    navController.navigate("login_admin")
                },
                productViewModel = productoViewModel
            )
        }

        composable("login_admin") {
            val adminViewModel: AdminViewModel = viewModel()
            PantallaAdmin(
                viewModel = adminViewModel,
                productoViewModel = productoViewModel,
                onRegresarAlCatalogo = {
                    navController.popBackStack()
                }
            )
        }
    }
}
