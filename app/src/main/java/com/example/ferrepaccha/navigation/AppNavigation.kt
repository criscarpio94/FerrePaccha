package com.example.ferrepaccha.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.PantallaAdmin
import com.example.ferrepaccha.ui.cliente.CarritoPantalla
import com.example.ferrepaccha.ui.cliente.CarritoViewModel
import com.example.ferrepaccha.ui.cliente.CarritoViewModelFactory
import com.example.ferrepaccha.ui.cliente.CatalogoPantalla
import com.example.ferrepaccha.ui.cliente.PantallaInicio
import com.example.ferrepaccha.ui.cliente.PedidoViewModel
import com.example.ferrepaccha.ui.cliente.PedidoViewModelFactory
import com.example.ferrepaccha.ui.cliente.PedidosPantalla
import com.example.ferrepaccha.ui.cliente.ProductoViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val productoViewModel: ProductoViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel(
        factory = CarritoViewModelFactory(application)
    )
    val pedidoViewModel: PedidoViewModel = viewModel(
        factory = PedidoViewModelFactory(application)
    )

    val cantidadCarrito by carritoViewModel.cantidadTotal.collectAsState()

    fun navegarCliente(ruta: String) {
        navController.navigate(ruta) {
            popUpTo("inicio") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) }
            )
        }

        composable("catalogo") {
            CatalogoPantalla(
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) },
                onNavegarAAdmin = {
                    navController.navigate("login_admin")
                },
                productViewModel = productoViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable("carrito") {
            CarritoPantalla(
                carritoViewModel = carritoViewModel,
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) },
                onPedidoConfirmado = { cedula ->
                    navController.navigate("pedidos/$cedula") {
                        popUpTo("inicio") {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("pedidos") {
            PedidosPantalla(
                pedidoViewModel = pedidoViewModel,
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) }
            )
        }

        composable("pedidos/{cedula}") { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula").orEmpty()
            PedidosPantalla(
                pedidoViewModel = pedidoViewModel,
                cantidadCarrito = cantidadCarrito,
                cedulaInicial = cedula,
                onNavegar = { ruta -> navegarCliente(ruta) }
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
