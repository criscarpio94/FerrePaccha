package com.example.ferrepaccha.admin

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.interfaz.FerreAmarillo
import com.example.ferrepaccha.interfaz.FerreBlanco
import com.example.ferrepaccha.interfaz.FerreGrisOscuro

@Composable
fun SubPantallaDashboard(
    nombreAdmin: String,
    rolUsuario: String,
    onCerrarSesion: () -> Unit,
    onNavegarA: (TipoSubpantalla) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        //Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column{
                    Text(text = "Hola, $nombreAdmin \uD83D\uDC4B", color = FerreAmarillo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "PANEL DE ADMINISTRACION", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                //Apartado para boton de notificacion y salir
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = {/*notificaciones*/}) {
                        Text(text = "\uD83D\uDD14", fontSize = 20.sp)
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Text(text = "➜\uD83D\uDEAA", fontSize = 17.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "FERRETERIA PACCHA", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        //Contenido con scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            //SECCION RESUMEN DEL DIA
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "RESUMEN DEL DIA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray,letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(10.dp))

            //GRID 4 TARJETAS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(titulo = "Pedidos Hoy", valor = "2", emoji = "\uD83D\uDCE6", colorFondo = Color(0xFFEFF6FF), colorTexto = Color(0xFF1D4ED8), modifier = Modifier.weight(1f))
                TarjetaResumen(titulo = "Pendientes", valor = "2", emoji = "⏳", colorFondo = Color(0xFFFEFCE8), colorTexto = Color(0xFFB45309), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(titulo = "Ingresos", valor = "$95.50", emoji = "\uD83D\uDCB0", colorFondo = Color(0xFF0FDF4), colorTexto = Color(0xFF15803D), modifier = Modifier.weight(1f))
                TarjetaResumen(titulo = "Completados", valor = "0", emoji = "✅", colorFondo = Color(0xFFF8FAFC), colorTexto = Color(0xFF475569), modifier = Modifier.weight(1f))
            }

            //SECCION PARA ACCIONES
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "ACCIONES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccionPequena(
                    titulo = "Gestionar\nProductos",
                    emoji = "\uD83D\uDD3A",
                    colorFondo = FerreAmarillo,
                    onClick = { onNavegarA(TipoSubpantalla.GESTION_PRODUCTOS) },
                    modifier = Modifier.weight(1f))
                TarjetaAccionPequena(
                    titulo = "Atender\nPedidos",
                    emoji = "\uD83D\uDCE6",
                    colorFondo = FerreGrisOscuro,
                    colorTexto = FerreBlanco,
                    onClick = { /*gestion de pedidos*/ },
                    modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccionPequena(
                    titulo = "Buscar\nPedidos",
                    emoji = "\uD83D\uDD0D",
                    colorFondo = Color(0xFFE0F2FE),
                    colorTexto = Color(0xFF0369A1),
                    onClick = {/*IR A BUSCAR*/},
                    modifier = Modifier.weight(1f))
                TarjetaAccionPequena(
                    titulo = "Ver\nCatálogo",
                    emoji = "\uD83D\uDCD6",
                    colorFondo = Color(0xFFDCFCE7),
                    colorTexto = Color(0xFF15803D),
                    onClick = { /*direccionar al catalogo de productos*/ },
                    modifier = Modifier.weight(1f))
            }

            //SECCION PARA ROL SOPORTE Y GERENTE
            if (rolUsuario == "SOPORTE" || rolUsuario == "GERENTE") {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "HERRAMIENTAS DE GESTION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                Spacer(modifier = Modifier.height(10.dp))

                if (rolUsuario == "SOPORTE") {
                    //Fila con 2 opciones para gestionar gerente y empleados
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TarjetaAccionPequena(
                            titulo = "Gestionar\nGerente",
                            emoji = "\uD83D\uDC51",
                            colorFondo = Color(0xFFFEF2F2),
                            onClick = { onNavegarA(TipoSubpantalla.GESTION_GERENTE) },
                            modifier = Modifier.weight(1f))
                        TarjetaAccionPequena(
                            titulo = "Gestionar\nEmpleados",
                            emoji = "\uD83D\uDC65",
                            colorFondo = Color(0xFFEDF2F7),
                            onClick = { onNavegarA(TipoSubpantalla.GESTION_EMPLEADOS) },
                            modifier = Modifier.weight(1f))
                    }
                } else {
                    //El gerente solo vera opcion de gestionar empleados
                    TarjetaAccionPequena(
                        titulo = "Gestionar\nEmpleados",
                        emoji = "\uD83D\uDC65",
                        colorFondo = Color(0xFFEDF2F7),
                        onClick = { onNavegarA(TipoSubpantalla.GESTION_EMPLEADOS) },
                        modifier = Modifier.fillMaxWidth())
                }
            }

            //SECCION PARA PEDIDOS RECIENTES
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "PEDIDOS RECIENTES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            //Pedidos traidos desde firebase - por ahora son datos de ejemplo
            ItemPedido(id = "PED-2024-001", cliente = "Juan Carlos Perez Lopez", total = "$53.00", estado = "Preparando", colorEstado = FerreAmarillo)
            Spacer(modifier = Modifier.height(1.dp))
            ItemPedido(id = "PED-2024-002", cliente = "Maria Elena Torres Ruiz", total = "$42.50", estado = "Confirmado", colorEstado = Color(0xFFBFDBFE))
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

//COMPONENTES PARA COMPLEMENTAR LA PRESENTACION
@Composable
fun TarjetaResumen(titulo: String, valor: String, emoji: String, colorFondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(95.dp),
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
fun TarjetaAccionPequena(titulo: String, emoji: String, colorFondo: Color, colorTexto: Color = FerreGrisOscuro, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier
            .height(105.dp),
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
            Text(text = titulo, color = colorTexto, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ItemPedido(id: String, cliente: String, total: String, estado: String, colorEstado: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FerreGrisOscuro)
                Text(text = cliente, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = colorEstado)) {
                   Text(text = estado, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = total, fontWeight = FontWeight.Black, fontSize = 14.sp, color = FerreGrisOscuro)
            }
        }
    }
}


@Preview(showBackground = true, name = "Vista como Soporte")
@Composable
fun PreviewDashBoardSoporte() {
    SubPantallaDashboard(nombreAdmin = "Christopher Soporte", rolUsuario = "SOPORTE", onCerrarSesion = {}, onNavegarA = {})
}

@Preview(showBackground = true, name = "Vista como Gerente")
@Composable
fun PreviewDashBoardGerente() {
    SubPantallaDashboard(nombreAdmin = "Hernan Paccha", rolUsuario = "GERENTE", onCerrarSesion = {}, onNavegarA = {})
}

@Preview(showBackground = true, name = "Vista como Empleado")
@Composable
fun PreviewDashBoardEmpleado() {
    SubPantallaDashboard(nombreAdmin = "Jhony Coronel", rolUsuario = "EMPLEADO", onCerrarSesion = {}, onNavegarA = {})
}
