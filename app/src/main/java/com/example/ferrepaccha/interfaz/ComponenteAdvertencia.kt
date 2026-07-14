package com.example.ferrepaccha.interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Subpantalla: Advertencia
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
            text = "Si ustedd no es un administrador, por favor regrese al inicio.",
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

@Preview(showBackground = true)
@Composable
fun PreviewAdvertenciaIndividual() {
    com.example.ferrepaccha.ui.theme.FerrePacchaTheme {
        ComponenteAdvertencia(onRegresar = {}, onContinuar = {})
    }
}