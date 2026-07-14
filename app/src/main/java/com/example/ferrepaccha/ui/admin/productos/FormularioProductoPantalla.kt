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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.TipoSubpantalla
import com.example.ferrepaccha.ui.cliente.ProductoViewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoPantalla(
    adminViewModel: AdminViewModel,
    productoViewModel: ProductoViewModel,
    onRegresarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val listaCategorias = listOf("General", "Herramientas", "Pintura", "Tubería", "Hierro", "Madera", "Eléctrico", "Construcción", "Acabados")

    Column(modifier = Modifier.fillMaxSize().background(FerreBlanco)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(FerreGrisOscuro).windowInsetsPadding(WindowInsets.statusBars).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresarClick) { Text("←", color = FerreBlanco, fontSize = 24.sp) }
            Text("Registrar / Editar Producto", color = FerreBlanco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(scrollState)) {
            Card(modifier = Modifier.fillMaxWidth().height(130.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (adminViewModel.imagenSeleccionadaUri != null) "✅ Imagen Cargada" else "📸 Subir Imagen")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = adminViewModel.codigoProductoInput, onValueChange = { adminViewModel.codigoProductoInput = it }, label = { Text("Código") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "🛠️", onValueChange = {}, label = { Text("Emoji") }, modifier = Modifier.width(80.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(value = adminViewModel.nombreProductoInput, onValueChange = { adminViewModel.nombreProductoInput = it }, label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(value = adminViewModel.descripcionProductoInput, onValueChange = { adminViewModel.descripcionProductoInput = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Spacer(modifier = Modifier.height(14.dp))

            ExposedDropdownMenuBox(expanded = adminViewModel.menuCategoriasExpandido, onExpandedChange = { adminViewModel.menuCategoriasExpandido = !adminViewModel.menuCategoriasExpandido }) {
                OutlinedTextField(value = adminViewModel.categoriaProductoInput, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(adminViewModel.menuCategoriasExpandido) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = adminViewModel.menuCategoriasExpandido, onDismissRequest = { adminViewModel.menuCategoriasExpandido = false }) {
                    listaCategorias.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = { adminViewModel.categoriaProductoInput = item; adminViewModel.menuCategoriasExpandido = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = adminViewModel.precioProductoInput, onValueChange = { adminViewModel.precioProductoInput = it }, label = { Text("Precio") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "${adminViewModel.porcentajeIvaInput.toInt()}%", onValueChange = {}, readOnly = true, label = { Text("IVA") }, modifier = Modifier.width(100.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Maneja Sub-Medida?", fontWeight = FontWeight.Bold)
                        Switch(checked = adminViewModel.tieneSubMedidaInput, onCheckedChange = { adminViewModel.tieneSubMedidaInput = it })
                    }
                    if (adminViewModel.tieneSubMedidaInput) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(value = adminViewModel.nombreSubMedidaInput, onValueChange = { adminViewModel.nombreSubMedidaInput = it }, label = { Text("NOMBRE SUB-MEDIDA") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = adminViewModel.precioSubMedidaInput, onValueChange = { adminViewModel.precioSubMedidaInput = it }, label = { Text("PRECIO SUB ($)") }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val productoNuevo = ProductoFirebase(
                        nombre = adminViewModel.nombreProductoInput,
                        precioPrincipal = adminViewModel.precioProductoInput.toDoubleOrNull() ?: 0.0,
                        codigoProducto = adminViewModel.codigoProductoInput,
                        marca = adminViewModel.marcaProductoInput,
                        medidaPrincipal = adminViewModel.medidaProductoInput,
                        categoria = adminViewModel.categoriaProductoInput,
                        descripcion = adminViewModel.descripcionProductoInput
                    )
                    productoViewModel.guardarProductoAlCatalogo(
                        context, productoNuevo, adminViewModel.imagenSeleccionadaUri,
                        onExito = {
                            adminViewModel.limpiarFormularioProducto()
                            adminViewModel.cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS)
                        },
                        onError = { Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo)
            ) {
                Text("💾 GUARDAR PRODUCTO", color = FerreGrisOscuro, fontWeight = FontWeight.Black)
            }
        }
    }
}
