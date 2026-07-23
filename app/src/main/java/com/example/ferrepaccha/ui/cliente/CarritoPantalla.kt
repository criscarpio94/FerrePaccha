package com.example.ferrepaccha.ui.cliente

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ferrepaccha.data.model.ItemCarrito
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisClaro
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import com.example.ferrepaccha.ui.theme.FerreVerde

private enum class PasoCarrito {
    LISTA, TERMINOS, FORMULARIO
}

private const val EMOJI_PRODUCTO = "\uD83D\uDEE0\uFE0F"

@Composable
fun CarritoPantalla(
    carritoViewModel: CarritoViewModel,
    cantidadCarrito: Int,
    onNavegar: (String) -> Unit,
    onPedidoConfirmado: (String) -> Unit
) {
    val items by carritoViewModel.items.collectAsState()
    val tipoEntrega by carritoViewModel.tipoEntrega.collectAsState()
    val context = LocalContext.current

    var pasoActual by remember { mutableStateOf(PasoCarrito.LISTA) }

    val subtotal = carritoViewModel.calcularSubtotal(items)
    val recargo = carritoViewModel.calcularRecargo(tipoEntrega)
    val total = carritoViewModel.calcularTotal(items, tipoEntrega)

    when (pasoActual) {
        PasoCarrito.TERMINOS -> {
            PantallaTerminosCondiciones(
                onRegresar = { pasoActual = PasoCarrito.LISTA },
                onAceptar = { pasoActual = PasoCarrito.FORMULARIO }
            )
            return
        }

        PasoCarrito.FORMULARIO -> {
            PantallaFormularioCliente(
                carritoViewModel = carritoViewModel,
                items = items,
                tipoEntrega = tipoEntrega,
                total = total,
                onRegresar = { pasoActual = PasoCarrito.TERMINOS },
                onConfirmar = {
                    carritoViewModel.confirmarPedido(
                        onExito = { cedula ->
                            pasoActual = PasoCarrito.LISTA
                            Toast.makeText(context, "✅ Pedido registrado con éxito", Toast.LENGTH_LONG).show()
                            carritoViewModel.limpiarFormularioCliente()
                            onPedidoConfirmado(cedula)
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
            return
        }

        PasoCarrito.LISTA -> Unit
    }

    ClienteScaffold(
        pantallaActual = "carrito",
        cantidadCarrito = cantidadCarrito,
        onNavegar = onNavegar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FerreBlanco)
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FerreGrisOscuro)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mi Carrito",
                    color = FerreBlanco,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (items.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = FerreGrisOscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explora el catálogo y agrega productos",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onNavegar("catalogo") },
                        colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo)
                    ) {
                        Text("Ir al Catálogo", color = FerreGrisOscuro, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { pasoActual = PasoCarrito.TERMINOS },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = FerreVerde),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("✅ Confirmar Pedido", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FerreBlanco)
                    }
                    OutlinedButton(
                        onClick = { onNavegar("catalogo") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("➕ Añadir Productos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCarritoFila(
                            item = item,
                            onCambiarCantidad = { nueva ->
                                carritoViewModel.actualizarCantidad(item.id, nueva)
                            },
                            onEliminar = {
                                carritoViewModel.actualizarCantidad(item.id, 0)
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "TIPO DE ENTREGA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TipoEntregaChip(
                            titulo = "🏪 Retiro en Local",
                            subtitulo = "Sin cargo",
                            seleccionado = tipoEntrega == "LOCAL",
                            onClick = { carritoViewModel.cambiarTipoEntrega("LOCAL") },
                            modifier = Modifier.weight(1f)
                        )
                        TipoEntregaChip(
                            titulo = "🚚 Domicilio",
                            subtitulo = "+$${String.format("%.2f", CarritoViewModel.RECARGO_DOMICILIO)}",
                            seleccionado = tipoEntrega == "DOMICILIO",
                            onClick = { carritoViewModel.cambiarTipoEntrega("DOMICILIO") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FerreGrisOscuro)
                        .padding(16.dp)
                ) {
                    FilaResumenOscuro("Código de pedido", "—")
                    FilaResumenOscuro(
                        "Tipo de entrega",
                        if (tipoEntrega == "DOMICILIO") "Domicilio" else "Retiro en local"
                    )
                    FilaResumenOscuro("Subtotal", "$${String.format("%.2f", subtotal)}")
                    if (recargo > 0) {
                        FilaResumenOscuro("Recargo domicilio", "$${String.format("%.2f", recargo)}")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", color = FerreBlanco.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(
                            "$${String.format("%.2f", total)}",
                            color = FerreAmarillo,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCarritoFila(
    item: ItemCarrito,
    onCambiarCantidad: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FerreBlanco),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FerreGrisClaro),
                contentAlignment = Alignment.Center
            ) {
                if (item.urlImagen.isNotBlank()) {
                    AsyncImage(
                        model = item.urlImagen,
                        contentDescription = item.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = EMOJI_PRODUCTO, fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = FerreGrisOscuro,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.medidaVenta} · ${item.codigoProducto}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    "$${String.format("%.2f", item.precioUnitario)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = FerreGrisOscuro
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "✕",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onEliminar() }
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        "-",
                        modifier = Modifier
                            .clickable { onCambiarCantidad(item.cantidad - 1) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        item.cantidad.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Text(
                        "+",
                        modifier = Modifier
                            .clickable { onCambiarCantidad(item.cantidad + 1) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TipoEntregaChip(
    titulo: String,
    subtitulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (seleccionado) FerreAmarillo.copy(alpha = 0.25f) else Color(0xFFF8FAFC),
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (seleccionado) FerreAmarillo else Color(0xFFE2E8F0),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FerreGrisOscuro, textAlign = TextAlign.Center)
            Text(subtitulo, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun FilaResumenOscuro(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = FerreBlanco.copy(alpha = 0.6f), fontSize = 13.sp)
        Text(valor, color = FerreBlanco, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
