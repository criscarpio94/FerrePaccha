package com.example.ferrepaccha.interfaz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.ProductoFirebase
import com.example.ferrepaccha.ProductoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProductoViewModel : ViewModel() {

    private val repositorio = ProductoRepositorio()

    //Estado de la lista de productos que mostrara las pantallas
    private val _productos = MutableStateFlow<List<ProductoFirebase>>(emptyList())
    val productos: StateFlow<List<ProductoFirebase>> = _productos

    //Barra de carga por si el internet esta lento
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            _productos.value = repositorio.obtenerProductos()
            _cargando.value = false
        }
    }
}