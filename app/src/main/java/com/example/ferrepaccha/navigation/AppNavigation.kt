package com.example.ferrepaccha.navigation

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.AdminViewModelFactory
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
    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(application)
    )
    val carritoViewModel: CarritoViewModel = viewModel(
        factory = CarritoViewModelFactory(application)
    )
    val pedidoViewModel: PedidoViewModel = viewModel(
        factory = PedidoViewModelFactory(application)
    )

    val cantidadCarrito by carritoViewModel.cantidadTotal.collectAsState()
    val sesionAdminActiva by adminViewModel.sesionAdminActiva.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, pedidoViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val activity = context as? ComponentActivity
                if (activity?.isChangingConfigurations != true) {
                    pedidoViewModel.limpiarSesionConsulta()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun navegarCliente(ruta: String) {
        navController.navigate(ruta) {
            popUpTo("inicio") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navegarAdmin() {
        adminViewModel.ingresarPanelAdmin()
        navController.navigate("login_admin")
    }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                cantidadCarrito = cantidadCarrito,
                sesionAdminActiva = sesionAdminActiva,
                onNavegar = { ruta -> navegarCliente(ruta) },
                onNavegarAdmin = { navegarAdmin() }
            )
        }

        composable("catalogo") {
            CatalogoPantalla(
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) },
                onNavegarAAdmin = { navegarAdmin() },
                productViewModel = productoViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable("carrito") {
            CarritoPantalla(
                carritoViewModel = carritoViewModel,
                cantidadCarrito = cantidadCarrito,
                onNavegar = { ruta -> navegarCliente(ruta) },
                onPedidoConfirmado = { cedula, pedidoId ->
                    pedidoViewModel.registrarPedidoRecienCreado(cedula, pedidoId)
                    navController.navigate("pedidos") {
                        popUpTo("inicio") {
                            saveState = true
                        }
                        launchSingleTop = false
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

        composable("login_admin") {
            PantallaAdmin(
                viewModel = adminViewModel,
                productoViewModel = productoViewModel,
                carritoViewModel = carritoViewModel,
                cantidadCarrito = cantidadCarrito,
                onRegresarAlCatalogo = {
                    navController.popBackStack()
                }
            )
        }
    }
}
