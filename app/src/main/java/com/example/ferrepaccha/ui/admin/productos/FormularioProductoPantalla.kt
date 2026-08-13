package com.example.ferrepaccha.ui.admin.productos

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.TipoSubpantalla
import com.example.ferrepaccha.ui.cliente.ProductoViewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoPantalla(
    adminViewModel: AdminViewModel,
    productoViewModel: ProductoViewModel,
    onRegresarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val listaCategorias = listOf(
        "General", "Herramientas", "Pintura", "Tubería",
        "Hierro", "Madera", "Eléctrico", "Construcción", "Acabados"
    )
    val esEdicion = adminViewModel.productoEnEdicionId.isNotBlank()
    val productos by productoViewModel.listaProductos.collectAsState()
    var mostrarSelectorImagen by remember { mutableStateOf(false) }
    var uriCamaraTemporal by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) adminViewModel.imagenSeleccionadaUri = uri
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) adminViewModel.imagenSeleccionadaUri = uriCamaraTemporal
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val archivo = File(context.cacheDir, "captura_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
            uriCamaraTemporal = uri
            launcherCamara.launch(uri)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(FerreBlanco)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresarClick) {
                Text("←", color = FerreBlanco, fontSize = 24.sp)
            }
            Text(
                text = if (esEdicion) "Editar Producto" else "Nuevo Producto",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { mostrarSelectorImagen = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        adminViewModel.imagenSeleccionadaUri != null -> {
                            AsyncImage(
                                model = adminViewModel.imagenSeleccionadaUri,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        adminViewModel.urlImagenActual.isNotBlank() -> {
                            AsyncImage(
                                model = adminViewModel.urlImagenActual,
                                contentDescription = "Imagen actual",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📸", fontSize = 36.sp)
                                Text("Toca para subir imagen", fontWeight = FontWeight.Bold)
                                Text("Cámara o galería", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    if (adminViewModel.estaSubiendoImagen) {
                        CircularProgressIndicator(color = FerreAmarillo)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = adminViewModel.codigoProductoInput,
                    onValueChange = { adminViewModel.codigoProductoInput = it },
                    label = { Text("Código producto") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = FerreAmarillo,
                        unfocusedIndicatorColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                )
                OutlinedTextField(
                    value = adminViewModel.marcaProductoInput,
                    onValueChange = { adminViewModel.marcaProductoInput = it },
                    label = { Text("Marca") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = FerreAmarillo,
                        unfocusedIndicatorColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = adminViewModel.nombreProductoInput,
                onValueChange = { adminViewModel.nombreProductoInput = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = FerreAmarillo,
                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = adminViewModel.medidaProductoInput,
                onValueChange = { adminViewModel.medidaProductoInput = it },
                label = { Text("Medida principal (Ej: Unidad, Galón)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = FerreAmarillo,
                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = adminViewModel.descripcionProductoInput,
                onValueChange = { adminViewModel.descripcionProductoInput = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = FerreAmarillo,
                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = adminViewModel.menuCategoriasExpandido,
                onExpandedChange = { adminViewModel.menuCategoriasExpandido = !adminViewModel.menuCategoriasExpandido }
            ) {
                OutlinedTextField(
                    value = adminViewModel.categoriaProductoInput,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(adminViewModel.menuCategoriasExpandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = FerreAmarillo,
                        unfocusedIndicatorColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                )
                ExposedDropdownMenu(
                    expanded = adminViewModel.menuCategoriasExpandido,
                    onDismissRequest = { adminViewModel.menuCategoriasExpandido = false }
                ) {
                    listaCategorias.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                adminViewModel.categoriaProductoInput = item
                                adminViewModel.menuCategoriasExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            var menuIvaExpandido by remember { mutableStateOf(false) }
            val opcionesIva = listOf(15.0, 5.0, 0.0)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = adminViewModel.precioProductoInput,
                    onValueChange = { adminViewModel.precioProductoInput = it },
                    label = { Text("Precio ($)") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = FerreAmarillo,
                        unfocusedIndicatorColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                )
                ExposedDropdownMenuBox(
                    expanded = menuIvaExpandido,
                    onExpandedChange = { menuIvaExpandido = !menuIvaExpandido },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = "${adminViewModel.porcentajeIvaInput.toInt()}%",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("IVA") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(menuIvaExpandido) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = FerreAmarillo,
                            unfocusedIndicatorColor = Color(0xFFCBD5E1),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = menuIvaExpandido,
                        onDismissRequest = { menuIvaExpandido = false }
                    ) {
                        opcionesIva.forEach { valor ->
                            DropdownMenuItem(
                                text = { Text("${valor.toInt()}%") },
                                onClick = {
                                    adminViewModel.porcentajeIvaInput = valor
                                    menuIvaExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Maneja sub-medida?", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = adminViewModel.tieneSubMedidaInput,
                            onCheckedChange = { adminViewModel.tieneSubMedidaInput = it }
                        )
                    }
                    if (adminViewModel.tieneSubMedidaInput) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = adminViewModel.nombreSubMedidaInput,
                                onValueChange = { adminViewModel.nombreSubMedidaInput = it },
                                label = { Text("Sub-medida") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = FerreAmarillo,
                                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                            )
                            OutlinedTextField(
                                value = adminViewModel.precioSubMedidaInput,
                                onValueChange = { adminViewModel.precioSubMedidaInput = it },
                                label = { Text("Precio sub ($)") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = FerreAmarillo,
                                    unfocusedIndicatorColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                            )
                        }
                    }
                }
            }

            if (esEdicion) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = adminViewModel.motivoAuditoriaInput,
                    onValueChange = { adminViewModel.motivoAuditoriaInput = it },
                    label = { Text("Motivo del cambio (auditoría)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = FerreAmarillo,
                        unfocusedIndicatorColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (adminViewModel.nombreProductoInput.isBlank() ||
                        adminViewModel.codigoProductoInput.isBlank() ||
                        adminViewModel.precioProductoInput.isBlank()
                    ) {
                        Toast.makeText(context, "Complete código, nombre y precio", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (esEdicion && adminViewModel.motivoAuditoriaInput.isBlank()) {
                        Toast.makeText(context, "Indique el motivo del cambio", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    adminViewModel.estaSubiendoImagen = true
                    val producto = adminViewModel.construirProductoDesdeFormulario()
                    val productoAnterior = productos.find { it.id == producto.id }

                    productoViewModel.guardarProductoAlCatalogo(
                        context = context,
                        producto = producto,
                        imagenUri = adminViewModel.imagenSeleccionadaUri,
                        onExito = { advertencia ->
                            adminViewModel.estaSubiendoImagen = false
                            if (esEdicion && productoAnterior != null) {
                                adminViewModel.registrarAuditoriaProducto(
                                    productoId = producto.id,
                                    codigo = producto.codigoProducto,
                                    nombre = producto.nombre,
                                    motivo = adminViewModel.motivoAuditoriaInput,
                                    anteriores = mapOf(
                                        "nombre" to productoAnterior.nombre,
                                        "precio" to productoAnterior.precioPrincipal
                                    ),
                                    nuevos = mapOf(
                                        "nombre" to producto.nombre,
                                        "precio" to producto.precioPrincipal
                                    )
                                )
                            } else {
                                adminViewModel.registrarAuditoriaProducto(
                                    productoId = producto.id,
                                    codigo = producto.codigoProducto,
                                    nombre = producto.nombre,
                                    motivo = "Creación de producto",
                                    anteriores = emptyMap(),
                                    nuevos = mapOf("nombre" to producto.nombre)
                                )
                            }
                            adminViewModel.limpiarFormularioProducto()
                            adminViewModel.cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS)
                            val msg = advertencia ?: "✅ Producto guardado"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            adminViewModel.estaSubiendoImagen = false
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (esEdicion) "💾 GUARDAR CAMBIOS" else "💾 GUARDAR PRODUCTO",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    if (mostrarSelectorImagen) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorImagen = false },
            title = { Text("Seleccionar imagen") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            mostrarSelectorImagen = false
                            launcherGaleria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("📁 Galería") }
                    OutlinedButton(
                        onClick = {
                            mostrarSelectorImagen = false
                            launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("📷 Cámara") }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { mostrarSelectorImagen = false }) { Text("Cancelar") }
            }
        )
    }
}
