package com.example.ferrepaccha.admin.pedidos

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.admin.TipoSubpantalla
import com.example.ferrepaccha.interfaz.AdminViewModel
import com.example.ferrepaccha.interfaz.FerreAmarillo
import com.example.ferrepaccha.interfaz.FerreBlanco
import com.example.ferrepaccha.interfaz.FerreGrisOscuro

@Composable
fun ComponenteGestionPedidos(
    viewModel: AdminViewModel,
    onFlechaRegresar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        // Encabezado Oscuro con Protección de Barra de Estado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars) // 🛡️ Protege la hora
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onFlechaRegresar,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Gestión de Pedidos",
                    color = FerreBlanco,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Fila para buscar pedidos y filtrar por fecha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Buscar pedido
                OutlinedTextField(
                    value = viewModel.busquedaPedidoInput,
                    onValueChange = { viewModel.busquedaPedidoInput = it },
                    placeholder = { Text(text = "🔍 Buscar pedido...", fontSize = 13.sp, color = Color.Gray) },
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

                // Filtro fecha
                OutlinedTextField(
                    value = viewModel.filtroFechaInput,
                    onValueChange = { viewModel.filtroFechaInput = it },
                    placeholder = { Text(text = "dd/mm/aaaa", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.width(125.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FerreBlanco,
                        unfocusedContainerColor = FerreBlanco,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        // Cuerpo con Scroll para la Lista de Pedidos
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Pendientes
            Text(
                text = "⏳ PENDIENTES (2)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD97706),
                letterSpacing = 0.5.sp
            )

            // Pedido 1 - Pendiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiarPantalla(TipoSubpantalla.DETALLE_PEDIDO) } // 🎯 Navegación real
            ) {
                CardPedidoPendienteReal(
                    codigo = "PED-2024-001",
                    cliente = "Juan Carlos Perez Lopez",
                    productos = "2 productos(s)",
                    tipoEntrega = "🚚 Domicilio",
                    total = "$53.00",
                    estado = "Preparando",
                    colorEstado = FerreAmarillo,
                )
            }

            // Pedido 2 - Pendiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiarPantalla(TipoSubpantalla.DETALLE_PEDIDO) }
            ) {
                CardPedidoPendienteReal(
                    codigo = "PED-2024-002",
                    cliente = "Maria Elena Torres Ruiz",
                    productos = "1 producto(s)",
                    tipoEntrega = "🏪 Local",
                    total = "$42.50",
                    estado = "Confirmado",
                    colorEstado = Color(0xFFBFDBFE),
                    colorTextoEstado = Color(0xFF1E40AF)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

            // Historial General
            Text(
                text = "TODOS LOS PEDIDOS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            CardPedidoHistoriaReal(
                codigo = "PED-2026-001",
                cliente = "Juan Carlos Perez Lopez",
                fecha = "29 jun 2026",
                estado = "Preparando",
                total = "$53.00",
                colorEstado = FerreAmarillo,
            )

            CardPedidoHistoriaReal(
                codigo = "PED-2026-002",
                cliente = "Maria Elena Torres Ruiz",
                fecha = "29 jun 2026",
                estado = "Confirmado",
                total = "$42.50",
                colorEstado = Color(0xFFBFDBFE),
                colorTextoEstado = Color(0xFF1E40AF)
            )
        }
    }
}

@Composable
fun CardPedidoPendienteReal(
    codigo: String,
    cliente: String,
    productos: String,
    tipoEntrega: String,
    total: String,
    estado: String,
    colorEstado: Color,
    colorTextoEstado: Color = Color.Black
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = codigo, fontWeight = FontWeight.Bold)
                Text(
                    text = estado,
                    color = colorTextoEstado,
                    modifier = Modifier
                        .background(colorEstado, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = cliente, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$productos • $tipoEntrega", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Total: $total", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
        }
    }
}

@Composable
fun CardPedidoHistoriaReal(
    codigo: String,
    cliente: String,
    fecha: String,
    estado: String,
    total: String,
    colorEstado: Color,
    colorTextoEstado: Color = Color.Black
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = codigo, fontWeight = FontWeight.Bold)
                Text(text = cliente, fontSize = 14.sp)
                Text(text = fecha, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = total, fontWeight = FontWeight.Bold)
                Text(
                    text = estado,
                    fontSize = 12.sp,
                    color = colorTextoEstado,
                    modifier = Modifier
                        .background(colorEstado.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
