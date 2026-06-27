package com.example.ferrepaccha.interfaz

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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