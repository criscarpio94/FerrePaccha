package com.example.ferrepaccha.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco

@Composable
fun ClienteScaffold(
    pantallaActual: String,
    cantidadCarrito: Int,
    onNavegar: (String) -> Unit,
    mostrarBarraInferior: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraNavegacionCliente(
                    pantallaActual = pantallaActual,
                    cantidadCarrito = cantidadCarrito,
                    onNavegar = onNavegar
                )
            }
        },
        content = content
    )
}

@Composable
fun BarraNavegacionCliente(
    pantallaActual: String,
    cantidadCarrito: Int,
    onNavegar: (String) -> Unit
) {
    val items = listOf(
        Triple("Inicio", "inicio", "🏠"),
        Triple("Catálogo", "catalogo", "📖"),
        Triple("Carrito", "carrito", "🛒"),
        Triple("Pedidos", "pedidos", "📋")
    )

    NavigationBar(
        containerColor = FerreBlanco,
        tonalElevation = 16.dp
    ) {
        items.forEach { (nombre, ruta, icono) ->
            val seleccionada = pantallaActual == ruta

            NavigationBarItem(
                selected = seleccionada,
                onClick = {
                    if (ruta != pantallaActual) {
                        onNavegar(ruta)
                    }
                },
                icon = {
                    Text(text = icono, fontSize = 22.sp)
                },
                label = {
                    Text(
                        text = nombre,
                        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionada) FerreAmarillo else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
