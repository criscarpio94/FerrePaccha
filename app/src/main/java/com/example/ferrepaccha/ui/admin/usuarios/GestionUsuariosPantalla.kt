package com.example.ferrepaccha.ui.admin.usuarios

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ferrepaccha.data.model.UsuarioFirebase
import com.example.ferrepaccha.ui.admin.AdminViewModel
import com.example.ferrepaccha.ui.theme.FerreAmarillo
import com.example.ferrepaccha.util.nombreMostrable
import com.example.ferrepaccha.ui.theme.FerreBlanco
import com.example.ferrepaccha.ui.theme.FerreGrisOscuro

@Composable
fun GestionUsuariosPantalla(
    tituloModulo: String,
    rolFiltro: String,
    adminViewModel: AdminViewModel,
    onAgregarUsuarioClick: () -> Unit,
    onRegresarClick: () -> Unit
) {
    val usuarios by adminViewModel.usuarios.collectAsState()
    val context = LocalContext.current
    var usuarioAEliminar by remember { mutableStateOf<UsuarioFirebase?>(null) }

    LaunchedEffect(rolFiltro) {
        adminViewModel.escucharUsuariosPorRol(rolFiltro)
    }

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
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresarClick) {
                Text(text = "←", color = FerreBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = tituloModulo, color = FerreBlanco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(
                onClick = onAgregarUsuarioClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FerreAmarillo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("➕ AGREGAR NUEVO", color = FerreGrisOscuro, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("PERSONAL REGISTRADO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            if (usuarios.isEmpty()) {
                Text("No hay usuarios registrados", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(usuarios, key = { it.uid }) { usuario ->
                        TarjetaUsuarioItem(
                            usuario = usuario,
                            onEditar = {
                                adminViewModel.cargarUsuarioParaEdicion(usuario)
                            },
                            onEliminar = { usuarioAEliminar = usuario }
                        )
                    }
                }
            }
        }
    }

    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Está seguro de eliminar a ${usuario.nombreMostrable()}?") },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.eliminarUsuario(usuario.uid) { ok ->
                        Toast.makeText(
                            context,
                            if (ok) "Usuario eliminado de Firestore" else "Error al eliminar",
                            Toast.LENGTH_SHORT
                        ).show()
                        usuarioAEliminar = null
                    }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun TarjetaUsuarioItem(
    usuario: UsuarioFirebase,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombreMostrable(), fontWeight = FontWeight.Bold, color = FerreGrisOscuro)
                Text(usuario.correo, fontSize = 12.sp, color = Color.Gray)
                Text("Rol: ${usuario.rol}", fontSize = 11.sp, color = Color.Gray)
            }
            Column {
                TextButton(onClick = onEditar) { Text("✏️ Editar", fontSize = 12.sp) }
                TextButton(onClick = onEliminar) { Text("🗑️ Eliminar", fontSize = 12.sp, color = Color.Red) }
            }
        }
    }
}
