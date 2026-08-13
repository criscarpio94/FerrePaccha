package com.example.ferrepaccha.ui.admin.productos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.cliente.ProductoViewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@Composable
fun GestionProductosPantalla(
    productoViewModel: ProductoViewModel,
    adminViewModel: AdminViewModel,
    onAgregarProductoClick: () -> Unit,
    onRegresarClick: () -> Unit
) {
    val productos by productoViewModel.listaProductos.collectAsState()
    val context = LocalContext.current
    var productoAEliminar by remember { mutableStateOf<ProductoFirebase?>(null) }

    val productosFiltrados = productos.filter { producto ->
        val q = adminViewModel.busquedaProductoInput.trim()
        if (q.isEmpty()) true
        else {
            producto.nombre.contains(q, true) ||
                producto.marca.contains(q, true) ||
                producto.codigoProducto.contains(q, true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
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

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(
                onClick = onAgregarProductoClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("🔺 CARGAR NUEVO PRODUCTO", color = FerreGrisOscuro, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = adminViewModel.busquedaProductoInput,
                onValueChange = { adminViewModel.busquedaProductoInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre, marca o código...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = FerreAmarillo,
                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                    focusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (productosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay productos para mostrar.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productosFiltrados, key = { it.id }) { producto ->
                        TarjetaProductoItem(
                            producto = producto,
                            onEditarClick = { adminViewModel.cargarProductoParaEdicion(producto) },
                            onEliminarClick = { productoAEliminar = producto }
                        )
                    }
                }
            }
        }
    }

    productoAEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Está seguro de eliminar \"${producto.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    productoViewModel.eliminarProducto(producto.id) { ok ->
                        if (ok) {
                            adminViewModel.registrarAuditoriaProducto(
                                productoId = producto.id,
                                codigo = producto.codigoProducto,
                                nombre = producto.nombre,
                                motivo = "Eliminación de producto",
                                anteriores = mapOf("nombre" to producto.nombre),
                                nuevos = emptyMap()
                            )
                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        }
                        productoAEliminar = null
                    }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { productoAEliminar = null }) { Text("Cancelar") }
            }
        )
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (producto.urlImagen.isNotBlank()) {
                        AsyncImage(
                            model = producto.urlImagen,
                            contentDescription = producto.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(text = "🛠️", fontSize = 20.sp)
                    }
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
                    Text(
                        text = "${producto.codigoProducto} · ${producto.marca}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$ ${String.format("%.2f", producto.precioPrincipal)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = FerreGrisOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onEditarClick) {
                        Text("✏️ Editar", fontSize = 12.sp, color = Color.Blue)
                    }
                    TextButton(onClick = onEliminarClick) {
                        Text("🗑️ Eliminar", fontSize = 12.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}
