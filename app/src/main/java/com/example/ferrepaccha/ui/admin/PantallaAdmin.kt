package com.example.ferrepaccha.ui.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ferrepaccha.ui.admin.dashboard.PantallaDashboard
import com.example.ferrepaccha.ui.admin.login.PantallaLogin
import com.example.ferrepaccha.ui.admin.pedidos.ComponenteBuscarPedidos
import com.example.ferrepaccha.ui.admin.pedidos.ComponenteGestionPedidos
import com.example.ferrepaccha.ui.admin.pedidos.DetallePedidoPantalla
import com.example.ferrepaccha.ui.admin.productos.FormularioProductoPantalla
import com.example.ferrepaccha.ui.admin.productos.GestionProductosPantalla
import com.example.ferrepaccha.ui.admin.usuarios.FormularioUsuarioPantalla
import com.example.ferrepaccha.ui.admin.usuarios.GestionUsuariosPantalla
import com.example.ferrepaccha.ui.cliente.CarritoViewModel
import com.example.ferrepaccha.ui.cliente.CatalogoPantalla
import com.example.ferrepaccha.ui.cliente.ProductoViewModel

@Composable
fun PantallaAdmin(
    viewModel: AdminViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    cantidadCarrito: Int,
    onRegresarAlCatalogo: () -> Unit
) {
    when (viewModel.pantallaActual) {
        TipoSubpantalla.LOGIN -> {
            PantallaLogin(
                viewModel = viewModel,
                onFlechaRegresar = onRegresarAlCatalogo
            )
        }

        TipoSubpantalla.DASHBOARD -> {
            PantallaDashboard(
                adminViewModel = viewModel,
                onCerrarSesion = { viewModel.cerrarSesion() },
                onNavegarA = { destino -> viewModel.cambiarPantalla(destino) },
                onAbrirDetallePedido = { id ->
                    viewModel.abrirDetallePedido(id, TipoSubpantalla.DASHBOARD)
                }
            )
        }

        TipoSubpantalla.GESTION_GERENTE -> {
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE GERENTE",
                rolFiltro = "GERENTE",
                adminViewModel = viewModel,
                onAgregarUsuarioClick = {
                    viewModel.limpiarFormularioUsuario()
                    viewModel.rolUsuarioFormulario = "GERENTE"
                    viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO)
                },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.GESTION_EMPLEADOS -> {
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE EMPLEADOS",
                rolFiltro = "EMPLEADO",
                adminViewModel = viewModel,
                onAgregarUsuarioClick = {
                    viewModel.limpiarFormularioUsuario()
                    viewModel.rolUsuarioFormulario = "EMPLEADO"
                    viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO)
                },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.FORMULARIO_USUARIO -> {
            FormularioUsuarioPantalla(
                adminViewModel = viewModel,
                onRegresarClick = {
                    viewModel.limpiarFormularioUsuario()
                    viewModel.cambiarPantalla(
                        if (viewModel.rolGestionActual == "GERENTE") TipoSubpantalla.GESTION_GERENTE
                        else TipoSubpantalla.GESTION_EMPLEADOS
                    )
                }
            )
        }

        TipoSubpantalla.GESTION_PRODUCTOS -> {
            GestionProductosPantalla(
                productoViewModel = productoViewModel,
                adminViewModel = viewModel,
                onAgregarProductoClick = {
                    viewModel.limpiarFormularioProducto()
                    viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_PRODUCTO)
                },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.GESTION_PEDIDOS -> {
            ComponenteGestionPedidos(
                viewModel = viewModel,
                onFlechaRegresar = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.BUSCAR_PEDIDOS -> {
            ComponenteBuscarPedidos(
                viewModel = viewModel,
                onFlechaRegresar = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.DETALLE_PEDIDO -> {
            DetallePedidoPantalla(
                adminViewModel = viewModel,
                onRegresarClick = { viewModel.cambiarPantalla(viewModel.pantallaRetornoPedido) }
            )
        }

        TipoSubpantalla.FORMULARIO_PRODUCTO -> {
            FormularioProductoPantalla(
                adminViewModel = viewModel,
                productoViewModel = productoViewModel,
                onRegresarClick = {
                    viewModel.limpiarFormularioProducto()
                    viewModel.cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS)
                }
            )
        }

        TipoSubpantalla.VER_CATALOGO -> {
            val cantidad by carritoViewModel.cantidadTotal.collectAsState()
            CatalogoPantalla(
                cantidadCarrito = cantidad,
                onNavegar = { ruta ->
                    if (ruta == "catalogo") return@CatalogoPantalla
                },
                onNavegarAAdmin = { },
                productViewModel = productoViewModel,
                carritoViewModel = carritoViewModel,
                mostrarBarraInferior = false,
                onRegresarAdmin = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }
    }
}
