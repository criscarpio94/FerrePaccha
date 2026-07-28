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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.ferrepaccha.util.EstadoPedidoUi
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PedidosPantalla(
    pedidoViewModel: PedidoViewModel,
    cantidadCarrito: Int,
    onNavegar: (String) -> Unit
) {
    val cedulaConsulta by pedidoViewModel.cedulaConsultaActiva.collectAsState()
    val pedidosRecientes by pedidoViewModel.pedidosRecientes.collectAsState()
    val pedidosBusqueda by pedidoViewModel.pedidosBusqueda.collectAsState()
    val estaBuscando by pedidoViewModel.estaBuscando.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var cedulaManual by remember { mutableStateOf("") }
    val sesionActiva = cedulaConsulta.isNotBlank()

    LaunchedEffect(Unit) {
        pedidoViewModel.reanudarSeguimientoSiActivo()
    }

    val pedidosMostrar = if (textoBusqueda.isBlank()) {
        pedidosRecientes
    } else {
        val consulta = textoBusqueda.trim()
        val coincidenciasLocales = pedidosRecientes.filter { pedido ->
            pedido.numeroPedido.contains(consulta, ignoreCase = true) ||
                pedido.cedulaRuc.contains(consulta, ignoreCase = true)
        }
        if (coincidenciasLocales.isNotEmpty()) coincidenciasLocales else pedidosBusqueda
    }

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Consulta con tu cédula o RUC",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FerreGrisOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tu identificación no se guarda en el dispositivo. " +
                        "Puedes cambiar de pantalla y el seguimiento sigue activo; " +
                        "se borra al minimizar la app o pulsar Limpiar.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = cedulaManual,
                        onValueChange = { cedulaManual = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = if (sesionActiva && cedulaManual.isBlank()) {
                                    "Consulta activa (oculta)"
                                } else {
                                    "Cédula / RUC"
                                },
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            if (cedulaManual.isNotBlank()) {
                                pedidoViewModel.iniciarSeguimientoPorCedula(cedulaManual)
                            }
                        },
                        enabled = cedulaManual.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Consultar", color = FerreGrisOscuro, fontWeight = FontWeight.Bold)
                    }
                }

                if (sesionActiva) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "● Seguimiento en vivo activo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF059669)
                        )
                        OutlinedButton(
                            onClick = {
                                cedulaManual = ""
                                textoBusqueda = ""
                                pedidoViewModel.limpiarSesionConsulta()
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Limpiar", fontSize = 12.sp)
                        }
                    }
                }
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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

            if (textoBusqueda.isBlank() && sesionActiva) {
                Text(
                    text = "Pedidos recientes (últimos 10 días)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            if (estaBuscando && pedidosMostrar.isEmpty()) {
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
                        text = if (!sesionActiva) {
                            "Ingresa tu cédula/RUC y pulsa Consultar para ver tus pedidos"
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
                    items(
                        items = pedidosMostrar,
                        key = { pedido -> "${pedido.id}_${pedido.estado}" }
                    ) { pedido ->
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
    val estado = EstadoPedidoUi.parsear(pedido.estado)
    val (colorFondoEstado, colorTextoEstado) = EstadoPedidoUi.colores(estado)

    val fechaTexto = pedido.fechaCreacion?.toDate()?.let {
        SimpleDateFormat("dd-MMM, hh:mm a", Locale("es", "EC")).format(it)
    } ?: "Sin fecha"

    val tipoEntrega = if (pedido.tipoEntrega == "DOMICILIO") "🚚 Domicilio" else "🏪 Retiro en local"
    val pasos = listOf(
        EstadoPedido.RECIBIDO to "Confirmado",
        EstadoPedido.PREPARANDO to "Preparando",
        EstadoPedido.LISTO to "Listo",
        EstadoPedido.ENTREGADO to "Entregado"
    )
    val indiceActual = pasos.indexOfFirst { it.first == estado }.coerceAtLeast(0)

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
                Column {
                    Text(pedido.numeroPedido, fontWeight = FontWeight.Black, fontSize = 14.sp, color = FerreGrisOscuro)
                    Text(fechaTexto, fontSize = 11.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .background(colorFondoEstado, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(estado.etiqueta(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorTextoEstado)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            pedido.items.take(3).forEach { item ->
                Text("${item.cantidad}x ${item.nombre}", fontSize = 13.sp, color = FerreGrisOscuro)
            }
            if (pedido.items.size > 3) {
                Text("+${pedido.items.size - 3} producto(s) más", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tipoEntrega, fontSize = 12.sp, color = Color.Gray)
                Text(
                    "$${String.format("%.2f", pedido.total)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = FerreGrisOscuro
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pasos.forEachIndexed { index, (estadoPaso, etiqueta) ->
                    val alcanzado = index <= indiceActual
                    val colorPaso = EstadoPedidoUi.colorPaso(estadoPaso, alcanzado)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(colorPaso, CircleShape)
                        )
                        Text(
                            etiqueta,
                            fontSize = 8.sp,
                            color = if (alcanzado) colorPaso else Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
