package com.example.ferrepaccha.ui.admin

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var pantallaActual by mutableStateOf(TipoSubpantalla.ADVERTENCIA)
    var nombreAdministrador by mutableStateOf("")
    var rolUsuarioActual by mutableStateOf("")

    var usuarioInput by mutableStateOf("")
    var contrasenaInput by mutableStateOf("")
    var contrasenaVisible by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    var nombreProductoInput by mutableStateOf("")
    var codigoProductoInput by mutableStateOf("")
    var precioProductoInput by mutableStateOf("")
    var marcaProductoInput by mutableStateOf("")
    var medidaProductoInput by mutableStateOf("")
    var categoriaProductoInput by mutableStateOf("")
    var menuCategoriasExpandido by mutableStateOf(false)
    var descripcionProductoInput by mutableStateOf("")
    var porcentajeIvaInput by mutableStateOf(15.0)
    var tieneSubMedidaInput by mutableStateOf(false)
    var nombreSubMedidaInput by mutableStateOf("")
    var precioSubMedidaInput by mutableStateOf("")

    var imagenSeleccionadaUri by mutableStateOf<android.net.Uri?>(null)
    var estaSubiendoImagen by mutableStateOf(false)

    var busquedaPedidoInput by mutableStateOf("")
    var filtroFechaInput by mutableStateOf("")

    var estadoPedidoSimulado by mutableStateOf("Preparando")
    var textoBotonEstado by mutableStateOf("Actualizar estado → Pedido Listo")
    var colorEstadoSimulado by mutableStateOf(Color(0xFFFEF3C7))
    var colorTextoEstadoSimulado by mutableStateOf(Color(0xFFD97706))

    var intentosFallidos by mutableStateOf(0)
    var estadoBloqueado by mutableStateOf(false)
    var usuarioSeleccionadoParaEditar by mutableStateOf<Any?>(null)

    fun cambiarPantalla(nuevaPantalla: TipoSubpantalla) {
        pantallaActual = nuevaPantalla
        if (nuevaPantalla == TipoSubpantalla.LOGIN) {
            mensajeError = ""
        }
    }

    fun procesarLogin() {
        if (estadoBloqueado) {
            mensajeError = "Acceso bloqueado por seguiridad. Demasiados intentos."
            return
        }
        val correo = usuarioInput.trim()
        val contrasena = contrasenaInput.trim()

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mensajeError = "Por favor, llene todos los campos."
            return
        }

        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { resultadoAuth ->
                val uidUsuario = resultadoAuth.user?.uid

                if (uidUsuario != null) {
                    db.collection("usuarios")
                        .document(uidUsuario)
                        .get()
                        .addOnSuccessListener { documento ->
                            if (documento.exists()) {
                                intentosFallidos = 0
                                mensajeError = ""
                                nombreAdministrador = documento.getString("nombre") ?: "Admin"
                                rolUsuarioActual = documento.getString("rol") ?: "EMPLEADO"
                                cambiarPantalla(TipoSubpantalla.DASHBOARD)
                            } else {
                                mensajeError = "El usuario existe pero no tiene perfil en la base de datos."
                            }
                        }
                        .addOnFailureListener {
                            mensajeError = "Error al leer los permisos del perfil."
                        }
                }
            }
            .addOnFailureListener {
                intentosFallidos++
                if (intentosFallidos >= 5) {
                    estadoBloqueado = true
                    mensajeError = "Área bloqueada. Supero los 5 intentos permitidos."
                } else {
                    mensajeError = "Correo o contraseña incorrectos ($intentosFallidos/5)"
                }
            }
    }

    fun avanzarEstadoPedido(contex: Context) {
        if (estadoPedidoSimulado == "Preparando") {
            estadoPedidoSimulado = "Pedido Listo"
            textoBotonEstado = "Actualizar estado → Entregado"
            colorEstadoSimulado = Color(0xFFD1FAE5)
            colorTextoEstadoSimulado = Color(0xFF065F46)
            android.widget.Toast.makeText(contex, "✨ ¡Pedido marcado como LISTO!", android.widget.Toast.LENGTH_SHORT).show()
        } else if (estadoPedidoSimulado == "Pedido Listo") {
            estadoPedidoSimulado = "Entregado"
            textoBotonEstado = "Pedido Finalizado y Entregado"
            colorEstadoSimulado = Color(0xFFE2E8F0)
            colorTextoEstadoSimulado = Color(0xFF475569)
            android.widget.Toast.makeText(contex, "\uD83D\uDCE6 ¡Pedido completado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun limpiarFormularioProducto() {
        nombreProductoInput = ""
        codigoProductoInput = ""
        precioProductoInput = ""
        marcaProductoInput = ""
        medidaProductoInput = ""
        categoriaProductoInput = ""
        descripcionProductoInput = ""
        imagenSeleccionadaUri = null
        estaSubiendoImagen = false
        menuCategoriasExpandido = false
        porcentajeIvaInput = 15.0
        tieneSubMedidaInput = false
        nombreSubMedidaInput = ""
        precioSubMedidaInput = ""
    }

    fun cerrarSesion() {
        auth.signOut()
        usuarioInput = ""
        contrasenaInput = ""
        rolUsuarioActual = ""
        nombreAdministrador = ""
        cambiarPantalla(TipoSubpantalla.LOGIN)
    }
}
