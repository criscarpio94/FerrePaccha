package com.example.ferrepaccha.admin.productos

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.interfaz.AdminViewModel
import com.example.ferrepaccha.interfaz.FerreAmarillo
import com.example.ferrepaccha.interfaz.FerreBlanco
import com.example.ferrepaccha.interfaz.FerreGrisOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoPantalla(
    viewModel: AdminViewModel,
    onRegresarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val listaCategorias = listOf("General", "Herramientas", "Pintura", "Tubería", "Hierro", "Madera", "Eléctrico", "Construcción", "Acabados")
    val listaIva = listOf(0.0, 5.0, 15.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        // ENCABEZADO OSCURO
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
                text = "Registrar / Editar Producto",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // CUERPO DEL FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 📸 CONTENEDOR MULTIMEDIA INTERACTIVO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clickable {
                        // TODO: Aquí dispararás el Intent de la cámara o galería en la integración nativa
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (viewModel.estaSubiendoImagen) {
                        CircularProgressIndicator(color = FerreGrisOscuro)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Subiendo a ImgBb...", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        Text(
                            text = if (viewModel.imagenSeleccionadaUri != null) "✅ Imagen Cargada" else "📸",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (viewModel.imagenSeleccionadaUri != null) "Toca para cambiar de fotografía" else "Tomar Foto o Subir desde Galería",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = FerreGrisOscuro
                        )
                        Text(
                            text = "Se procesará automáticamente mediante API",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FILA: CÓDIGO Y EMOJI
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CÓDIGO PRODUCTO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.codigoProductoInput,
                        onValueChange = { viewModel.codigoProductoInput = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.width(80.dp)) {
                    Text(text = "EMOJI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = "🛠️", // Dejado por defecto como fallback estático en interfaz
                        onValueChange = {},
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CAMPO: NOMBRE DEL PRODUCTO
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "NOMBRE DEL PRODUCTO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.nombreProductoInput,
                    onValueChange = { viewModel.nombreProductoInput = it },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CAMPO: DESCRIPCIÓN
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "DESCRIPCIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.descripcionProductoInput,
                    onValueChange = { viewModel.descripcionProductoInput = it },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FILA: MARCA Y CATEGORÍA (DROPDOWN MENU)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "MARCA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.marcaProductoInput,
                        onValueChange = { viewModel.marcaProductoInput = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Dropdown dinámico para tus Categorías de base de datos
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CATEGORÍA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = viewModel.menuCategoriasExpandido,
                        onExpandedChange = { viewModel.menuCategoriasExpandido = !viewModel.menuCategoriasExpandido }
                    ) {
                        OutlinedTextField(
                            value = viewModel.categoriaProductoInput.ifEmpty { "General" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.menuCategoriasExpandido) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = viewModel.menuCategoriasExpandido,
                            onDismissRequest = { viewModel.menuCategoriasExpandido = false }
                        ) {
                            listaCategorias.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        viewModel.categoriaProductoInput = item
                                        viewModel.menuCategoriasExpandido = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FILA: MEDIDA PRINCIPAL, PRECIO PRINCIPAL E IVA (DROPDOWN)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "MEDIDA PRINCIPAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.medidaProductoInput,
                        onValueChange = { viewModel.medidaProductoInput = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(text = "PRECIO * ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.precioProductoInput,
                        onValueChange = { viewModel.precioProductoInput = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Dropdown dinámico para el IVA (0%, 5%, 15%)
                var ivaExpandido by remember { mutableStateOf(false) }
                Column(modifier = Modifier.weight(0.6f)) {
                    Text(text = "IVA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = ivaExpandido,
                        onExpandedChange = { ivaExpandido = !ivaExpandido }
                    ) {
                        OutlinedTextField(
                            value = "${viewModel.porcentajeIvaInput.toInt()}%",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ivaExpandido) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = ivaExpandido,
                            onDismissRequest = { ivaExpandido = false }
                        ) {
                            listaIva.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("${item.toInt()}%") },
                                    onClick = {
                                        viewModel.porcentajeIvaInput = item
                                        ivaExpandido = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN: SUBMEDIDA (MAP DE FIRESTORE)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "¿Maneja Sub-Medida?", fontWeight = FontWeight.Bold, color = FerreGrisOscuro, fontSize = 14.sp)
                            Text(text = "Permite vender por unidades fraccionadas", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = viewModel.tieneSubMedidaInput,
                            onCheckedChange = { viewModel.tieneSubMedidaInput = it }
                        )
                    }

                    if (viewModel.tieneSubMedidaInput) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(text = "NOMBRE SUB-MEDIDA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = viewModel.nombreSubMedidaInput,
                                    onValueChange = { viewModel.nombreSubMedidaInput = it },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Column(modifier = Modifier.weight(0.8f)) {
                                Text(text = "PRECIO SUB ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = viewModel.precioSubMedidaInput,
                                    onValueChange = { viewModel.precioSubMedidaInput = it },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // BOTÓN GUARDAR PRODUCTO REAL
            Button(
                onClick = { viewModel.guardarProductoAlCatalogo(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💾 GUARDAR EN BASE DE DATOS",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
