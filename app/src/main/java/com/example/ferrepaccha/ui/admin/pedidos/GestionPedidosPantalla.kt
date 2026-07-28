package com.example.ferrepaccha.ui.admin.pedidos

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.admin.TipoSubpantalla
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import com.example.ferrepaccha.util.FechaUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponenteGestionPedidos(
    viewModel: AdminViewModel,
    onFlechaRegresar: () -> Unit
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val pendientes = viewModel.pedidosPendientesFiltrados()
    val todos = viewModel.todosPedidosFiltrados()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        EncabezadoBusquedaPedidos(
            viewModel = viewModel,
            titulo = "Gestión de Pedidos",
            onFlechaRegresar = onFlechaRegresar,
            onAbrirCalendario = { mostrarCalendario = true }
        )

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⏳ PENDIENTES (${pendientes.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD97706)
            )

            if (pendientes.isEmpty()) {
                Text("No hay pedidos pendientes", color = Color.Gray, fontSize = 13.sp)
            } else {
                pendientes.forEach { pedido ->
                    CardPedidoAdmin(
                        pedido = pedido,
                        onClick = {
                            viewModel.abrirDetallePedido(pedido.id, TipoSubpantalla.GESTION_PEDIDOS)
                        }
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray)
            Text(
                text = "TODOS LOS PEDIDOS (30 días)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            todos.forEach { pedido ->
                CardPedidoAdmin(
                    pedido = pedido,
                    compacto = true,
                    onClick = {
                        viewModel.abrirDetallePedido(pedido.id, TipoSubpantalla.GESTION_PEDIDOS)
                    }
                )
            }
        }
    }

    if (mostrarCalendario) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.filtroFechaMillis = millis
                        viewModel.filtroFechaInput = FechaUtil.formatear(Date(millis), "dd/MM/yyyy")
                    }
                    mostrarCalendario = false
                }) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.filtroFechaMillis = null
                    viewModel.filtroFechaInput = ""
                    mostrarCalendario = false
                }) { Text("Limpiar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponenteBuscarPedidos(
    viewModel: AdminViewModel,
    onFlechaRegresar: () -> Unit
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val resultados = viewModel.todosPedidosFiltrados()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        EncabezadoBusquedaPedidos(
            viewModel = viewModel,
            titulo = "Buscar Pedidos",
            onFlechaRegresar = onFlechaRegresar,
            onAbrirCalendario = { mostrarCalendario = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (resultados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin resultados. Use número de pedido, nombre o fecha.", color = Color.Gray)
                }
            } else {
                resultados.forEach { pedido ->
                    CardPedidoAdmin(
                        pedido = pedido,
                        onClick = {
                            viewModel.abrirDetallePedido(pedido.id, TipoSubpantalla.BUSCAR_PEDIDOS)
                        }
                    )
                }
            }
        }
    }

    if (mostrarCalendario) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.filtroFechaMillis = millis
                        viewModel.filtroFechaInput = FechaUtil.formatear(Date(millis), "dd/MM/yyyy")
                    }
                    mostrarCalendario = false
                }) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun EncabezadoBusquedaPedidos(
    viewModel: AdminViewModel,
    titulo: String,
    onFlechaRegresar: () -> Unit,
    onAbrirCalendario: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FerreGrisOscuro)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onFlechaRegresar, modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = titulo, color = FerreBlanco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.busquedaPedidoInput,
                onValueChange = { viewModel.busquedaPedidoInput = it },
                placeholder = { Text("🔍 Buscar pedido o cliente...", fontSize = 13.sp, color = Color.Gray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FerreBlanco,
                    unfocusedContainerColor = FerreBlanco,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .clickable { onAbrirCalendario() }
            ) {
                OutlinedTextField(
                    value = viewModel.filtroFechaInput,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Fecha", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = FerreBlanco,
                        disabledBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun CardPedidoAdmin(
    pedido: PedidoFirebase,
    compacto: Boolean = false,
    onClick: () -> Unit
) {
    val estado = try {
        EstadoPedido.valueOf(pedido.estado)
    } catch (_: Exception) {
        EstadoPedido.RECIBIDO
    }
    val colorEstado = when (estado) {
        EstadoPedido.RECIBIDO -> Color(0xFFBFDBFE) to Color(0xFF1E40AF)
        EstadoPedido.PREPARANDO -> FerreAmarillo to Color.Black
        EstadoPedido.LISTO -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        EstadoPedido.ENTREGADO -> Color(0xFFE2E8F0) to Color(0xFF475569)
    }
    val fecha = pedido.fechaCreacion?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "EC")).format(it)
    }.orEmpty()
    val tipoEntrega = if (pedido.tipoEntrega == "DOMICILIO") "🚚 Domicilio" else "🏪 Local"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (compacto) Color(0xFFF9F9F9) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (compacto) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = pedido.numeroPedido, fontWeight = FontWeight.Bold)
                Text(
                    text = estado.etiqueta(),
                    color = colorEstado.second,
                    modifier = Modifier
                        .background(colorEstado.first, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (!compacto) Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${pedido.nombresCliente} ${pedido.apellidosCliente}".trim(),
                fontSize = if (compacto) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${pedido.items.size} producto(s) • $tipoEntrega${if (fecha.isNotBlank()) " • $fecha" else ""}",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Text(
                text = "Total: $${String.format("%.2f", pedido.total)}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD97706)
            )
        }
    }
}
