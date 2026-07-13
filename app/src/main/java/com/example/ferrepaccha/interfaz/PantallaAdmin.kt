package com.example.ferrepaccha.interfaz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferrepaccha.admin.SubPantallaDashboard
import com.example.ferrepaccha.admin.TipoSubpantalla
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme

@Composable
fun PantallaAdmin(
    viewModel: AdminViewModel) {
    //Para monitorear las supantallas que se reciben de ViewModel
    val subPantallaActual = viewModel.pantallaActual

    when (subPantallaActual) {
        TipoSubpantalla.ADVERTENCIA -> {
            //llama a SubPantallaAdvertencia
        }

        TipoSubpantalla.LOGIN -> {
            //Llama a SubPantallaLogin
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
        }

        TipoSubpantalla.GESTION_EMPLEADOS -> {
            //Llama a SubPantallaGestionEmpleados para gestionar a los EMPLEADOS
        }

        TipoSubpantalla.FORMULARIO_USUARIO -> {
            //Llama a SubPantallaFormularioUsuario
        }

        TipoSubpantalla.GESTION_PRODUCTOS -> {
            //Llama a SubPantallaGestoinProductos
        }

        TipoSubpantalla.DETALLE_PEDIDO -> {
            //Llama a SubPantallaDetallePedido
        }

    }
}


