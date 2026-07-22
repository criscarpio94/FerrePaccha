package com.example.ferrepaccha.ui.admin

import androidx.compose.runtime.Composable
import com.example.ferrepaccha.ui.admin.dashboard.PantallaDashboard
import com.example.ferrepaccha.ui.admin.login.PantallaLogin
import com.example.ferrepaccha.ui.admin.pedidos.ComponenteGestionPedidos
import com.example.ferrepaccha.ui.admin.pedidos.DetallePedidoPantalla
import com.example.ferrepaccha.ui.admin.productos.FormularioProductoPantalla
import com.example.ferrepaccha.ui.admin.productos.GestionProductosPantalla
import com.example.ferrepaccha.ui.admin.usuarios.FormularioUsuarioPantalla
import com.example.ferrepaccha.ui.admin.usuarios.GestionUsuariosPantalla
import com.example.ferrepaccha.ui.cliente.ProductoViewModel

@Composable
fun PantallaAdmin(
    viewModel: AdminViewModel,
    productoViewModel: ProductoViewModel,
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
                nombreAdmin = viewModel.nombreAdministrador,
                rolUsuario = viewModel.rolUsuarioActual,
                onCerrarSesion = { viewModel.cerrarSesion() },
                onNavegarA = { destino -> viewModel.cambiarPantalla(destino) }
            )
        }

        TipoSubpantalla.GESTION_GERENTE -> {
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE GERENTE",
                onAgregarUsuarioClick = { viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO) },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.GESTION_EMPLEADOS -> {
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE EMPLEADOS",
                onAgregarUsuarioClick = { viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO) },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.FORMULARIO_USUARIO -> {
            FormularioUsuarioPantalla(
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) },
                onGuardarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                }
            )
        }

        TipoSubpantalla.GESTION_PRODUCTOS -> {
            GestionProductosPantalla(
                productoViewModel = productoViewModel,
                onAgregarProductoClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_PRODUCTO)
                },
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                }
            )
        }

        TipoSubpantalla.GESTION_PEDIDOS -> {
            ComponenteGestionPedidos(
                viewModel = viewModel,
                onFlechaRegresar = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.DETALLE_PEDIDO -> {
            DetallePedidoPantalla(
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                },
                onActualizarEstadoClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                },
                onExportarPdfClick = { }
            )
        }

        TipoSubpantalla.FORMULARIO_PRODUCTO -> {
            FormularioProductoPantalla(
                adminViewModel = viewModel,
                productoViewModel = productoViewModel,
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS)
                }
            )
        }
    }
}
