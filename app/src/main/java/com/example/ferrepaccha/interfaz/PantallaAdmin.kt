package com.example.ferrepaccha.interfaz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferrepaccha.admin.SubPantallaDashboard
import com.example.ferrepaccha.admin.TipoSubpantalla
import com.example.ferrepaccha.admin.pedidos.ComponenteGestionPedidos
import com.example.ferrepaccha.admin.pedidos.DetallePedidoPantalla
import com.example.ferrepaccha.admin.productos.FormularioProductoPantalla
import com.example.ferrepaccha.admin.productos.GestionProductosPantalla
import com.example.ferrepaccha.admin.usuarios.FormularioUsuarioPantalla
import com.example.ferrepaccha.admin.usuarios.GestionUsuariosPantalla
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme

@Composable
fun PantallaAdmin(
    viewModel: AdminViewModel) {
    //Para monitorear las supantallas que se reciben de ViewModel
    val subPantallaActual = viewModel.pantallaActual

    when (subPantallaActual) {
        TipoSubpantalla.ADVERTENCIA -> {
            //llama a SubPantallaAdvertencia
            ComponenteAdvertencia(
                onRegresar = {

                },
                onContinuar = {
                    viewModel.cambiarPantalla(TipoSubpantalla.LOGIN)
                }
            )
        }

        TipoSubpantalla.LOGIN -> {
            //Llama a SubPantallaLogin
            ComponenteLogin(
                viewModel = viewModel,
                onFlechaRegresar = {
                    viewModel.cambiarPantalla(TipoSubpantalla.ADVERTENCIA)
                }
            )
        }

        TipoSubpantalla.DASHBOARD -> {
            //Llama a SubPantallaDashboard
            SubPantallaDashboard(
                nombreAdmin = viewModel.nombreAdministrador,
                rolUsuario = viewModel.rolUsuarioActual,
                onCerrarSesion = { viewModel.cerrarSesion() },
                onNavegarA = { destino -> viewModel.cambiarPantalla(destino) }
            )
        }

        TipoSubpantalla.GESTION_GERENTE -> {
            //Llama a SubPantallaGestionUsuarios para gestionar al GERENTE
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE GERENTE",
                onAgregarUsuarioClick = { viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO) },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.GESTION_EMPLEADOS -> {
            //Llama a SubPantallaGestionEmpleados para gestionar a los EMPLEADOS
            GestionUsuariosPantalla(
                tituloModulo = "GESTION DE EMPLEADOS",
                onAgregarUsuarioClick = { viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO) },
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.FORMULARIO_USUARIO -> {
            //Llama a SubPantallaFormularioUsuario
            FormularioUsuarioPantalla(
                onRegresarClick = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) },
                onGuardarClick = {
                //funcion para guardar en firebase
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                }
            )
        }

        TipoSubpantalla.GESTION_PRODUCTOS -> {
            //Llama a SubPantallaGestoinProductos
            GestionProductosPantalla(
                viewModel = viewModel,
                onAgregarProductoClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.FORMULARIO_PRODUCTO)
                },
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                }
            )
        }

        TipoSubpantalla.GESTION_PEDIDOS -> {
            //llama a subpantalla GestionPedidos
            ComponenteGestionPedidos(
                viewModel = viewModel,
                onFlechaRegresar = { viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD) }
            )
        }

        TipoSubpantalla.DETALLE_PEDIDO -> {
            //Llama a SubPantallaDetallePedido
            DetallePedidoPantalla(
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                },
                onActualizarEstadoClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.DASHBOARD)
                },
                onExportarPdfClick = {  }
            )
        }

        TipoSubpantalla.FORMULARIO_PRODUCTO -> {
            FormularioProductoPantalla(
                viewModel = viewModel, // Pasamos el viewModel para que controle el formulario y Firebase
                onRegresarClick = {
                    viewModel.cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS) // Acción para volver atrás
                }
            )
        }

    }
}


