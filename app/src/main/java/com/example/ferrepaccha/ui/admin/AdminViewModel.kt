package com.example.ferrepaccha.ui.admin

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.model.AuditoriaProducto
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.data.model.SubMedidaModel
import com.example.ferrepaccha.data.model.UsuarioFirebase
import com.example.ferrepaccha.data.repository.AuditoriaRepositorio
import com.example.ferrepaccha.data.repository.PedidoRepositorio
import com.example.ferrepaccha.data.repository.UsuarioRepositorio
import com.example.ferrepaccha.util.FechaUtil
import com.example.ferrepaccha.util.PdfPedidoExporter
import com.example.ferrepaccha.util.soloValidos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.TimeUnit

data class ResumenDiaAdmin(
    val pedidosHoy: Int = 0,
    val pendientes: Int = 0,
    val ingresos: Double = 0.0,
    val completados: Int = 0
)

data class NotificacionPedido(
    val pedidoId: String,
    val numeroPedido: String,
    val mensaje: String
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val pedidoRepositorio = PedidoRepositorio()
    private val usuarioRepositorio = UsuarioRepositorio()
    private val auditoriaRepositorio = AuditoriaRepositorio()
    private val prefs = application.getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)

    var pantallaActual by mutableStateOf(TipoSubpantalla.LOGIN)
    var nombreAdministrador by mutableStateOf("")
    var rolUsuarioActual by mutableStateOf("")
    var uidAdministrador by mutableStateOf("")

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
    var motivoAuditoriaInput by mutableStateOf("")
    var imagenSeleccionadaUri by mutableStateOf<Uri?>(null)
    var urlImagenActual by mutableStateOf("")
    var estaSubiendoImagen by mutableStateOf(false)
    var productoEnEdicionId by mutableStateOf("")
    var busquedaProductoInput by mutableStateOf("")

    var busquedaPedidoInput by mutableStateOf("")
    var filtroFechaInput by mutableStateOf("")
    var filtroFechaMillis by mutableStateOf<Long?>(null)
    var pedidoSeleccionadoId by mutableStateOf("")
    var pantallaRetornoPedido by mutableStateOf(TipoSubpantalla.GESTION_PEDIDOS)
    var modoBusquedaPedidos by mutableStateOf(false)

    var nombreUsuarioInput by mutableStateOf("")
    var correoUsuarioInput by mutableStateOf("")
    var contrasenaUsuarioInput by mutableStateOf("")
    var rolUsuarioFormulario by mutableStateOf("EMPLEADO")
    var usuarioEnEdicionId by mutableStateOf("")
    var rolGestionActual by mutableStateOf("EMPLEADO")
    var correoAnteriorUsuarioInput by mutableStateOf("")
    var rolUsuarioEnEdicionOriginal by mutableStateOf("")

    var intentosFallidos by mutableStateOf(0)
    var estadoBloqueado by mutableStateOf(false)
    var mostrarNotificaciones by mutableStateOf(false)

    private var adminCorreoSesion = ""
    private var adminContrasenaSesion = ""
    private var escuchaPedidosJob: Job? = null
    private var escuchaUsuariosJob: Job? = null

    private val _pedidos = MutableStateFlow<List<PedidoFirebase>>(emptyList())
    val pedidos: StateFlow<List<PedidoFirebase>> = _pedidos.asStateFlow()

    private val _usuarios = MutableStateFlow<List<UsuarioFirebase>>(emptyList())
    val usuarios: StateFlow<List<UsuarioFirebase>> = _usuarios.asStateFlow()

    private val _resumenDia = MutableStateFlow(ResumenDiaAdmin())
    val resumenDia: StateFlow<ResumenDiaAdmin> = _resumenDia.asStateFlow()

    private val _notificaciones = MutableStateFlow<List<NotificacionPedido>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionPedido>> = _notificaciones.asStateFlow()

    private val _sesionAdminActiva = MutableStateFlow(auth.currentUser != null)
    val sesionAdminActiva: StateFlow<Boolean> = _sesionAdminActiva.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _sesionAdminActiva.value = firebaseAuth.currentUser != null
        if (firebaseAuth.currentUser == null) {
            pantallaActual = TipoSubpantalla.LOGIN
            nombreAdministrador = ""
            rolUsuarioActual = ""
            uidAdministrador = ""
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        verificarSesionExistente()
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun verificarSesionExistente() {
        val user = auth.currentUser ?: run {
            _sesionAdminActiva.value = false
            return
        }
        viewModelScope.launch {
            try {
                val doc = db.collection("usuarios").document(user.uid).get().await()
                if (doc.exists()) {
                    uidAdministrador = user.uid
                    nombreAdministrador = doc.getString("nombre")
                        ?: doc.getString("nombreCompleto")
                        ?: "Admin"
                    rolUsuarioActual = doc.getString("rol") ?: "EMPLEADO"
                    adminCorreoSesion = user.email.orEmpty()
                    _sesionAdminActiva.value = true
                    pantallaActual = TipoSubpantalla.DASHBOARD
                    iniciarEscuchas()
                } else {
                    _sesionAdminActiva.value = false
                }
            } catch (_: Exception) {
                _sesionAdminActiva.value = false
            }
        }
    }

    fun ingresarPanelAdmin() {
        if (_sesionAdminActiva.value) {
            pantallaActual = TipoSubpantalla.DASHBOARD
            iniciarEscuchas()
        } else {
            pantallaActual = TipoSubpantalla.LOGIN
        }
    }

    fun cambiarPantalla(nuevaPantalla: TipoSubpantalla) {
        pantallaActual = nuevaPantalla
        if (nuevaPantalla == TipoSubpantalla.LOGIN) {
            mensajeError = ""
        }
        if (nuevaPantalla == TipoSubpantalla.FORMULARIO_PRODUCTO && productoEnEdicionId.isEmpty()) {
            limpiarFormularioProducto()
        }
        if (nuevaPantalla == TipoSubpantalla.FORMULARIO_USUARIO && usuarioEnEdicionId.isEmpty()) {
            limpiarFormularioUsuario()
        }
    }

    fun procesarLogin() {
        if (estadoBloqueado) {
            mensajeError = "Acceso bloqueado por seguridad. Demasiados intentos."
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
                    db.collection("usuarios").document(uidUsuario).get()
                        .addOnSuccessListener { documento ->
                            if (documento.exists()) {
                                val activo = documento.getBoolean("activo") ?: true
                                if (!activo) {
                                    auth.signOut()
                                    mensajeError = "Usuario inactivo. Contacte al soporte."
                                    return@addOnSuccessListener
                                }
                                intentosFallidos = 0
                                mensajeError = ""
                                uidAdministrador = uidUsuario
                                nombreAdministrador = documento.getString("nombre")
                                    ?: documento.getString("nombreCompleto")
                                    ?: "Admin"
                                rolUsuarioActual = documento.getString("rol") ?: "EMPLEADO"
                                adminCorreoSesion = correo
                                adminContrasenaSesion = contrasena
                                _sesionAdminActiva.value = true
                                cambiarPantalla(TipoSubpantalla.DASHBOARD)
                                iniciarEscuchas()
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
                    mensajeError = "Área bloqueada. Superó los 5 intentos permitidos."
                } else {
                    mensajeError = "Correo o contraseña incorrectos ($intentosFallidos/5)"
                }
            }
    }

    private fun iniciarEscuchas() {
        escuchaPedidosJob?.cancel()
        escuchaPedidosJob = viewModelScope.launch {
            pedidoRepositorio.escucharTodosPedidos().collect { lista ->
                val validos = lista.soloValidos()
                _pedidos.value = validos
                calcularResumenDia(validos)
                actualizarNotificaciones(validos)
            }
        }
    }

    fun escucharUsuariosPorRol(rol: String) {
        rolGestionActual = rol
        escuchaUsuariosJob?.cancel()
        escuchaUsuariosJob = viewModelScope.launch {
            usuarioRepositorio.escucharPorRol(rol).collect { _usuarios.value = it }
        }
    }

    private fun calcularResumenDia(pedidos: List<PedidoFirebase>) {
        val hoy = java.util.Calendar.getInstance()
        val inicio = FechaUtil.inicioDelDia(hoy)
        val fin = FechaUtil.finDelDia(hoy)

        val pedidosDelDia = pedidos.filter { pedido ->
            val fecha = FechaUtil.timestampADate(pedido.fechaCreacion)?.time ?: return@filter false
            fecha in inicio..fin
        }
        val entregadosHoy = pedidosDelDia.filter { it.estado == EstadoPedido.ENTREGADO.name }
        val pendientesHoy = pedidosDelDia.count { it.estado != EstadoPedido.ENTREGADO.name }

        _resumenDia.value = ResumenDiaAdmin(
            pedidosHoy = pedidosDelDia.size,
            pendientes = pendientesHoy,
            ingresos = entregadosHoy.sumOf { it.total },
            completados = entregadosHoy.size
        )
    }

    private fun actualizarNotificaciones(pedidos: List<PedidoFirebase>) {
        val vistos = prefs.getStringSet("notif_vistas", emptySet()) ?: emptySet()
        val nuevas = pedidos
            .filter { it.estado == EstadoPedido.RECIBIDO.name && it.id !in vistos }
            .map {
                NotificacionPedido(
                    pedidoId = it.id,
                    numeroPedido = it.numeroPedido,
                    mensaje = "Nuevo pedido solicitado"
                )
            }
        _notificaciones.value = nuevas
    }

    fun marcarNotificacionVista(pedidoId: String) {
        val actuales = prefs.getStringSet("notif_vistas", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        actuales.add(pedidoId)
        prefs.edit().putStringSet("notif_vistas", actuales).apply()
        _notificaciones.value = _notificaciones.value.filter { it.pedidoId != pedidoId }
        pedidoSeleccionadoId = pedidoId
        pantallaRetornoPedido = TipoSubpantalla.DASHBOARD
        cambiarPantalla(TipoSubpantalla.DETALLE_PEDIDO)
    }

    fun pedidosPendientesFiltrados(): List<PedidoFirebase> {
        return filtrarPedidos(_pedidos.value)
            .filter { it.estado != EstadoPedido.ENTREGADO.name }
    }

    fun todosPedidosFiltrados(): List<PedidoFirebase> {
        val limite = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        return filtrarPedidos(_pedidos.value)
            .filter { (FechaUtil.timestampADate(it.fechaCreacion)?.time ?: 0L) >= limite }
    }

    fun pedidosRecientes24h(): List<PedidoFirebase> {
        val limite = FechaUtil.hace24Horas()
        return _pedidos.value.filter {
            (FechaUtil.timestampADate(it.fechaCreacion)?.time ?: 0L) >= limite
        }
    }

    fun pedidoSeleccionado(): PedidoFirebase? {
        return _pedidos.value.find { it.id == pedidoSeleccionadoId }
            ?: _pedidos.value.find { it.numeroPedido == pedidoSeleccionadoId }
    }

    private fun filtrarPedidos(lista: List<PedidoFirebase>): List<PedidoFirebase> {
        var resultado = lista
        val texto = busquedaPedidoInput.trim()
        if (texto.isNotEmpty()) {
            resultado = resultado.filter { pedido ->
                pedido.numeroPedido.contains(texto, ignoreCase = true) ||
                    pedido.cedulaRuc.contains(texto, ignoreCase = true) ||
                    pedido.nombresCliente.contains(texto, ignoreCase = true) ||
                    pedido.apellidosCliente.contains(texto, ignoreCase = true) ||
                    "${pedido.nombresCliente} ${pedido.apellidosCliente}".contains(texto, ignoreCase = true)
            }
        }
        filtroFechaMillis?.let { millis ->
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
            val inicio = FechaUtil.inicioDelDia(cal)
            val fin = FechaUtil.finDelDia(cal)
            resultado = resultado.filter { pedido ->
                val fecha = FechaUtil.timestampADate(pedido.fechaCreacion)?.time ?: 0L
                fecha in inicio..fin
            }
        }
        return resultado
    }

    fun abrirDetallePedido(pedidoId: String, retorno: TipoSubpantalla = TipoSubpantalla.GESTION_PEDIDOS) {
        pedidoSeleccionadoId = pedidoId
        pantallaRetornoPedido = retorno
        cambiarPantalla(TipoSubpantalla.DETALLE_PEDIDO)
    }

    fun avanzarEstadoPedido(context: Context, onResultado: (String) -> Unit) {
        val pedido = pedidoSeleccionado() ?: return
        val estadoActual = try {
            EstadoPedido.valueOf(pedido.estado)
        } catch (_: Exception) {
            EstadoPedido.RECIBIDO
        }
        val siguiente = when (estadoActual) {
            EstadoPedido.RECIBIDO -> EstadoPedido.PREPARANDO
            EstadoPedido.PREPARANDO -> EstadoPedido.LISTO
            EstadoPedido.LISTO -> EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO -> null
        } ?: run {
            onResultado("El pedido ya fue entregado")
            return
        }

        viewModelScope.launch {
            val ok = pedidoRepositorio.actualizarEstadoPedido(pedido.id, siguiente.name)
            onResultado(
                if (ok) "Estado actualizado a ${siguiente.etiqueta()}"
                else "No se pudo actualizar el estado"
            )
        }
    }

    fun textoBotonEstadoPedido(pedido: PedidoFirebase? = pedidoSeleccionado()): String {
        val siguiente = siguienteEstado(pedido) ?: return "Pedido finalizado"
        return "Actualizar estado → ${siguiente.etiqueta()}"
    }

    fun coloresEstadoPedido(pedido: PedidoFirebase? = pedidoSeleccionado()): Pair<Color, Color> {
        val target = pedido ?: return Color(0xFFFEF3C7) to Color(0xFFD97706)
        val estado = try {
            EstadoPedido.valueOf(target.estado)
        } catch (_: Exception) {
            EstadoPedido.RECIBIDO
        }
        return when (estado) {
            EstadoPedido.RECIBIDO -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
            EstadoPedido.PREPARANDO -> Color(0xFFFEF3C7) to Color(0xFFD97706)
            EstadoPedido.LISTO -> Color(0xFFD1FAE5) to Color(0xFF065F46)
            EstadoPedido.ENTREGADO -> Color(0xFFE2E8F0) to Color(0xFF475569)
        }
    }

    fun colorBotonEstadoPedido(pedido: PedidoFirebase? = pedidoSeleccionado()): Color {
        return when (siguienteEstado(pedido)) {
            EstadoPedido.PREPARANDO -> Color(0xFFF59E0B)
            EstadoPedido.LISTO -> Color(0xFF059669)
            EstadoPedido.ENTREGADO -> Color(0xFF2563EB)
            else -> Color(0xFF64748B)
        }
    }

    private fun siguienteEstado(pedido: PedidoFirebase? = pedidoSeleccionado()): EstadoPedido? {
        val target = pedido ?: return null
        val estado = try {
            EstadoPedido.valueOf(target.estado)
        } catch (_: Exception) {
            EstadoPedido.RECIBIDO
        }
        return when (estado) {
            EstadoPedido.RECIBIDO -> EstadoPedido.PREPARANDO
            EstadoPedido.PREPARANDO -> EstadoPedido.LISTO
            EstadoPedido.LISTO -> EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO -> null
        }
    }

    fun exportarPedidoPdf(context: Context, onResultado: (Result<File>) -> Unit) {
        val pedido = pedidoSeleccionado() ?: return
        viewModelScope.launch {
            onResultado(PdfPedidoExporter.exportar(context, pedido))
        }
    }

    fun cargarProductoParaEdicion(producto: ProductoFirebase) {
        productoEnEdicionId = producto.id
        codigoProductoInput = producto.codigoProducto
        nombreProductoInput = producto.nombre
        marcaProductoInput = producto.marca
        medidaProductoInput = producto.medidaPrincipal
        categoriaProductoInput = producto.categoria
        descripcionProductoInput = producto.descripcion
        precioProductoInput = producto.precioPrincipal.toString()
        porcentajeIvaInput = producto.porcentajeIva
        tieneSubMedidaInput = producto.tieneSubMedida
        nombreSubMedidaInput = producto.subMedida?.nombreSubMedida.orEmpty()
        precioSubMedidaInput = producto.subMedida?.precioSubMedida?.toString().orEmpty()
        urlImagenActual = producto.urlImagen
        imagenSeleccionadaUri = null
        motivoAuditoriaInput = ""
        cambiarPantalla(TipoSubpantalla.FORMULARIO_PRODUCTO)
    }

    fun construirProductoDesdeFormulario(): ProductoFirebase {
        val subMedida = if (tieneSubMedidaInput && nombreSubMedidaInput.isNotBlank()) {
            SubMedidaModel(
                nombreSubMedida = nombreSubMedidaInput.trim(),
                precioSubMedida = precioSubMedidaInput.toDoubleOrNull() ?: 0.0
            )
        } else null

        return ProductoFirebase(
            id = productoEnEdicionId,
            codigoProducto = codigoProductoInput.trim(),
            nombre = nombreProductoInput.trim(),
            marca = marcaProductoInput.trim(),
            descripcion = descripcionProductoInput.trim(),
            categoria = categoriaProductoInput.trim(),
            porcentajeIva = porcentajeIvaInput,
            medidaPrincipal = medidaProductoInput.trim(),
            precioPrincipal = precioProductoInput.toDoubleOrNull() ?: 0.0,
            tieneSubMedida = tieneSubMedidaInput && subMedida != null,
            subMedida = subMedida,
            urlImagen = urlImagenActual
        )
    }

    fun registrarAuditoriaProducto(
        productoId: String,
        codigo: String,
        nombre: String,
        motivo: String,
        anteriores: Map<String, Any>,
        nuevos: Map<String, Any>
    ) {
        viewModelScope.launch {
            auditoriaRepositorio.registrar(
                AuditoriaProducto(
                    productoId = productoId,
                    codigoProducto = codigo,
                    nombreProducto = nombre,
                    usuarioId = uidAdministrador,
                    usuarioNombre = nombreAdministrador,
                    rolUsuario = rolUsuarioActual,
                    motivo = motivo,
                    valoresAnteriores = anteriores,
                    valoresNuevos = nuevos
                )
            )
        }
    }

    fun cargarUsuarioParaEdicion(usuario: UsuarioFirebase) {
        if (!puedeGestionarRol(usuario.rol)) return
        usuarioEnEdicionId = usuario.uid
        nombreUsuarioInput = usuario.nombreCompleto.ifBlank { usuario.nombre }
        correoUsuarioInput = usuario.correo
        correoAnteriorUsuarioInput = usuario.correo
        rolUsuarioFormulario = usuario.rol.ifBlank { rolGestionActual }
        rolUsuarioEnEdicionOriginal = usuario.rol
        contrasenaUsuarioInput = ""
        cambiarPantalla(TipoSubpantalla.FORMULARIO_USUARIO)
    }

    fun puedeGestionarRol(rolObjetivo: String): Boolean {
        return when (rolUsuarioActual) {
            "SOPORTE" -> rolObjetivo == "GERENTE" || rolObjetivo == "EMPLEADO"
            "GERENTE" -> rolObjetivo == "EMPLEADO"
            else -> false
        }
    }

    fun rolesDisponiblesEnFormulario(): List<String> {
        return when {
            usuarioEnEdicionId.isBlank() -> listOf(rolGestionActual)
            rolUsuarioActual == "SOPORTE" -> listOf(rolUsuarioEnEdicionOriginal)
            rolUsuarioActual == "GERENTE" -> listOf("EMPLEADO")
            else -> emptyList()
        }
    }

    fun guardarUsuario(onResultado: (Boolean, String) -> Unit) {
        if (nombreUsuarioInput.isBlank() || correoUsuarioInput.isBlank()) {
            onResultado(false, "Complete nombre y correo")
            return
        }
        if (!puedeGestionarRol(rolUsuarioFormulario)) {
            onResultado(false, "No tiene permisos para gestionar este rol")
            return
        }
        if (usuarioEnEdicionId.isBlank() && contrasenaUsuarioInput.length < 6) {
            onResultado(false, "La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (usuarioEnEdicionId.isNotBlank() &&
            contrasenaUsuarioInput.isNotBlank() &&
            contrasenaUsuarioInput.length < 6
        ) {
            onResultado(false, "La nueva contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            try {
                if (usuarioEnEdicionId.isBlank()) {
                    val resultado = auth.createUserWithEmailAndPassword(
                        correoUsuarioInput.trim(),
                        contrasenaUsuarioInput
                    ).await()
                    val uid = resultado.user?.uid ?: run {
                        onResultado(false, "No se pudo crear el usuario")
                        return@launch
                    }
                    usuarioRepositorio.actualizarUsuarioEnFirestore(
                        uid = uid,
                        nombre = nombreUsuarioInput.trim(),
                        correo = correoUsuarioInput.trim(),
                        rol = rolUsuarioFormulario
                    )
                    db.collection("usuarios").document(uid).set(
                        mapOf(
                            "nombre" to nombreUsuarioInput.trim(),
                            "nombreCompleto" to nombreUsuarioInput.trim(),
                            "correo" to correoUsuarioInput.trim(),
                            "rol" to rolUsuarioFormulario,
                            "activo" to true
                        )
                    ).await()
                    if (adminCorreoSesion.isNotBlank() && adminContrasenaSesion.isNotBlank()) {
                        auth.signOut()
                        auth.signInWithEmailAndPassword(adminCorreoSesion, adminContrasenaSesion).await()
                    }
                    limpiarFormularioUsuario()
                    onResultado(true, "Usuario creado correctamente")
                } else {
                    if (!puedeGestionarRol(rolUsuarioEnEdicionOriginal)) {
                        onResultado(false, "No tiene permisos para editar este usuario")
                        return@launch
                    }

                    val okFirestore = usuarioRepositorio.actualizarUsuarioEnFirestore(
                        uid = usuarioEnEdicionId,
                        nombre = nombreUsuarioInput.trim(),
                        correo = correoUsuarioInput.trim(),
                        rol = rolUsuarioFormulario
                    )
                    if (!okFirestore) {
                        onResultado(false, "No se pudo actualizar el usuario en Firestore")
                        return@launch
                    }

                    val correoCambio = correoAnteriorUsuarioInput.trim() != correoUsuarioInput.trim()
                    val contrasenaCambio = contrasenaUsuarioInput.isNotBlank()
                    var mensaje = "Usuario actualizado en Firebase"

                    if (correoCambio || contrasenaCambio) {
                        val resultadoAuth = usuarioRepositorio.actualizarCredencialesAuth(
                            uid = usuarioEnEdicionId,
                            correoNuevo = correoUsuarioInput.trim(),
                            contrasenaNueva = contrasenaUsuarioInput.takeIf { it.isNotBlank() }
                        )
                        mensaje = if (resultadoAuth.isSuccess) {
                            "Usuario y credenciales actualizados"
                        } else {
                            "Usuario actualizado. ${resultadoAuth.exceptionOrNull()?.message.orEmpty()}"
                        }
                    }

                    limpiarFormularioUsuario()
                    onResultado(true, mensaje)
                }
            } catch (e: Exception) {
                onResultado(false, e.localizedMessage ?: "Error al guardar usuario")
            }
        }
    }

    fun eliminarUsuario(uid: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResultado(usuarioRepositorio.eliminarUsuario(uid))
        }
    }

    fun limpiarFormularioProducto() {
        productoEnEdicionId = ""
        nombreProductoInput = ""
        codigoProductoInput = ""
        precioProductoInput = ""
        marcaProductoInput = ""
        medidaProductoInput = ""
        categoriaProductoInput = ""
        descripcionProductoInput = ""
        imagenSeleccionadaUri = null
        urlImagenActual = ""
        estaSubiendoImagen = false
        menuCategoriasExpandido = false
        porcentajeIvaInput = 15.0
        tieneSubMedidaInput = false
        nombreSubMedidaInput = ""
        precioSubMedidaInput = ""
        motivoAuditoriaInput = ""
    }

    fun limpiarFormularioUsuario() {
        usuarioEnEdicionId = ""
        nombreUsuarioInput = ""
        correoUsuarioInput = ""
        correoAnteriorUsuarioInput = ""
        contrasenaUsuarioInput = ""
        rolUsuarioEnEdicionOriginal = ""
        rolUsuarioFormulario = rolGestionActual
    }

    fun cerrarSesion() {
        auth.signOut()
        _sesionAdminActiva.value = false
        escuchaPedidosJob?.cancel()
        escuchaUsuariosJob?.cancel()
        adminCorreoSesion = ""
        adminContrasenaSesion = ""
        usuarioInput = ""
        contrasenaInput = ""
        rolUsuarioActual = ""
        nombreAdministrador = ""
        uidAdministrador = ""
        _pedidos.value = emptyList()
        _notificaciones.value = emptyList()
        cambiarPantalla(TipoSubpantalla.LOGIN)
    }
}

class AdminViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
