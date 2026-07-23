package com.example.ferrepaccha.ui.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.data.model.ItemCarrito
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import com.example.ferrepaccha.ui.theme.FerreVerde

private val VerdeOscuro = Color(0xFF15803D)
private val VerdeClaro = Color(0xFFD1FAE5)

@Composable
fun EncabezadoCheckout(
    titulo: String,
    onRegresar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FerreGrisOscuro)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = FerreBlanco,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onRegresar() }
                .padding(end = 8.dp)
        )
        Text(
            text = titulo,
            color = FerreBlanco,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PantallaTerminosCondiciones(
    onRegresar: () -> Unit,
    onAceptar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        EncabezadoCheckout(titulo = "Confirmar Pedido", onRegresar = onRegresar)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(VerdeClaro),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TÉRMINOS Y CONDICIONES",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = FerreGrisOscuro,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Para procesar tu pedido, necesitamos recopilar tus datos personales. Esta información se usará exclusivamente para gestionar tu compra y generar tu factura.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Al continuar, aceptas el uso de tus datos conforme a la ")
                    withStyle(SpanStyle(color = VerdeOscuro, fontWeight = FontWeight.Bold)) {
                        append("Ley Orgánica de Protección de Datos Personales del Ecuador.")
                    }
                },
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Los datos se almacenarán de forma segura para agilizar futuras compras.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRegresar,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Regresar", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onAceptar,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Aceptar y Continuar", color = FerreBlanco, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun PantallaFormularioCliente(
    carritoViewModel: CarritoViewModel,
    items: List<ItemCarrito>,
    tipoEntrega: String,
    total: Double,
    onRegresar: () -> Unit,
    onConfirmar: () -> Unit
) {
    val cedula by carritoViewModel.cedulaRucInput.collectAsState()
    val nombres by carritoViewModel.nombresInput.collectAsState()
    val apellidos by carritoViewModel.apellidosInput.collectAsState()
    val direccion by carritoViewModel.direccionInput.collectAsState()
    val correo by carritoViewModel.correoInput.collectAsState()
    val telefono by carritoViewModel.telefonoInput.collectAsState()
    val esEmpresa by carritoViewModel.esEmpresaInput.collectAsState()

    val campoColores = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF8FAFC),
        focusedBorderColor = Color(0xFFE2E8F0),
        unfocusedBorderColor = Color(0xFFE2E8F0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        EncabezadoCheckout(titulo = "Confirmar Pedido", onRegresar = onRegresar)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "DATOS PARA FACTURA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("CÉDULA O RUC *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            OutlinedTextField(
                value = cedula,
                onValueChange = {
                    if (it.length <= 13) {
                        carritoViewModel.cedulaRucInput.value = it
                        if (it.length == 10 || it.length == 13) {
                            carritoViewModel.buscarClientePorCedula(it)
                        }
                    }
                },
                placeholder = { Text("0000000000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = campoColores
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("NOMBRES *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = { carritoViewModel.nombresInput.value = it },
                        placeholder = { Text("Juan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = campoColores
                    )
                }
                if (!esEmpresa) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("APELLIDOS *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = { carritoViewModel.apellidosInput.value = it },
                            placeholder = { Text("Pérez") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = campoColores
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("DIRECCIÓN *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            OutlinedTextField(
                value = direccion,
                onValueChange = { carritoViewModel.direccionInput.value = it },
                placeholder = { Text("Av. Principal 123") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = campoColores
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("CORREO ELECTRÓNICO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            OutlinedTextField(
                value = correo,
                onValueChange = { carritoViewModel.correoInput.value = it },
                placeholder = { Text("correo@ejemplo.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = campoColores
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("TELÉFONO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            OutlinedTextField(
                value = telefono,
                onValueChange = { carritoViewModel.telefonoInput.value = it },
                placeholder = { Text("0991234567") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = campoColores
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "RESUMEN DEL PEDIDO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.cantidad}x ${item.nombre}",
                            fontSize = 13.sp,
                            color = FerreGrisOscuro,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format("%.2f", item.subtotal)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE2E8F0))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text(
                        "$${String.format("%.2f", total)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = VerdeOscuro
                    )
                }
                Text(
                    text = "Entrega: ${if (tipoEntrega == "DOMICILIO") "Domicilio" else "Retiro en local"}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = onConfirmar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Enviar y Confirmar Pedido", color = FerreBlanco, fontWeight = FontWeight.Black)
        }
    }
}
