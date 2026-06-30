package com.example.ferrepaccha.interfaz

import android.R
import android.widget.QuickContactBadge
import android.widget.Space
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisClaro
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import kotlin.math.sin

//1.- Subpantalla: Advertencia
@Composable
fun ComponenteAdvertencia(
    onRegresar: () -> Unit,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Alerta en amarillo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFBEB)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚠\uFE0F", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ÁREA RESTRINGIDA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = FerreGrisOscuro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Este apartado es exclusivo para administradores y personal autorizado de la Ferretería Paccha e Hijos.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Si ud no es un administrador, por favor regrese al inicio.",
            fontSize = 14.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        //Fila de botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Boton regresar
            Button(
                onClick = onRegresar,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = FerreBlanco),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Regresar", color = FerreGrisOscuro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            //Boton Continuar
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreGrisOscuro),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Continuar", color = FerreBlanco, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

//2.- Subpantalla: Login
@Composable
fun ComponenteLogin(
    viewModel: AdminViewModel,
    onFlechaRegresar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        //Encabezado obscuro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFlechaRegresar) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acceso Administrador",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //Formulario cuerpo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Recuadro con escudo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(FerreGrisOscuro),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDEE1\uFE0F", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "INICIAR SESIÓN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = FerreGrisOscuro
            )

            Spacer(modifier = Modifier.height(32.dp))

            //Campo de Usuario
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "USUARIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.usuarioInput,
                    onValueChange = { viewModel.usuarioInput = it },
                    placeholder = { Text(text = "Usuario", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    enabled = !viewModel.estaBloqueado
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Campo de contraseña
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CONTRASEÑA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.contrasenaInput,
                    onValueChange = { viewModel.contrasenaInput = it },
                    placeholder = {  Text(text = "••••••••", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    enabled = !viewModel.estaBloqueado,
                    visualTransformation = if (viewModel.contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.contrasenaVisible = !viewModel.contrasenaVisible }) {
                            Text(text = if (viewModel.contrasenaVisible) "\uD83D\uDC41\uFE0F" else "\uD83D\uDE48", fontSize = 16.sp)
                        }
                    }
                )
            }

            //Alerta de error
            if (viewModel.mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = viewModel.mensajeError,
                    color = Color.Red,
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Boton de ingresar
            Button(
                onClick = { viewModel.procesarLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp),
                enabled = !viewModel.estaBloqueado
            ) {
                Text(
                    text = "Ingresar",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Datos de prueba

            Text(
                text = "DATOS DE PRUEBA: admin / admin123",
                fontSize = 12.sp,
                color = Color.LightGray,
                letterSpacing = 0.5.sp
            )
        }
    }
}

