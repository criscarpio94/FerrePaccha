package com.example.ferrepaccha.admin.productos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ProductoFirebase
import com.example.ferrepaccha.interfaz.AdminViewModel
import com.example.ferrepaccha.interfaz.FerreAmarillo
import com.example.ferrepaccha.interfaz.FerreBlanco
import com.example.ferrepaccha.interfaz.FerreGrisOscuro

@Composable
fun GestionProductosPantalla(
    viewModel: AdminViewModel, // 👈 Inyectamos el ViewModel real
    onAgregarProductoClick: () -> Unit,
    onRegresarClick: () -> Unit
) {
    // 🔄 Se ejecuta una sola vez al montar la pantalla para activar el SnapshotListener de Firebase
    LaunchedEffect(Unit) {
        viewModel.escucharProductosDelCatalogo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresarClick) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Gestión de Productos",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // CUERPO DE LA PANTALLA
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // BOTON PARA CREAR/CARGAR PRODUCTO
            Button(
                onClick = onAgregarProductoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "🔺 CARGAR NUEVO PRODUCTO",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CATÁLOGO DE PRODUCTOS (FIRESTORE)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔄 INDICADOR DE CARGA O LISTA REAL
            if (viewModel.estaCargandoProductos && viewModel.listaProductosReal.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FerreGrisOscuro)
                }
            } else if (viewModel.listaProductosReal.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay productos en la base de datos.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                // LISTA REAL CONECTADA A FIREBASE
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.listaProductosReal) { producto ->
                        TarjetaProductoItem(
                            producto = producto,
                            onEditarClick = { /* TODO: Cargar datos en formulario para editar */ },
                            onEliminarClick = { /* TODO: Eliminar de Firestore */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProductoItem(
    producto: ProductoFirebase,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del producto (Emoji/Imagen + Nombre)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Muestra el emoji guardado en la base de datos
                    Text(text = producto.emoji.ifEmpty { "🛠️" }, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = producto.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = FerreGrisOscuro,
                        maxLines = 2
                    )
                    if (producto.marca.isNotEmpty()) {
                        Text(
                            text = producto.marca,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Precio y acciones rápidas
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = "$${String.format("%.2f", producto.precioPrincipal)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = FerreGrisOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditarClick, modifier = Modifier.size(30.dp)) {
                        Text(text = "✏️", fontSize = 14.sp)
                    }
                    IconButton(onClick = onEliminarClick, modifier = Modifier.size(30.dp)) {
                        Text(text = "🗑️", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// 🔄 FUNCIÓN DE SOPORTE PARA EVITAR ERRORES EN OTRAS PANTALLAS (Como DetallePedidoPantalla)
@Composable
fun TarjetaProductoItem(
    nombre: String,
    precio: String,
    detalles: String,
    cantidad: Int
) {
    // Convertimos temporalmente los strings en un modelo ficticio para reusar el diseño limpio
    val productoFicticio = com.example.ferrepaccha.ProductoFirebase(
        nombre = nombre,
        precioPrincipal = precio.replace("$", "").toDoubleOrNull() ?: 0.0,
        marca = detalles
    )

    TarjetaProductoItem(
        producto = productoFicticio,
        onEditarClick = {},
        onEliminarClick = {}
    )
}

