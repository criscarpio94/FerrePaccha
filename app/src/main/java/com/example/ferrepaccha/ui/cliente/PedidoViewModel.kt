package com.example.ferrepaccha.ui.cliente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.data.repository.PedidoRepositorio
import com.example.ferrepaccha.util.esPedidoValido
import com.example.ferrepaccha.util.normalizarCedulaRuc
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val pedidoRepositorio = PedidoRepositorio()

    private val _pedidosRecientes = MutableStateFlow<List<PedidoFirebase>>(emptyList())
    val pedidosRecientes = _pedidosRecientes.asStateFlow()

    private val _pedidosBusqueda = MutableStateFlow<List<PedidoFirebase>>(emptyList())
    val pedidosBusqueda = _pedidosBusqueda.asStateFlow()

    private val _estaBuscando = MutableStateFlow(false)
    val estaBuscando = _estaBuscando.asStateFlow()

    private val _cedulaConsultaActiva = MutableStateFlow("")
    val cedulaConsultaActiva: StateFlow<String> = _cedulaConsultaActiva.asStateFlow()

    private var escuchaCedulaJob: Job? = null
    private var busquedaJob: Job? = null
    private val escuchasPorPedido = mutableMapOf<String, Job>()

    fun reanudarSeguimientoSiActivo() {
        val cedula = _cedulaConsultaActiva.value
        if (cedula.isBlank()) return

        if (escuchaCedulaJob?.isActive != true) {
            iniciarSeguimientoPorCedula(cedula)
        } else {
            sincronizarEscuchasIndividuales(pedidosVisibles())
        }
    }

    fun iniciarSeguimientoPorCedula(cedula: String) {
        val cedulaNormalizada = normalizarCedulaRuc(cedula)
        _cedulaConsultaActiva.value = cedulaNormalizada

        escuchaCedulaJob?.cancel()
        cancelarEscuchasIndividuales()

        if (cedulaNormalizada.isBlank()) {
            _pedidosRecientes.value = emptyList()
            return
        }

        escuchaCedulaJob = viewModelScope.launch {
            pedidoRepositorio.escucharPedidosRecientes(cedulaNormalizada).collect { pedidos ->
                val filtrados = filtrarRecientes(pedidos)
                _pedidosRecientes.value = filtrados
                sincronizarEscuchasIndividuales(pedidosVisibles())
            }
        }
    }

    fun registrarPedidoRecienCreado(cedula: String, pedidoId: String) {
        iniciarSeguimientoPorCedula(cedula)

        if (pedidoId.isBlank()) return
        viewModelScope.launch {
            val pedido = pedidoRepositorio.obtenerPedidoPorId(pedidoId) ?: return@launch
            if (!pedido.esPedidoValido()) return@launch
            actualizarPedidoEnLista(pedido, enRecientes = true)
            escucharPedidoIndividual(pedido.id)
        }
    }

    fun buscarPedidos(texto: String) {
        val consulta = texto.trim()
        busquedaJob?.cancel()

        if (consulta.isBlank()) {
            _pedidosBusqueda.value = emptyList()
            _estaBuscando.value = false
            sincronizarEscuchasIndividuales(pedidosVisibles())
            return
        }

        val consultaPedido = consulta.uppercase(Locale.getDefault())
        if (!consultaPedido.startsWith("PED-") && consulta.length >= 10) {
            iniciarSeguimientoPorCedula(consulta)
        }

        _estaBuscando.value = true
        busquedaJob = viewModelScope.launch {
            pedidoRepositorio.escucharBusquedaPedidos(consulta).collect { pedidos ->
                _pedidosBusqueda.value = pedidos
                _estaBuscando.value = false
                sincronizarEscuchasIndividuales(pedidosVisibles())
            }
        }
    }

    fun limpiarBusqueda() {
        busquedaJob?.cancel()
        _pedidosBusqueda.value = emptyList()
        _estaBuscando.value = false
        sincronizarEscuchasIndividuales(pedidosVisibles())
    }

    fun limpiarSesionConsulta() {
        escuchaCedulaJob?.cancel()
        busquedaJob?.cancel()
        cancelarEscuchasIndividuales()
        _cedulaConsultaActiva.value = ""
        _pedidosRecientes.value = emptyList()
        _pedidosBusqueda.value = emptyList()
        _estaBuscando.value = false
    }

    private fun pedidosVisibles(): List<PedidoFirebase> =
        (_pedidosRecientes.value + _pedidosBusqueda.value).distinctBy { it.id }

    private fun sincronizarEscuchasIndividuales(pedidos: List<PedidoFirebase>) {
        val idsActivos = pedidos.map { it.id }.filter { it.isNotBlank() }.toSet()

        (escuchasPorPedido.keys - idsActivos).forEach { id ->
            escuchasPorPedido.remove(id)?.cancel()
        }

        idsActivos.forEach { id ->
            escucharPedidoIndividual(id)
        }
    }

    private fun escucharPedidoIndividual(pedidoId: String) {
        if (pedidoId.isBlank() || pedidoId in escuchasPorPedido) return

        escuchasPorPedido[pedidoId] = viewModelScope.launch {
            pedidoRepositorio.escucharPedidoPorId(pedidoId).collect { pedido ->
                if (pedido != null && pedido.esPedidoValido()) {
                    actualizarPedidoEnLista(pedido)
                } else {
                    removerPedidoDeListas(pedidoId)
                }
            }
        }
    }

    private fun actualizarPedidoEnLista(
        pedido: PedidoFirebase,
        enRecientes: Boolean = true
    ) {
        if (enRecientes || _pedidosRecientes.value.any { it.id == pedido.id }) {
            _pedidosRecientes.update { actuales ->
                filtrarRecientes(reemplazarPedido(actuales, pedido))
            }
        }
        if (_pedidosBusqueda.value.any { it.id == pedido.id }) {
            _pedidosBusqueda.update { actuales ->
                reemplazarPedido(actuales, pedido)
            }
        }
    }

    private fun reemplazarPedido(
        actuales: List<PedidoFirebase>,
        pedido: PedidoFirebase
    ): List<PedidoFirebase> {
        val indice = actuales.indexOfFirst { it.id == pedido.id }
        return if (indice >= 0) {
            actuales.toMutableList().apply { this[indice] = pedido }
        } else {
            listOf(pedido) + actuales
        }
    }

    private fun removerPedidoDeListas(pedidoId: String) {
        escuchasPorPedido.remove(pedidoId)?.cancel()
        _pedidosRecientes.update { it.filter { pedido -> pedido.id != pedidoId } }
        _pedidosBusqueda.update { it.filter { pedido -> pedido.id != pedidoId } }
    }

    private fun cancelarEscuchasIndividuales() {
        escuchasPorPedido.values.forEach { it.cancel() }
        escuchasPorPedido.clear()
    }

    private fun filtrarRecientes(pedidos: List<PedidoFirebase>): List<PedidoFirebase> {
        val limite = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)
        return pedidos
            .filter { pedido ->
                val fecha = pedido.fechaCreacion?.toDate()?.time ?: return@filter false
                fecha >= limite
            }
            .sortedByDescending { it.fechaCreacion?.toDate()?.time ?: 0L }
    }

    override fun onCleared() {
        limpiarSesionConsulta()
        super.onCleared()
    }
}

class PedidoViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {
            return PedidoViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
