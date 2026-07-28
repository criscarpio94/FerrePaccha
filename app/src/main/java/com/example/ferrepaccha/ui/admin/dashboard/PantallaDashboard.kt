package com.example.ferrepaccha.ui.admin.dashboard

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ferrepaccha.ui.admin.NotificacionPedido
import com.example.ferrepaccha.ui.admin.ResumenDiaAdmin
import com.example.ferrepaccha.ui.admin.TipoSubpantalla
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@Composable
fun PantallaDashboard(
    adminViewModel: AdminViewModel,
    onCerrarSesion: () -> Unit,
    onNavegarA: (TipoSubpantalla) -> Unit,
    onAbrirDetallePedido: (String) -> Unit
) {
    val resumen by adminViewModel.resumenDia.collectAsState()
    val notificaciones by adminViewModel.notificaciones.collectAsState()
    val pedidosRecientes = adminViewModel.pedidosRecientes24h()
    var menuNotifAbierto by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, ${adminViewModel.nombreAdministrador} 👋",
                        color = FerreAmarillo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PANEL DE ADMINISTRACION",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box {
                        IconButton(onClick = { menuNotifAbierto = true }) {
                            Text(text = "🔔", fontSize = 20.sp)
                        }
                        if (notificaciones.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 6.dp)
                                    .size(16.dp)
                                    .background(Color.Red, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = notificaciones.size.toString(),
                                    color = FerreBlanco,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = menuNotifAbierto,
                            onDismissRequest = { menuNotifAbierto = false }
                        ) {
                            if (notificaciones.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Sin notificaciones nuevas") },
                                    onClick = { menuNotifAbierto = false }
                                )
                            } else {
                                notificaciones.forEach { notif ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(notif.numeroPedido, fontWeight = FontWeight.Bold)
                                                Text(notif.mensaje, fontSize = 12.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            menuNotifAbierto = false
                                            adminViewModel.marcarNotificacionVista(notif.pedidoId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Text(text = "➜🚪", fontSize = 17.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "FERRETERIA PACCHA",
                color = FerreBlanco,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "RESUMEN DEL DIA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    titulo = "Pedidos Hoy",
                    valor = resumen.pedidosHoy.toString(),
                    emoji = "📦",
                    colorFondo = Color(0xFFEFF6FF),
                    colorTexto = Color(0xFF1D4ED8),
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Pendientes",
                    valor = resumen.pendientes.toString(),
                    emoji = "⏳",
                    colorFondo = Color(0xFFFEFCE8),
                    colorTexto = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    titulo = "Ingresos",
                    valor = "$${String.format("%.2f", resumen.ingresos)}",
                    emoji = "💰",
                    colorFondo = Color(0xFFECFDF5),
                    colorTexto = Color(0xFF15803D),
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Atendidos",
                    valor = resumen.completados.toString(),
                    emoji = "✅",
                    colorFondo = Color(0xFFF8FAFC),
                    colorTexto = Color(0xFF475569),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ACCIONES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccionPequena(
                    titulo = "Gestionar\nProductos",
                    emoji = "🔺",
                    colorFondo = FerreAmarillo,
                    onClick = { onNavegarA(TipoSubpantalla.GESTION_PRODUCTOS) },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAccionPequena(
                    titulo = "Atender\nPedidos",
                    emoji = "📦",
                    colorFondo = FerreGrisOscuro,
                    colorTexto = FerreBlanco,
                    onClick = {
                        adminViewModel.modoBusquedaPedidos = false
                        onNavegarA(TipoSubpantalla.GESTION_PEDIDOS)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccionPequena(
                    titulo = "Buscar\nPedidos",
                    emoji = "🔍",
                    colorFondo = Color(0xFFE0F2FE),
                    colorTexto = Color(0xFF0369A1),
                    onClick = {
                        adminViewModel.modoBusquedaPedidos = true
                        onNavegarA(TipoSubpantalla.BUSCAR_PEDIDOS)
                    },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAccionPequena(
                    titulo = "Ver\nCatálogo",
                    emoji = "📖",
                    colorFondo = Color(0xFFDCFCE7),
                    colorTexto = Color(0xFF15803D),
                    onClick = { onNavegarA(TipoSubpantalla.VER_CATALOGO) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (adminViewModel.rolUsuarioActual == "SOPORTE" || adminViewModel.rolUsuarioActual == "GERENTE") {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "HERRAMIENTAS DE GESTION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (adminViewModel.rolUsuarioActual == "SOPORTE") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TarjetaAccionPequena(
                            titulo = "Gestionar\nGerente",
                            emoji = "👑",
                            colorFondo = Color(0xFFFEF2F2),
                            onClick = {
                                adminViewModel.escucharUsuariosPorRol("GERENTE")
                                onNavegarA(TipoSubpantalla.GESTION_GERENTE)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaAccionPequena(
                            titulo = "Gestionar\nEmpleados",
                            emoji = "👥",
                            colorFondo = Color(0xFFEDF2F7),
                            onClick = {
                                adminViewModel.escucharUsuariosPorRol("EMPLEADO")
                                onNavegarA(TipoSubpantalla.GESTION_EMPLEADOS)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    TarjetaAccionPequena(
                        titulo = "Gestionar\nEmpleados",
                        emoji = "👥",
                        colorFondo = Color(0xFFEDF2F7),
                        onClick = {
                            adminViewModel.escucharUsuariosPorRol("EMPLEADO")
                            onNavegarA(TipoSubpantalla.GESTION_EMPLEADOS)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "PEDIDOS RECIENTES (24h)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (pedidosRecientes.isEmpty()) {
                Text("No hay pedidos en las últimas 24 horas", color = Color.Gray, fontSize = 13.sp)
            } else {
                pedidosRecientes.take(5).forEach { pedido ->
                    ItemPedido(
                        pedido = pedido,
                        onClick = { onAbrirDetallePedido(pedido.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TarjetaResumen(
    titulo: String,
    valor: String,
    emoji: String,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Column {
                Text(text = valor, color = colorTexto, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(text = titulo, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun TarjetaAccionPequena(
    titulo: String,
    emoji: String,
    colorFondo: Color,
    colorTexto: Color = FerreGrisOscuro,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = titulo,
                color = colorTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ItemPedido(
    pedido: PedidoFirebase,
    onClick: () -> Unit
) {
    val estado = try {
        EstadoPedido.valueOf(pedido.estado)
    } catch (_: Exception) {
        EstadoPedido.RECIBIDO
    }
    val colorEstado = when (estado) {
        EstadoPedido.RECIBIDO -> Color(0xFFBFDBFE)
        EstadoPedido.PREPARANDO -> FerreAmarillo
        EstadoPedido.LISTO -> Color(0xFFD1FAE5)
        EstadoPedido.ENTREGADO -> Color(0xFFE2E8F0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = pedido.numeroPedido,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = FerreGrisOscuro
                )
                Text(
                    text = "${pedido.nombresCliente} ${pedido.apellidosCliente}".trim(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = colorEstado)
                ) {
                    Text(
                        text = estado.etiqueta(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", pedido.total)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FerreGrisOscuro
                )
            }
        }
    }
}
