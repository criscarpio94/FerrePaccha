package com.example.ferrepaccha.ui.cliente

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PedidosPantalla(
    pedidoViewModel: PedidoViewModel,
    cantidadCarrito: Int,
    cedulaInicial: String = "",
    onNavegar: (String) -> Unit
) {
    val ultimaCedula by pedidoViewModel.ultimaCedula.collectAsState()
    val pedidosRecientes by pedidoViewModel.pedidosRecientes.collectAsState()
    val pedidosBusqueda by pedidoViewModel.pedidosBusqueda.collectAsState()
    val estaBuscando by pedidoViewModel.estaBuscando.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    val cedulaActiva = cedulaInicial.ifBlank { ultimaCedula }

    LaunchedEffect(cedulaActiva) {
        if (cedulaActiva.isNotBlank()) {
            pedidoViewModel.escucharPedidosRecientes(cedulaActiva)
        }
    }

    val pedidosMostrar = if (textoBusqueda.isNotBlank()) pedidosBusqueda else pedidosRecientes

    ClienteScaffold(
        pantallaActual = "pedidos",
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
                    text = "Mis Pedidos",
                    color = FerreBlanco,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = {
                    textoBusqueda = it
                    if (it.isBlank()) {
                        pedidoViewModel.limpiarBusqueda()
                    } else if (it.length >= 3) {
                        pedidoViewModel.buscarPedidos(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Buscar por cédula/RUC o N° pedido...", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = FerreAmarillo,
                    unfocusedIndicatorColor = Color(0xFFCBD5E1)
                )
            )

            if (textoBusqueda.isBlank()) {
                Text(
                    text = "Pedidos recientes (últimos 10 días)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            if (estaBuscando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FerreAmarillo)
                }
            } else if (pedidosMostrar.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📋", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (cedulaActiva.isBlank()) {
                            "Realiza un pedido para ver el seguimiento aquí"
                        } else {
                            "No hay pedidos para mostrar"
                        },
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pedidosMostrar, key = { it.id }) { pedido ->
                        TarjetaPedidoCliente(pedido = pedido)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPedidoCliente(pedido: PedidoFirebase) {
    val estado = try {
        EstadoPedido.valueOf(pedido.estado)
    } catch (e: Exception) {
        EstadoPedido.RECIBIDO
    }

    val colorEstado = when (estado) {
        EstadoPedido.RECIBIDO -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        EstadoPedido.PREPARANDO -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        EstadoPedido.LISTO -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        EstadoPedido.ENTREGADO -> Color(0xFFE2E8F0) to Color(0xFF475569)
    }

    val fechaTexto = pedido.fechaCreacion?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
    } ?: "Sin fecha"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FerreBlanco),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pedido.numeroPedido, fontWeight = FontWeight.Black, fontSize = 14.sp, color = FerreGrisOscuro)
                Box(
                    modifier = Modifier
                        .background(colorEstado.first, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(estado.etiqueta(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorEstado.second)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(fechaTexto, fontSize = 11.sp, color = Color.Gray)
            Text(
                "${pedido.nombresCliente} ${pedido.apellidosCliente}".trim(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text("Entrega: ${pedido.tipoEntrega}", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total: $${String.format("%.2f", pedido.total)}",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = FerreGrisOscuro
            )
            Text(
                "${pedido.items.size} producto(s)",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
