package com.example.ferrepaccha.interfaz

import android.R
import android.graphics.Paint
import android.widget.Space
import android.widget.Toast
import androidx.collection.emptyLongSet
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ferrepaccha.ProductoFirebase


//Colores a utilizar
val FerreGrisOscuro = Color(0xFF1E1E1E)
val FerreAmarillo = Color(0xFFFACC15)
val FerreBlanco = Color(0xFFFFFFFF)
val FerreGrisFondoCard = Color(0xFFF8FAFC)
val FerreGrisTextoEtiqueta = Color(0xFF64748B)

//Estructura basica de producto en el catalogo
data class ProductoSimulado(
    val id: String,
    val codigo: String,
    val nombre: String,
    val marca: String,
    val medida: String,
    val precio: String,
    val emoji: String,
    val categoriaNombre: String,
    val emojiCategoria: String,
    val descripcion: String
)

//Pantalla de Catalogo de productos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponenteCatalogoCliente(
    onFlechaRegresar: () -> Unit,
    onAgregarAlCarrito: () -> Unit,
    productViewModel: ProductoViewModel = viewModel() //motor de la base de datos
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var cantidadCarritoSimulada by remember { mutableStateOf(0) }

    //lista de productos reales que vienen desde internet
    val listaProductosMuestra by productViewModel.productos.collectAsState()
    val estaCargando by productViewModel.cargando.collectAsState()


    //Estados para la vista detalle de producto
    var mostrarDetalleBottomSheet by remember { mutableStateOf(false) }
    var productoSeleccionadoDetalle by remember { mutableStateOf<ProductoFirebase?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        //Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
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

            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
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

        //Barra de busqueda de productos
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Buscar por nombre, marca o código...", fontSize = 14.sp, color = FerreGrisTextoEtiqueta) },
            leadingIcon = { Text("\uD83D\uDD0D", modifier = Modifier.padding(start = 8.dp)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = FerreAmarillo,
                unfocusedIndicatorColor = Color(0xFFCBD5E1)
            )
        )

        //Fila de categorias
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categorias = listOf("General", "Herramientas", "Pintura", "Tubería", "Hierro", "Madera", "Eléctrico", "Construcción", "Acabados")
            categorias.forEach { cat ->
                val esSeleccionado = cat == categoriaSeleccionada

                Box(
                    modifier = Modifier
                        .background(
                            if (esSeleccionado) FerreAmarillo else Color(0xFF334155),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable{categoriaSeleccionada = cat}
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

        //Indicador de carga por si el internet esta lento
        if (estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FerreAmarillo)
            }
        } else {
            //Filtrado dinamico tiempo real desde Firebase
            val listaFiltrada = listaProductosMuestra.filter { prod ->
                val cumpleFiltroTexto = prod.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        prod.marca.contains(textoBusqueda, ignoreCase = true) ||
                        prod.codigoProducto.contains(textoBusqueda, ignoreCase = true)
                val cumpleFiltroCategoria = if (categoriaSeleccionada == "Todos") true else prod.categoria == categoriaSeleccionada

                cumpleFiltroTexto && cumpleFiltroCategoria
            }

            //Productos en tarjetas de 2 columnas
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

    //Expancion de detalle producto al dar clic en tarjeta
    if (mostrarDetalleBottomSheet && productoSeleccionadoDetalle != null) {
        val prod = productoSeleccionadoDetalle!!
        var cantidadDetalle by remember { mutableStateOf(0) }

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

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
                //Apartado para imagen mas grande
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(FerreGrisFondoCard, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    //Etiqueta para categoria
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(FerreAmarillo, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "\uD83D\uDCE6", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = prod.categoria, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FerreGrisOscuro)
                    }

                    //Bonton para cerrar el detalle de producto
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(28.dp)
                            .background(Color(0x66000000), CircleShape)
                            .clickable { mostrarDetalleBottomSheet = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "X", color = FerreBlanco, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    //Renderizar imagen o emoji
                    if (prod.urlImagen.isNotEmpty()) {
                        AsyncImage(model = prod.urlImagen, contentDescription = prod.nombre, modifier = Modifier.size(140.dp))
                    } else {
                        Text(text = prod.emoji, fontSize = 72.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = prod.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = FerreGrisOscuro)

                Spacer(modifier = Modifier.height(8.dp))

                //Fila para marca, medida y codigo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Marca: ", fontSize = 12.sp, color = FerreGrisTextoEtiqueta)
                    Text(text = prod.marca, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Medida: ", fontSize = 12.sp, color = FerreGrisTextoEtiqueta)
                    Text(text = prod.medidaPrincipal, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Código: ", fontSize = 12.sp, color = FerreGrisTextoEtiqueta)
                    Text(text = prod.codigoProducto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = prod.descripcion, fontSize = 14.sp, color = Color(0xFF475569), lineHeight = 20.sp)

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
                            fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Text(
                            text = cantidadDetalle.toString(),
                            fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center
                        )
                        Text(
                            text = "＋",
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickable { cantidadDetalle++ },
                            fontWeight = FontWeight.Bold, fontSize = 14.sp
                        )
                    }
                     //Boton de agregar al carrito
                    Button(
                        onClick = {
                            cantidadCarritoSimulada += cantidadDetalle
                            onAgregarAlCarrito()
                            mostrarDetalleBottomSheet = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "Agregar al Carrito", color = FerreGrisOscuro, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


//Diseño de tarjetas
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
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            //Parte superior de tarjeta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(FerreGrisFondoCard, RoundedCornerShape(18.dp))
                    .clickable { onCardClick() },
                contentAlignment = Alignment.Center
            ) {
                //Codigo de producto en la izquierda de imagen
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(0xFF475569), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = producto.codigoProducto, color = FerreBlanco, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                //Espacio amarillo para el icono de la categoria del producto
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(22.dp)
                        .background(FerreAmarillo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDEE0\uFE0F", fontSize = 11.sp)
                }

                //Imagen dinamica de Firebase Storage o emoji en caso de no detectar imagen
                if (producto.urlImagen.isNotEmpty()) {
                    AsyncImage(model = producto.urlImagen, contentDescription = producto.nombre, modifier = Modifier.size(80.dp))
                } else {
                    Text(text = producto.emoji, fontSize = 48.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Informacion
            Text(text = producto.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = FerreGrisOscuro, maxLines = 1)
            Text(text = "${producto.marca} - ${producto.medidaPrincipal}", fontSize = 11.sp, color = FerreGrisTextoEtiqueta, maxLines = 1)

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "$${String.format("%.2f", producto.precioPrincipal)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = FerreGrisOscuro)

            Spacer(modifier = Modifier.height(8.dp))

            //Controloes inferiores de tarjetas de productos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //Barra de seleccion de cantidad
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
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                    Text(
                        text = cantidad.toString(),
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.width(14.dp), textAlign = TextAlign.Center
                    )
                    Text(
                        text = "+",
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable { cantidad++ },
                        fontWeight = FontWeight.Bold, fontSize = 12.sp
                    )
                }

                //Boton para agregar producto al carrito
                Button(
                    onClick = {
                        if (cantidad > 0) {
                            onAgregarClick(cantidad)
                            Toast.makeText(context, "✅ Añadido al pedido", Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(context, "⚠\uFE0F Selecciona al menos 1 unidad", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .height(34.dp)
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Agregar", color = FerreGrisOscuro, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewCatalogoCliente() {
    ComponenteCatalogoCliente(
        onFlechaRegresar = {},
        onAgregarAlCarrito = {}
    )
}