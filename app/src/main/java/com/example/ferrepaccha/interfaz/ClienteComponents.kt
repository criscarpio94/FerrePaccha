package com.example.ferrepaccha.interfaz

import android.R
import android.graphics.Paint
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
    val marcaMedida: String,
    val precio: String,
    val emoji: String,
    val emojiCategoria: String
)

//Lista de productos de prueba para las tarjetas
val listaProductosMuestra = listOf(
    ProductoSimulado("1", "HER-001", "Martillo Carpintero 16oz", "Stanley - Unidad", "$12.50", "\uD83D\uDD28", "\uD83D\uDD27"),
    ProductoSimulado("2", "HERR-002", "Taladro Percutor 700W", "Bosch - Unidad", "$89.99", "\uD83D\uDD0C", "\uD83D\uDD27"),
    ProductoSimulado("3", "PIN-001", "Pintura Latex Blanca", "Condor - Galón", "$28.00", "\uD83C\uDFA8", "\uD83C\uDFA8"),
    ProductoSimulado("4", "ELE-001", "Cable Eléctrico 12AWG", "Electrocable - Metro", "$1.80", "\uD83E\uDDF5", "⚡"),
    ProductoSimulado("5", "HER-003", "Juego Destornilladores", "Stanley - 6 piezas", "$14.00", "\uD83E\uDE9B", "\uD83D\uDD27"),
    ProductoSimulado("6", "CON-001", "Foco LED 12W", "Philips - Unidad", "$3.20", "\uD83D\uDCA1", "\uD83C\uDFD7\uFE0F")
)

//Pantalla de Catalogo de productos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponenteCatalogoCliente(
    onFlechaRegresar: () -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var cantidadCarritoSimulada by remember { mutableStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
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
            val categorias = listOf("Todos", "Herramientas", "Eléctrico", "Pintura", "Construcción", "Plomeria")
            categorias.forEach { cat ->
                val esSeleccionado = (cat == "Todos" && categoriaSeleccionada == "Todos") || (cat.contains(categoriaSeleccionada) && categoriaSeleccionada != "Todos")

                Box(
                    modifier = Modifier
                        .background(
                            if (esSeleccionado) FerreAmarillo else Color(0xFF334155),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable{
                            categoriaSeleccionada = if (cat == "Todos") "Todos" else cat.split(" ") [1]
                        }
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
            //Muestra la lista de acuerdo a la categoria seleccionada
            val listaFiltrada = if (categoriaSeleccionada =="Todos") {
                listaProductosMuestra
            } else {
                listaProductosMuestra.filter { it.codigo.startsWith(categoriaSeleccionada.substring(0, 3).uppercase()) }
            }

            items(listaFiltrada) { producto ->
                CardProducto(
                    producto = producto,
                    onAgregarClick = { cantidad ->
                        cantidadCarritoSimulada += cantidad
                        onAgregarAlCarrito()
                    }
                )
            }
        }
    }
}



//Diseño de tarjetas
@Composable
fun CardProducto(
    producto: ProductoSimulado,
    onAgregarClick: (Int) -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }

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
                    .background(FerreGrisFondoCard, RoundedCornerShape(18.dp)),
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
                    Text(text = producto.codigo, color = FerreBlanco, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
                    Text(text = producto.emojiCategoria, fontSize = 11.sp)
                }

                //Render
                Text(text = producto.emoji, fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Informacion
            Text(text = producto.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = FerreGrisOscuro, maxLines = 1)
            Text(text = producto.marcaMedida, fontSize = 11.sp, color = FerreGrisTextoEtiqueta, maxLines = 1)

            Spacer(modifier = Modifier.height(6.dp))

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
                        onAgregarClick(cantidad)
                        cantidad = 1
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