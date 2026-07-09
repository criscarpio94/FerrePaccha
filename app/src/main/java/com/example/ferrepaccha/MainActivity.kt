package com.example.ferrepaccha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.ferrepaccha.interfaz.ComponenteCatalogoCliente
import com.example.ferrepaccha.ui.PantallaInicio
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


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
    //Controladora para navegar entre pantallas
    val navController = rememberNavController()

    //rutas
    NavHost(
        navController = navController,
        startDestination = "inicio" //la aplicacion inicia en la PantallaInicio
    ) {
        //Ruta 1: Pantalla de inicio
        composable("inicio") {
            PantallaInicio(
                onNavegarAlCatalogo = {
                    navController.navigate("catalogo")
                }
            )
        }

        //Ruta 2: Catalogo de productos
        composable("catalogo") {
            ComponenteCatalogoCliente(
                onFlechaRegresar = {
                    navController.popBackStack() //Para regresar
                },
                onAgregarAlCarrito = {
                    //Pendiente para carrito de compras
                }
            )
        }
    }
}

