package com.example.ferrepaccha.interfaz

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.ImgBbRepository
import com.example.ferrepaccha.ProductoFirebase
import com.example.ferrepaccha.SubMedidaModel
import com.example.ferrepaccha.admin.TipoSubpantalla
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    //Instancias de seguridad y de base de datos
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    //NAVEGACION INTERNA EN APARTADO DE ADMINISTRADORES
    var pantallaActual by mutableStateOf(TipoSubpantalla.ADVERTENCIA)

    var nombreAdministrador by mutableStateOf("") //SOPORTE, GERENTE O EMPLEADO


    //Control para roles de Seguridad para Administrador Principal
    var rolUsuarioActual by mutableStateOf("")


    //ESTADO DEL FORMULARIO LOGIN
    var usuarioInput by mutableStateOf("")
    var contrasenaInput by mutableStateOf("")
    var contrasenaVisible by mutableStateOf(false)
    var mensajeError by mutableStateOf("")



    //FORMULARIO PARA CARGA DE PRODUCTOS
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


    //LISTA DE PRODUCTOS DESDE FIRESTORE
    var listaProductosReal by mutableStateOf<List<ProductoFirebase>>(emptyList())
    var estaCargandoProductos by mutableStateOf(false)



    //Para gestionar fotografias
    var imagenSeleccionadaUri by mutableStateOf<Uri?>(null)
    var estaSubiendoImagen by mutableStateOf(false)


    //Estados para apartado de atender pedidos
    var busquedaPedidoInput by mutableStateOf("")
    var filtroFechaInput by mutableStateOf("")


    //Estados para el detalle de los pedidos
    var estadoPedidoSimulado by mutableStateOf("Preparando")
    var textoBotonEstado by mutableStateOf("Actualizar estado → Pedido Listo")
    var colorEstadoSimulado by mutableStateOf(Color(0xFFFEF3C7))
    var colorTextoEstadoSimulado by mutableStateOf(Color(0xFFD97706))


    //Control de acceso y bloqueo
    var intentosFallidos by mutableStateOf(0)
    var estadoBloqueado by mutableStateOf(false)


    //Variable de apoyo
    var usuarioSeleccionadoParaEditar by mutableStateOf<Any?>(null)


    //FUNCIONES PARA LA NAVEGACION
    fun cambiarPantalla(nuevaPantalla: TipoSubpantalla) {
        pantallaActual = nuevaPantalla
        //Limpiar mensaje de error al continuar a la siguiente pantalla
        if (nuevaPantalla == TipoSubpantalla.LOGIN) {
            mensajeError = ""
        }
    }


    //Login para acceder
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

        //Validacion de Firebase de forma encriptada
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { resultadoAuth ->
                val uidUsuario = resultadoAuth.user?.uid

                if (uidUsuario != null) {
                    //Busqueda del rol que tiene ese usuario
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


    //Funcion para limpiar la pantalla al guardar los productos nuevos
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

    // Funcion para guardar el producto al catalogo
    fun guardarProductoAlCatalogo(context: Context) {
        if (nombreProductoInput.isEmpty() || codigoProductoInput.isEmpty() || precioProductoInput.isEmpty()) {
            android.widget.Toast.makeText(context, "Por favor, llene los campos obligatorios (*)", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            var urlImagenFinal = ""

            if (imagenSeleccionadaUri != null) {
                estaSubiendoImagen = true
                val urlSubida = ImgBbRepository.subirFotoAdmin(context, imagenSeleccionadaUri!!)
                if (urlSubida != null) {
                    urlImagenFinal = urlSubida
                }
                estaSubiendoImagen = false
            }

            // 1. Preparamos el mapa de submedida si está activo el flag
            val subMedidaEstructura = if (tieneSubMedidaInput) {
                SubMedidaModel(
                    nombreSubMedida = nombreSubMedidaInput.trim(),
                    precioSubMedida = precioSubMedidaInput.toDoubleOrNull() ?: 0.0
                )
            } else {
                null
            }

            // 2. Generamos una referencia de documento para obtener un ID automático
            val docRef = db.collection("productos").document()

            // 3. Instanciamos tu modelo real de ModelosBasesDatos.kt
            val nuevoProducto = ProductoFirebase(
                id = docRef.id,
                codigoIncremental = 0, // TODO: Implementar lógica de autoincrementar si aplica
                codigoProducto = codigoProductoInput.trim(),
                nombre = nombreProductoInput.trim(),
                marca = marcaProductoInput.trim(),
                descripcion = descripcionProductoInput.trim(),
                categoria = categoriaProductoInput.trim().ifEmpty { "General" },
                porcentajeIva = porcentajeIvaInput,
                medidaPrincipal = medidaProductoInput.trim(),
                precioPrincipal = precioProductoInput.toDoubleOrNull() ?: 0.0,
                tieneSubMedida = tieneSubMedidaInput,
                subMedida = subMedidaEstructura,
                emoji = "🛠️",
                urlImagen = urlImagenFinal
            )

            // 4. Guardamos en Firebase Firestore
            docRef.set(nuevoProducto)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(context, "📦 Producto guardado con éxito en el catálogo", android.widget.Toast.LENGTH_SHORT).show()
                    limpiarFormularioProducto()
                    cambiarPantalla(TipoSubpantalla.GESTION_PRODUCTOS) // Te regresa directo a la lista
                }
                .addOnFailureListener { e ->
                    android.widget.Toast.makeText(context, "❌ Error al guardar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
        }
    }

    //Para estados de los pedidos
    fun avanzarEstadoPedido(contex: Context) {
        if (estadoPedidoSimulado == "Preparando") {
            estadoPedidoSimulado = "Pedido Listo"
            textoBotonEstado = "Actualizar estado → Entregado"
            colorEstadoSimulado = Color(0xFFD1FAE5)
            colorTextoEstadoSimulado = Color(0xFF065F46)
            android.widget.Toast.makeText(contex, "✨ ¡Pedido marcado como LISTO!", android.widget.Toast.LENGTH_SHORT).show()
        } else if (estadoPedidoSimulado == "Pedido Listo") {
            estadoPedidoSimulado ="Entregado"
            textoBotonEstado = "Pedido Finalizado y Entregado"
            colorEstadoSimulado = Color(0xFFE2E8F0)
            colorTextoEstadoSimulado = Color(0xFF475569)
            android.widget.Toast.makeText(contex,"\uD83D\uDCE6 ¡Pedido completado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    //Cierre de sesion
    fun cerrarSesion() {
        auth.signOut()
        usuarioInput = ""
        contrasenaInput = ""
        rolUsuarioActual = ""
        nombreAdministrador = ""
        cambiarPantalla(TipoSubpantalla.LOGIN)
    }

    //FUNCION PARA TIEMPO REAL DE TRAIDA DE PRODUCTOS DE FIREBASE
    fun escucharProductosDelCatalogo() {
        // 🛡️ EVITA QUE EL PREVIEW SE ROMPA: Si es un entorno de diseño, no ejecutes Firebase
        if (android.os.Build.FINGERPRINT.startsWith("generic") || android.os.Build.MODEL.contains("google_sdk") || java.lang.System.getProperty("java.runtime.name")?.contains("Android") == false) {
            return
        }

        estaCargandoProductos = true
        db.collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    estaCargandoProductos = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val productosTomados = snapshot.toObjects(ProductoFirebase::class.java)
                    listaProductosReal = productosTomados
                }
                estaCargandoProductos = false
            }
    }
}