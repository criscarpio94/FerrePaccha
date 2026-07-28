package com.example.ferrepaccha.ui.admin.usuarios

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioUsuarioPantalla(
    adminViewModel: AdminViewModel,
    onRegresarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val esEdicion = adminViewModel.usuarioEnEdicionId.isNotBlank()
    var menuRolExpandido by remember { mutableStateOf(false) }
    val roles = adminViewModel.rolesDisponiblesEnFormulario()
    val puedeCambiarRol = roles.size > 1 || !esEdicion

    Column(modifier = Modifier.fillMaxSize().background(FerreBlanco)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FerreGrisOscuro)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresarClick) {
                Text("←", color = FerreBlanco, fontSize = 24.sp)
            }
            Text(
                text = if (esEdicion) "Editar Personal" else "Registrar Personal",
                color = FerreBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                value = adminViewModel.nombreUsuarioInput,
                onValueChange = { adminViewModel.nombreUsuarioInput = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = adminViewModel.correoUsuarioInput,
                onValueChange = { adminViewModel.correoUsuarioInput = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = adminViewModel.contrasenaUsuarioInput,
                onValueChange = { adminViewModel.contrasenaUsuarioInput = it },
                label = {
                    Text(
                        if (esEdicion) "Nueva contraseña (opcional)" else "Contraseña"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp)
            )
            if (esEdicion) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Deje vacío para mantener la contraseña actual",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = menuRolExpandido && puedeCambiarRol,
                onExpandedChange = { if (puedeCambiarRol) menuRolExpandido = !menuRolExpandido }
            ) {
                OutlinedTextField(
                    value = adminViewModel.rolUsuarioFormulario,
                    onValueChange = {},
                    readOnly = true,
                    enabled = puedeCambiarRol,
                    label = { Text("Rol") },
                    trailingIcon = {
                        if (puedeCambiarRol) {
                            ExposedDropdownMenuDefaults.TrailingIcon(menuRolExpandido)
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuRolExpandido && puedeCambiarRol,
                    onDismissRequest = { menuRolExpandido = false }
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol) },
                            onClick = {
                                adminViewModel.rolUsuarioFormulario = rol
                                menuRolExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    adminViewModel.guardarUsuario { ok, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (ok) onRegresarClick()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (esEdicion) "💾 GUARDAR CAMBIOS" else "💾 GUARDAR USUARIO",
                    color = FerreGrisOscuro,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
