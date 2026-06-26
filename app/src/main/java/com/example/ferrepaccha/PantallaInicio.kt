package com.example.ferrepaccha.ui

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisClaro
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro
import com.example.ferrepaccha.ui.theme.FerreVerde


@Composable
fun PantallaInicio() {
    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(pantallaActual = "inicio")
        }
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FerreBlanco)
                .padding(paddingValores)
        ) {
            //Encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FerreGrisOscuro)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column{
                        Text(
                            text = "BIENVENIDO A",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FerreAmarillo,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "FERRETERIA",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = FerreBlanco
                        )
                        Text(
                            text = "PACCHA E HIJOS",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = FerreAmarillo
                        )
                    }

                    //Carrito de compras con cantidad
                    Box(contentAlignment = Alignment.TopEnd) {
                        Text(
                            text = "\uD83D\uDED2",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 6.dp, top = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(FerreAmarillo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "0",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FerreGrisOscuro
                            )
                        }
                    }
                }
            }

            //Baner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF334155)) //Deberia ir una imagen de fondo pero por lo pronto se coloca un color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Materiales y Herramientas\nde Ferretería",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FerreAmarillo,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Todo para tu obra o proyecto",
                        fontSize = 13.sp,
                        color = FerreBlanco
                    )
                }
            }
            //Contenido para categorias y acceso rapido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //Categorias
                Text(
                    text = "CATEGORIAS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val listaCategorias = listOf(
                    Pair("🔧", "Herramientas"),
                    Pair("⚡", "Eléctrico"),
                    Pair("🎨", "Pintura"),
                    Pair("🧱", "Construcción"),
                    Pair("🛠️", "Plomería")
                )

                //Columnas para categorias
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(150.dp)
                ) {
                    items(listaCategorias) { (icono, nombre) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = icono, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = nombre,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = FerreGrisOscuro
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                //Acceso rapido
                Text(
                    text = "ACCESO RAPIDO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    //Tarjeta de catalogo
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable { },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = FerreAmarillo)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "\uD83D\uDCD6", fontSize = 24.sp)
                            Column{
                                Text(
                                    text = "VER CATALOGO",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FerreGrisOscuro
                                )
                                Text(
                                    text = "Todos nuestros productos",
                                    fontSize = 11.sp,
                                    color = FerreGrisOscuro.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    //Tarjeta de Mis Pedidos
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable{ },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = FerreVerde)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "\uD83D\uDCCB", fontSize = 24.sp)
                            Column{
                                Text(
                                    text = "MIS PEDIDOS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FerreBlanco
                                )
                                Text(
                                    text = "Seguimiento en tiempo real",
                                    fontSize = 11.sp,
                                    color = FerreBlanco.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun BarraNavegacionInferior(pantallaActual: String) {
    // 4 Botones para navegacion
    val items = listOf(
        Triple("Inicio", "inicio", "🏠"),
        Triple("Catálogo", "catalogo", "📖"),
        Triple("Carrito", "carrito", "🛒"),
        Triple("Pedidos", "pedidos", "📋")
    )

    NavigationBar(
        containerColor = FerreBlanco, //Fondo
        tonalElevation = 16.dp
    ) {
        items.forEach { (nombre, ruta, icono) ->
            val seleccionada = pantallaActual == ruta

            NavigationBarItem(
                selected = seleccionada,
                onClick = { /*pendiente cofigurar cambio de color*/ },
                icon = {
                    Text(text = icono, fontSize = 22.sp)
                },
                label = {
                    Text(
                        text = nombre,
                        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionada) FerreAmarillo else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaInicioPeview() {
    PantallaInicio()
}