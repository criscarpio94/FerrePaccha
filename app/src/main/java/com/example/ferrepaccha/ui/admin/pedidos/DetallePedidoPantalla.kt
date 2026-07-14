package com.example.ferrepaccha.ui.admin.pedidos

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ui.admin.productos.TarjetaProductoItem
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@Composable
fun DetallePedidoPantalla(
    onRegresarClick: () -> Unit,
    onActualizarEstadoClick: () -> Unit,
    onExportarPdfClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onRegresarClick) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "PED-2024-001",
                color = FerreBlanco,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Preparando",
                    color = Color(0xFFD97706),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Juan Carlos Perez Lopez", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FerreGrisOscuro)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "CI/RUC: 1712345678", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FerreGrisOscuro)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "juan@email.com", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "0991245677", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Av. Principal 126, Paccha", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🚚 Entrega a domicilio  •  30-jun, 10:29 a.m.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PRODUCTOS DEL PEDIDO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            TarjetaProductoItem(
                nombre = "Martillo Carpintero 16oz",
                detalles = "Stanley · Unidad",
                cantidad = 2,
                precio = "$25.00"
            )

            Spacer(modifier = Modifier.height(10.dp))

            TarjetaProductoItem(
                nombre = "Pintura Latex Blanca",
                detalles = "Condor · Galón",
                cantidad = 1,
                precio = "$28.00"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FerreGrisOscuro)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "TOTAL DEL PEDIDO", color = FerreBlanco, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "$53.00", color = FerreAmarillo, fontWeight = FontWeight.Black, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onActualizarEstadoClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Actualizar estado → Pedido Listo", color = FerreBlanco, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onExportarPdfClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FerreGrisOscuro)
            ) {
                Text(text = "📄 Exportar como PDF", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun TarjetaProductoPedidoItem(nombre: String, detalles: String, cantidad: Int, precio: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📦", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FerreGrisOscuro)
                    Text(text = detalles, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Cant: $cantidad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }
            Text(text = precio, fontWeight = FontWeight.Black, fontSize = 16.sp, color = FerreGrisOscuro)
        }
    }
}

@Preview(showBackground = true, name = "Vista Detalle Pedido")
@Composable
fun PreviewDetallePedido() {
    DetallePedidoPantalla(onRegresarClick = {}, onActualizarEstadoClick = {}, onExportarPdfClick = {})
}
