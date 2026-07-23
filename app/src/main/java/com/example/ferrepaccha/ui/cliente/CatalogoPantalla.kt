package com.example.ferrepaccha.ui.cliente

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisClaro
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

private const val EMOJI_IMAGEN_POR_DEFECTO = "\uD83D\uDEE0\uFE0F"

private fun textoMarcaMedidas(producto: ProductoFirebase): String {
    return buildString {
        append(producto.marca.ifBlank { "Sin marca" })
        append(" · ")
        append(producto.medidaPrincipal.ifBlank { "Unidad" })
        if (producto.tieneSubMedida && producto.subMedida != null) {
            append(" · ")
            append(producto.subMedida.nombreSubMedida)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoPantalla(
    onFlechaRegresar: () -> Unit,
    onAgregarAlCarrito: () -> Unit,
    onNavegarAAdmin: () -> Unit,
    productViewModel: ProductoViewModel
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var cantidadCarritoSimulada by remember { mutableStateOf(0) }

    val listaProductosMuestra by productViewModel.listaProductos.collectAsState()
    val estaCargando by productViewModel.isLoading.collectAsState()

    var mostrarDetalleBottomSheet by remember { mutableStateOf(false) }
    var productoSeleccionadoDetalle by remember { mutableStateOf<ProductoFirebase?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onFlechaRegresar() }
            ) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Catálogo de Productos",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(text = "\uD83D\uDED2", fontSize = 22.sp)
                if (cantidadCarritoSimulada > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 8.dp, y = -4.dp)
                            .background(FerreAmarillo, CircleShape)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = cantidadCarritoSimulada.toString(),
                            color = FerreGrisOscuro,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
        //BARRA DE BUSQUEDA
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { nuevoTexto ->
                textoBusqueda = nuevoTexto
                productViewModel.verificarCodigoMaestro(nuevoTexto.trim()) { esCodigoMaestro ->
                    if (esCodigoMaestro) {
                        textoBusqueda = ""
                        onNavegarAAdmin()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Buscar por nombre, marca o código...", fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Text("\uD83D\uDD0D", modifier = Modifier.padding(start = 8.dp)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = FerreAmarillo,
                unfocusedIndicatorColor = Color(0xFFCBD5E1),
                focusedTextColor = Color.Black
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categorias = listOf(
                "Todos", "General", "Herramientas", "Pintura", "Tubería",
                "Hierro", "Madera", "Eléctrico", "Construcción", "Acabados"
            )
            categorias.forEach { cat ->
                val esSeleccionado = cat == categoriaSeleccionada

                Box(
                    modifier = Modifier
                        .background(
                            if (esSeleccionado) FerreAmarillo else Color(0xFF334155),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { categoriaSeleccionada = cat }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (esSeleccionado) FerreGrisOscuro else FerreBlanco,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FerreAmarillo)
            }
        } else {
            val listaFiltrada = listaProductosMuestra.filter { prod ->
                val cumpleFiltroTexto = prod.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        prod.marca.contains(textoBusqueda, ignoreCase = true) ||
                        prod.codigoProducto.contains(textoBusqueda, ignoreCase = true)
                val cumpleFiltroCategoria =
                    if (categoriaSeleccionada == "Todos") true else prod.categoria == categoriaSeleccionada

                cumpleFiltroTexto && cumpleFiltroCategoria
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFiltrada) { producto ->
                    CardProducto(
                        producto = producto,
                        onCardClick = {
                            productoSeleccionadoDetalle = producto
                            mostrarDetalleBottomSheet = true
                        },
                        onAgregarClick = { cantidad ->
                            cantidadCarritoSimulada += cantidad
                            onAgregarAlCarrito()
                        }
                    )
                }
            }
        }
    }

    if (mostrarDetalleBottomSheet && productoSeleccionadoDetalle != null) {
        val prod = productoSeleccionadoDetalle!!
        var cantidadDetalle by remember(prod.id) { mutableStateOf(0) }
        var medidaSeleccionada by remember(prod.id) { mutableStateOf("principal") }

        val precioSeleccionado = if (prod.tieneSubMedida && medidaSeleccionada == "sub" && prod.subMedida != null) {
            prod.subMedida.precioSubMedida
        } else {
            prod.precioPrincipal
        }

        val etiquetaMedidaSeleccionada = if (prod.tieneSubMedida && medidaSeleccionada == "sub" && prod.subMedida != null) {
            prod.subMedida.nombreSubMedida
        } else {
            prod.medidaPrincipal
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val context = LocalContext.current

        ModalBottomSheet(
            onDismissRequest = { mostrarDetalleBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = FerreBlanco,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                ZonaImagenProducto(
                    urlImagen = prod.urlImagen,
                    nombre = prod.nombre,
                    altura = 200.dp,
                    emojiFontSize = 72.sp,
                    esquinas = 20.dp,
                    badgeIzquierdo = {
                        Row(
                            modifier = Modifier
                                .background(FerreAmarillo, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prod.categoria,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = FerreGrisOscuro
                            )
                        }
                    },
                    badgeDerecho = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0x66000000), CircleShape)
                                .clickable { mostrarDetalleBottomSheet = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "X", color = FerreBlanco, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = prod.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = FerreGrisOscuro)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = textoMarcaMedidas(prod),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Código: ", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = prod.codigoProducto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = prod.descripcion, fontSize = 14.sp, color = Color(0xFF475569), lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(20.dp))

                if (prod.tieneSubMedida && prod.subMedida != null) {
                    Text(
                        text = "Selecciona cómo deseas comprarlo:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FerreGrisOscuro
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorMedidaVenta(
                        producto = prod,
                        medidaSeleccionada = medidaSeleccionada,
                        onMedidaSeleccionada = { medidaSeleccionada = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "$${String.format("%.2f", precioSeleccionado)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = FerreGrisOscuro
                )
                Text(
                    text = "Precio por ${etiquetaMedidaSeleccionada.lowercase()}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(FerreBlanco, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "-",
                            modifier = Modifier
                                .padding(horizontal = 18.dp)
                                .clickable { if (cantidadDetalle > 1) cantidadDetalle-- },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = cantidadDetalle.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "＋",
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickable { cantidadDetalle++ },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            cantidadCarritoSimulada += cantidadDetalle
                            onAgregarAlCarrito()
                            Toast.makeText(
                                context,
                                "✅ $cantidadDetalle x ${prod.nombre} ($etiquetaMedidaSeleccionada)",
                                Toast.LENGTH_SHORT
                            ).show()
                            mostrarDetalleBottomSheet = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Agregar al Carrito",
                            color = FerreGrisOscuro,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZonaImagenProducto(
    urlImagen: String,
    nombre: String,
    altura: Dp,
    emojiFontSize: TextUnit,
    esquinas: Dp = 18.dp,
    badgeIzquierdo: @Composable () -> Unit = {},
    badgeDerecho: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(altura)
            .clip(RoundedCornerShape(esquinas))
            .background(FerreGrisClaro)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            badgeIzquierdo()
            badgeDerecho()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (urlImagen.isNotBlank()) {
                AsyncImage(
                    model = urlImagen,
                    contentDescription = nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(text = EMOJI_IMAGEN_POR_DEFECTO, fontSize = emojiFontSize)
            }
        }
    }
}

@Composable
fun SelectorMedidaVenta(
    producto: ProductoFirebase,
    medidaSeleccionada: String,
    onMedidaSeleccionada: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (medidaSeleccionada == "principal") FerreAmarillo else Color(0xFFF1F5F9),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onMedidaSeleccionada("principal") }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = producto.medidaPrincipal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FerreGrisOscuro
                )
                Text(
                    text = "$${String.format("%.2f", producto.precioPrincipal)}",
                    fontSize = 12.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        producto.subMedida?.let { sub ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (medidaSeleccionada == "sub") FerreAmarillo else Color(0xFFF1F5F9),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onMedidaSeleccionada("sub") }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = sub.nombreSubMedida,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FerreGrisOscuro
                    )
                    Text(
                        text = "$${String.format("%.2f", sub.precioSubMedida)}",
                        fontSize = 12.sp,
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun CardProducto(
    producto: ProductoFirebase,
    onCardClick: () -> Unit,
    onAgregarClick: (Int) -> Unit
) {
    var cantidad by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = FerreBlanco),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCED2))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.clickable { onCardClick() }) {
                ZonaImagenProducto(
                    urlImagen = producto.urlImagen,
                    nombre = producto.nombre,
                    altura = 130.dp,
                    emojiFontSize = 48.sp,
                    badgeIzquierdo = {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF475569), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = producto.codigoProducto,
                                color = FerreBlanco,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    badgeDerecho = {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = producto.nombre,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = FerreGrisOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textoMarcaMedidas(producto),
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$${String.format("%.2f", producto.precioPrincipal)}",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = FerreGrisOscuro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(FerreBlanco, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "-",
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable { if (cantidad > 1) cantidad-- },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = cantidad.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.width(14.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "+",
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable { cantidad++ },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        onAgregarClick(cantidad)
                        Toast.makeText(
                            context,
                            "✅ $cantidad x ${producto.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .height(34.dp)
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Agregar",
                        color = FerreGrisOscuro,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCatalogoCliente() {
    CatalogoPantalla(
        onFlechaRegresar = {},
        onAgregarAlCarrito = {},
        onNavegarAAdmin = {},
        productViewModel = ProductoViewModel()
    )
}