//3.- Subpantalla Dashboard principal
@Composable
fun ComponenteDashboard(
    viewModel: AdminViewModel
) {
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
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, ${viewModel.nombreAdministrador} 👋",
                        color = FerreAmarillo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PANEL DE ADMINISTRACION",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "FERRETERIA PACCHA",
                        color = FerreBlanco,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                //Iconos (notificacion y salida )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {/* mostrara notificaciones */}) {
                        Text(text = "\uD83D\uDD14", fontSize = 22.sp)
                    }
                    IconButton(onClick = { viewModel.cerrarSesion() }) {
                        Text(text = "➜\uD83D\uDEAA", fontSize = 17.sp, color = Color.White)
                    }
                }
            }
        }

        //Cuerpo de pantalla inicial administradores
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //Resumen del día
            Text(
                text = "RESUMEN DEL DIA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            //Cuadro de tarjetas resumen
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CardResumen(modifier = Modifier.weight(1f), icono = "\uD83D\uDCE6", valor = "2", etiqueta = "Pedidos Hoy", fondo = Color(0xFFEFF6FF), colorTexto = Color(0xFF2563EB))
                CardResumen(modifier = Modifier.weight(1f), icono = "⏳", valor = "2", etiqueta = "Pendientes", fondo = Color(0xFFFFFBEB), colorTexto = Color(0xFFD97706))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CardResumen(modifier = Modifier.weight(1f), icono = "\uD83D\uDCB0", valor = "$95.50", etiqueta = "INgresos", fondo = Color(0xFFF0FDF4), colorTexto = Color(0xFF16A34A))
                CardResumen(modifier = Modifier.weight(1f), icono = "✅", valor = "0", etiqueta = "Completados", fondo = Color(0xFFF8FAFC), colorTexto = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ACCIONES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //Boton para cargar nuevos productos
                ButtonAccionPrincipal(
                    modifier = Modifier.weight(1f),
                    titulo = "CARGAR\nPRODUCTO",
                    icono = "\uD83D\uDCE4",
                    esOscuro = false,
                    onClick = { viewModel.cambiarPantalla(SubPantallaAdmin.CARGAR_PRODUCTO) }
                )

                //Boton para cargar atender pedidos
                ButtonAccionPrincipal(
                    modifier = Modifier.weight(1f),
                    titulo = "ATENDER\nPEDIDOS",
                    icono = "\uD83D\uDCE6",
                    esOscuro = true,
                    badgeContador = "2",
                    onClick = { viewModel.cambiarPantalla(SubPantallaAdmin.GESTION_PEDIDOS) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Apartado de pedidos recientes
            Text(
                text = "PEDIDOS RECIENTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            //Listado de pedidos recientes
            CardPedidoReciente(codigo = "PED-2024-010", cliente = "Juan Carlos Perez Lopez", estado = "Preparando", total = "$53.00", colorEstado = FerreAmarillo, onClick = { viewModel.cambiarPantalla(SubPantallaAdmin.DETALLE_PEDIDO) })
            Spacer(modifier = Modifier.height(8.dp))
            CardPedidoReciente(codigo = "PED-2024-002", cliente = "Maria Elena Torres Ruiz", estado = "Confirmado", total = "$42.50", colorEstado = Color(0xFFBFDBFE), colorTextoEstado = Color(0xFF1E40AF), onClick = { viewModel.cambiarPantalla(SubPantallaAdmin.DETALLE_PEDIDO) })
        }
    }
}

//Componente de apoyo tarjetas resumen
@Composable
fun CardResumen(modifier: Modifier, icono: String, valor: String, etiqueta: String, fondo: Color, colorTexto: Color ) {
    Card(
        modifier = modifier.height(95.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = icono, fontSize = 18.sp)
            Column{
                Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Black, color = colorTexto)
                Text(text = etiqueta, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

//Componente de apoyo de botones princpaples para crear productos o atendere pedido
@Composable
fun ButtonAccionPrincipal(modifier: Modifier, titulo: String, icono: String, esOscuro: Boolean, badgeContador: String? = null, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = if (esOscuro) FerreGrisOscuro else FerreAmarillo),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            if (badgeContador != null) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(FerreAmarillo, RoundedCornerShape(11.dp))
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badgeContador, color = FerreGrisOscuro, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = icono, fontSize = 22.sp, color = if (esOscuro) FerreBlanco else FerreGrisOscuro)
                Text(text = titulo, fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (esOscuro) FerreBlanco else FerreGrisOscuro, lineHeight = 16.sp)
            }
        }
    }
}

//Componente para las filas de pedidos recientes
@Composable
fun CardPedidoReciente(codigo: String, cliente: String, estado: String, total: String, colorEstado: Color, colorTextoEstado: Color = FerreGrisOscuro, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column{
                Text(text = codigo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FerreGrisOscuro)
                Text(text = cliente, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.background(colorEstado, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = estado, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorTextoEstado)
                }
                Text(text = total, fontWeight = FontWeight.Black, fontSize = 14.sp, color = FerreGrisOscuro)
            }
        }
    }
}

