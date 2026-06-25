package com.example.ferrepaccha.ui

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ui.theme.FerreGrisClaro
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro


@Composable
fun PantallaInicio() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FerreGrisClaro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //Nombre principal: Ferreteria y carrito de compras
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ferretería Paccha e Hijos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FerreGrisOscuro
                )

                Text(
                    text = "\uD83D\uDED2 0", //icono de carrito de compras en Unicode
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FerreGrisOscuro
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Textos de presentacion y bienvenida.
            Text(
                text = "!Bienvenidos¡",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = FerreGrisOscuro
            )
            Text(
                text = "Materiales y Herramientas de Ferretería",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Categorias
            Text(
                text = "Categorías",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FerreGrisOscuro
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaInicioPeview() {
    PantallaInicio()
}