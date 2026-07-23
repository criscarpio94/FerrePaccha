package com.example.ferrepaccha.ui.cliente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.local.FerrePacchaDatabase
import com.example.ferrepaccha.data.model.ClienteFirebase
import com.example.ferrepaccha.data.model.ItemCarrito
import com.example.ferrepaccha.data.model.ItemPedidoFirebase
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.data.repository.CarritoRepositorio
import com.example.ferrepaccha.data.repository.ClienteRepositorio
import com.example.ferrepaccha.data.repository.PedidoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val carritoRepositorio = CarritoRepositorio(
        FerrePacchaDatabase.getDatabase(application).carritoDao()
    )
    private val clienteRepositorio = ClienteRepositorio()
    private val pedidoRepositorio = PedidoRepositorio()

    companion object {
        const val RECARGO_DOMICILIO = 3.50
    }

    val items: StateFlow<List<ItemCarrito>> = carritoRepositorio.itemsCarrito
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cantidadTotal: StateFlow<Int> = carritoRepositorio.cantidadTotalItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val tipoEntrega: StateFlow<String> = carritoRepositorio.tipoEntrega
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LOCAL")

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito = _mensajeExito.asStateFlow()

    private val _pedidoConfirmado = MutableStateFlow(false)
    val pedidoConfirmado = _pedidoConfirmado.asStateFlow()

    var cedulaRucInput = MutableStateFlow("")
    var nombresInput = MutableStateFlow("")
    var apellidosInput = MutableStateFlow("")
    var direccionInput = MutableStateFlow("")
    var correoInput = MutableStateFlow("")
    var telefonoInput = MutableStateFlow("")
    var esEmpresaInput = MutableStateFlow(false)

    fun agregarAlCarrito(item: ItemCarrito) {
        viewModelScope.launch {
            carritoRepositorio.agregarItem(item)
        }
    }

    fun actualizarCantidad(itemId: Long, cantidad: Int) {
        viewModelScope.launch {
            carritoRepositorio.actualizarCantidad(itemId, cantidad)
        }
    }

    fun cambiarTipoEntrega(tipo: String) {
        viewModelScope.launch {
            carritoRepositorio.guardarTipoEntrega(tipo)
        }
    }

    fun calcularSubtotal(lista: List<ItemCarrito>): Double =
        lista.sumOf { it.subtotal }

    fun calcularRecargo(tipo: String): Double =
        if (tipo == "DOMICILIO") RECARGO_DOMICILIO else 0.0

    fun calcularTotal(lista: List<ItemCarrito>, tipo: String): Double =
        calcularSubtotal(lista) + calcularRecargo(tipo)

    fun buscarClientePorCedula(cedula: String) {
        if (cedula.length < 10) return
        viewModelScope.launch {
            val cliente = clienteRepositorio.obtenerClientePorCedula(cedula)
            if (cliente != null) {
                nombresInput.value = cliente.nombres
                apellidosInput.value = cliente.apellidos
                direccionInput.value = cliente.direccion
                correoInput.value = cliente.correo
                telefonoInput.value = cliente.telefono
                esEmpresaInput.value = cliente.esEmpresa
            }
        }
    }

    fun confirmarPedido(onExito: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val listaItems = items.value
            if (listaItems.isEmpty()) {
                onError("El carrito está vacío")
                return@launch
            }

            val cedula = cedulaRucInput.value.trim()
            if (cedula.isEmpty() || cedula.length > 13) {
                onError("Ingrese una cédula o RUC válida (máx. 13 caracteres)")
                return@launch
            }

            val cliente = ClienteFirebase(
                cedulaRuc = cedula,
                nombres = nombresInput.value.trim(),
                apellidos = if (esEmpresaInput.value) "" else apellidosInput.value.trim(),
                direccion = direccionInput.value.trim(),
                correo = correoInput.value.trim(),
                telefono = telefonoInput.value.trim(),
                esEmpresa = esEmpresaInput.value
            )

            if (cliente.nombres.isEmpty() || cliente.direccion.isEmpty() ||
                cliente.correo.isEmpty() || cliente.telefono.isEmpty()
            ) {
                onError("Complete todos los campos obligatorios")
                return@launch
            }

            if (!esEmpresaInput.value && cliente.apellidos.isEmpty()) {
                onError("Ingrese los apellidos")
                return@launch
            }

            val tipo = tipoEntrega.value
            val subtotal = calcularSubtotal(listaItems)
            val recargo = calcularRecargo(tipo)
            val total = subtotal + recargo

            val itemsPedido = listaItems.map {
                ItemPedidoFirebase(
                    productoId = it.productoId,
                    codigoProducto = it.codigoProducto,
                    nombre = it.nombre,
                    cantidad = it.cantidad,
                    precioUnitario = it.precioUnitario,
                    medidaVenta = it.medidaVenta,
                    subtotal = it.subtotal
                )
            }

            val pedido = PedidoFirebase(
                cedulaRuc = cedula,
                nombresCliente = cliente.nombres,
                apellidosCliente = cliente.apellidos,
                direccionEntrega = cliente.direccion,
                correoCliente = cliente.correo,
                telefonoCliente = cliente.telefono,
                tipoEntrega = tipo,
                recargoEntrega = recargo,
                subtotal = subtotal,
                total = total,
                items = itemsPedido
            )

            val guardoCliente = clienteRepositorio.guardarCliente(cliente)
            if (!guardoCliente) {
                onError("No se pudo guardar los datos del cliente")
                return@launch
            }

            val resultadoPedido = pedidoRepositorio.crearPedido(pedido)
            resultadoPedido.onFailure { error ->
                val detalle = error.localizedMessage ?: error.message ?: "Error desconocido"
                onError("No se pudo registrar el pedido: $detalle")
                return@launch
            }

            carritoRepositorio.guardarConfigCompleta(tipo, cedula)
            carritoRepositorio.vaciarCarrito()
            _pedidoConfirmado.value = true
            onExito(cedula)
        }
    }

    fun limpiarFormularioCliente() {
        cedulaRucInput.value = ""
        nombresInput.value = ""
        apellidosInput.value = ""
        direccionInput.value = ""
        correoInput.value = ""
        telefonoInput.value = ""
        esEmpresaInput.value = false
    }

    fun resetPedidoConfirmado() {
        _pedidoConfirmado.value = false
    }
}

class CarritoViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            return CarritoViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