//4.- Subpantalla Cargar producto
@Composable
fun ComponenteCargarProducto(
    viewModel: AdminViewModel,
    onFlechaRegresar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreBlanco)
    ) {
        //Encabezado negro y texto blanco
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .padding(horizontal = 4.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onFlechaRegresar,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "←",
                    color = FerreBlanco,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Cargar Producto",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(modifier = Modifier.height(50.dp))

    //Cuerpo de los campos del formulario
    androidx.compose.foundation.rememberScrollState().let { scrollState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)

        ) {
            //Recuadro para cargar fotografia
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(25.dp))
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "\uD83D\uDCE4", fontSize = 28.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Toca para subir fotografia", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    Text(text = "JPG, PNG - Máx. 5MB", fontSize = 11.sp, color = Color.LightGray)
                }
            }
             //Campo: nombre del producto
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "NOMBRE DEL PRODUCTO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.nombreProductoInput,
                    onValueChange = { viewModel.nombreProductoInput = it },
                    placeholder = { Text(text = "Ej: Martillo Carpintero 16oz", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }
             //Campos codigo y precio
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CÓDIGO *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.codigoProductoInput,
                        onValueChange = { viewModel.codigoProductoInput = it },
                        placeholder = { Text(text = "HERR-001", color = Color.LightGray) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRECIO ($) *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.precioProductoInput,
                        onValueChange = { viewModel.precioProductoInput = it },
                        placeholder = { Text(text = "0.00", color = Color.LightGray) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            //Campos: marca y medida
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "MARCA *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.marcaProductoInput,
                        onValueChange = { viewModel.marcaProductoInput = it },
                        placeholder = { Text(text = "Stanley", color = Color.LightGray) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "MEDIDA / UNIDAD *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.medidaProductoInput,
                        onValueChange = { viewModel.medidaProductoInput = it },
                        placeholder = { Text(text = "Unidad", color = Color.LightGray) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            //Campo: Categoria
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "CATEGORIA *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))

                //Contenedor desplegable
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.categoriaProductoInput,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        leadingIcon = {
                            val icono = when (viewModel.categoriaProductoInput) {
                                "Herramientas" -> "\uD83D\uDD27"
                                "Eléctrico" -> "⚡"
                                "Pintura" -> "🎨"
                                "Construcción" -> "🧱"
                                "Plomería" -> "🔩"
                                else -> "\uD83D\uDCE6"
                            }
                            Text(text = icono, fontSize = 16.sp)
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.menuCategoriasExpandido = true }) {
                                Text(text = if (viewModel.menuCategoriasExpandido) "▲" else "▼", fontSize = 12.sp, color = Color.Gray)
                            }
                        },

                        //Opcion clickeable para abrir el menu de opciones de categorias
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FerreAmarillo,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    //Parte transparente para poder identificar el click
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { viewModel.menuCategoriasExpandido = true }
                    )

                    //Menu nativo de compose
                    DropdownMenu(
                        expanded = viewModel.menuCategoriasExpandido,
                        onDismissRequest = { viewModel.menuCategoriasExpandido = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(FerreBlanco)
                    ) {
                        //Definicion de la lista exacta
                        val opciones = listOf(
                            "Herramientas" to "🔧",
                            "Eléctrico" to "⚡",
                            "Pintura" to "🎨",
                            "Construcción" to "🧱",
                            "Plomería" to "🔩"
                        )

                        opciones.forEach { (nombre, icono) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = icono, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = nombre, fontSize = 14.sp, color = FerreGrisOscuro)
                                    }
                                },
                                onClick = {
                                    viewModel.categoriaProductoInput = nombre
                                    viewModel.menuCategoriasExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            //Campo para descripcion del producto
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "DESCRIPCION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.descripcionProductoInput,
                    onValueChange = { viewModel.descripcionProductoInput = it },
                    placeholder = { Text(text = "Descripción detallada del producto...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            //Boton para guardar productos creados.
            Button(
                onClick = {
                    viewModel.limpiarFormularioProducto()
                    viewModel.cambiarPantalla(SubPantallaAdmin.DASHBOARD)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Guardar Producto al Catálogo",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewAdvertenciaIndividual() {
    com.example.ferrepaccha.ui.theme.FerrePacchaTheme {
        ComponenteAdvertencia(onRegresar = {}, onContinuar = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComponenteLogin() {
    com.example.ferrepaccha.ui.theme.FerrePacchaTheme {
        ComponenteLogin(onFlechaRegresar = {}, viewModel = AdminViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComponenteDashboard() {
    com.example.ferrepaccha.ui.theme.FerrePacchaTheme {
        ComponenteDashboard(viewModel = AdminViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComponenteCargarProducto() {
    com.example.ferrepaccha.ui.theme.FerrePacchaTheme {
        ComponenteCargarProducto(viewModel = AdminViewModel(), onFlechaRegresar = {})
    }
}