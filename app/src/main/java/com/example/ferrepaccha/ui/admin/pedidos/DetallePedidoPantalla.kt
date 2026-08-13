package com.example.ferrepaccha.ui.admin.pedidos

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.util.FechaUtil

@Composable
fun DetallePedidoPantalla(
    adminViewModel: AdminViewModel,
    onRegresarClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val pedidos by adminViewModel.pedidos.collectAsState()
    val pedidoId = adminViewModel.pedidoSeleccionadoId

    val pedido = pedidos.find { it.id == pedidoId }
        ?: pedidos.find { it.numeroPedido == pedidoId }

    if (pedido == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pedido no encontrado")
        }
        return
    }

    val estado = try {
        EstadoPedido.valueOf(pedido.estado)
    } catch (_: Exception) {
        EstadoPedido.RECIBIDO
    }
    val (colorFondoEstado, colorTextoEstado) = adminViewModel.coloresEstadoPedido(pedido)
    val colorBoton = adminViewModel.colorBotonEstadoPedido(pedido)
    val fechaTexto = pedido.fechaCreacion?.toDate()?.let { FechaUtil.formatearCorto(it) }.orEmpty()
    val tipoEntregaTexto = if (pedido.tipoEntrega == "DOMICILIO") {
        "🚚 Entrega a domicilio"
    } else {
        "🏪 Retiro en local"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onRegresarClick) {
                Text(text = "←", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = pedido.numeroPedido,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Surface(color = colorFondoEstado, shape = RoundedCornerShape(10.dp)) {
                Text(
                    text = estado.etiqueta(),
                    color = colorTextoEstado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "DATOS DEL CLIENTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEBFD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${pedido.nombresCliente} ${pedido.apellidosCliente}".trim(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("CI/RUC: ${pedido.cedulaRuc}")
                    Text(pedido.correoCliente, color = Color.Gray)
                    Text(pedido.telefonoCliente, color = Color.Gray)
                    Text(pedido.direccionEntrega, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$tipoEntregaTexto  •  $fechaTexto", fontSize = 13.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "PRODUCTOS DEL PEDIDO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            pedido.items.forEach { item ->
                TarjetaProductoPedidoItem(
                    nombre = item.nombre,
                    detalles = "${item.codigoProducto} · ${item.medidaVenta}",
                    cantidad = item.cantidad,
                    precio = "$${String.format("%.2f", item.subtotal)}"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL DEL PEDIDO", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "$${String.format("%.2f", pedido.total)}",
                        color = Color(0xFFFACC15),
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (estado != EstadoPedido.ENTREGADO) {
                Button(
                    onClick = {
                        adminViewModel.avanzarEstadoPedido(context) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = adminViewModel.textoBotonEstadoPedido(pedido),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    adminViewModel.exportarPedidoPdf(context) { result ->
                        result.onSuccess { file ->
                            try {
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    if (context !is Activity) {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "No se pudo abrir el PDF: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }.onFailure { error ->
                            Toast.makeText(
                                context,
                                "Error al exportar PDF: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9CBFF5)
                ),
                border = BorderStroke(1.dp, Color(0xFF4B87EF))
            ) {
                Text("📄 Exportar como PDF", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun TarjetaProductoPedidoItem(nombre: String, detalles: String, cantidad: Int, precio: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD8FFD8))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = detalles, fontSize = 12.sp, color = Color.Gray)
                Text(text = "x$cantidad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Text(text = precio, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }
}
