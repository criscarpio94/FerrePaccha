package com.example.ferrepaccha.interfaz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme

@Composable
fun PantallaAdmin(
    viewModel: AdminViewModel = viewModel(),
    onRegresarAlInicio: () -> Unit = {}
) {
    //Control para el estado del ViewModel
    when (viewModel.pantallaActual) {
        SubPantallaAdmin.ADVERTENCIA -> {
            ComponenteAdvertencia(
                onRegresar = onRegresarAlInicio,
                onContinuar = { viewModel.cambiarPantalla(SubPantallaAdmin.LOGIN) }
            )
        }

        SubPantallaAdmin.LOGIN -> {
            ComponenteLogin(
                viewModel = viewModel,
                onFlechaRegresar = { viewModel.cambiarPantalla(SubPantallaAdmin.ADVERTENCIA) }
            )
        }

        SubPantallaAdmin.DASHBOARD -> {
            //Pendiente completar
        }

        SubPantallaAdmin.CARGAR_PRODUCTO -> {
            //Pendiente completar
        }

        SubPantallaAdmin.GESTION_PEDIDOS -> {
            //Pendiente completar
        }

        SubPantallaAdmin.DETALLE_PEDIDO -> {
            //Pendiente completar
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaAdminPreview() {
    FerrePacchaTheme {
        PantallaAdmin()
    }
}