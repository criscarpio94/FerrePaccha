package com.example.ferrepaccha.ui.cliente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.local.FerrePacchaDatabase
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.example.ferrepaccha.data.repository.CarritoRepositorio
import com.example.ferrepaccha.data.repository.PedidoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val pedidoRepositorio = PedidoRepositorio()
    private val carritoRepositorio = CarritoRepositorio(
        FerrePacchaDatabase.getDatabase(application).carritoDao()
    )

    val ultimaCedula: StateFlow<String> = carritoRepositorio.ultimaCedulaConsultada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _pedidosRecientes = MutableStateFlow<List<PedidoFirebase>>(emptyList())
    val pedidosRecientes = _pedidosRecientes.asStateFlow()

    private val _pedidosBusqueda = MutableStateFlow<List<PedidoFirebase>>(emptyList())
    val pedidosBusqueda = _pedidosBusqueda.asStateFlow()

    private val _estaBuscando = MutableStateFlow(false)
    val estaBuscando = _estaBuscando.asStateFlow()

    private var escuchaJob: Job? = null

    fun escucharPedidosRecientes(cedula: String) {
        escuchaJob?.cancel()
        if (cedula.isBlank()) {
            _pedidosRecientes.value = emptyList()
            return
        }
        escuchaJob = viewModelScope.launch {
            pedidoRepositorio.escucharPedidosRecientes(cedula).collect { pedidos ->
                val limite = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)
                _pedidosRecientes.value = pedidos.filter { pedido ->
                    val fecha = pedido.fechaCreacion?.toDate()?.time ?: 0L
                    fecha >= limite
                }
            }
        }
    }

    fun buscarPedidos(texto: String) {
        viewModelScope.launch {
            _estaBuscando.value = true
            _pedidosBusqueda.value = pedidoRepositorio.buscarPedidos(texto)
            _estaBuscando.value = false
        }
    }

    fun limpiarBusqueda() {
        _pedidosBusqueda.value = emptyList()
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
