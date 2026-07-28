package com.example.ferrepaccha.ui.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.data.repository.AccesoRepositorio
import com.example.ferrepaccha.data.repository.ProductoRepositorio
import com.example.ferrepaccha.network.ImgBbRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductoViewModel : ViewModel() {

    private val repositorio = ProductoRepositorio()
    private val accesoRepositorio = AccesoRepositorio()
    private val db = FirebaseFirestore.getInstance()

    private var codigoMaestroCache: String? = null

    private val _listaProductos = MutableStateFlow<List<ProductoFirebase>>(emptyList())
    val listaProductos: StateFlow<List<ProductoFirebase>> = _listaProductos

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        escucharProductosDelCatalogo()
        precargarCodigoMaestro()
    }

    private fun precargarCodigoMaestro() {
        viewModelScope.launch {
            codigoMaestroCache = accesoRepositorio.obtenerCodigoMaestro()
        }
    }

    fun verificarCodigoMaestro(textoIngresado: String, onResultado: (Boolean) -> Unit) {
        if (textoIngresado.isEmpty()) {
            onResultado(false)
            return
        }

        val codigoCacheado = codigoMaestroCache
        if (codigoCacheado != null) {
            onResultado(textoIngresado == codigoCacheado)
            return
        }

        viewModelScope.launch {
            val codigoMaestro = accesoRepositorio.obtenerCodigoMaestro().also {
                codigoMaestroCache = it
            }
            onResultado(codigoMaestro != null && textoIngresado == codigoMaestro)
        }
    }

    fun escucharProductosDelCatalogo() {
        db.collection("productos").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                _listaProductos.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductoFirebase::class.java)?.apply {
                        id = document.id
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun eliminarProducto(productoId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = repositorio.eliminarProducto(productoId)
            onResult(exito)
        }
    }

    fun guardarProductoAlCatalogo(
        context: android.content.Context,
        producto: ProductoFirebase,
        imagenUri: android.net.Uri?,
        onExito: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var urlImagenFinal = producto.urlImagen
                var advertencia: String? = null

                if (imagenUri != null) {
                    val urlSubida = ImgBbRepository.subirFotoAdmin(
                        context,
                        imagenUri,
                        producto.codigoProducto.ifBlank { producto.nombre }
                    )
                    if (urlSubida != null) {
                        urlImagenFinal = urlSubida
                    } else if (producto.urlImagen.isNotBlank()) {
                        urlImagenFinal = producto.urlImagen
                        advertencia = "No se obtuvo nueva URL; se conservó la imagen anterior"
                    } else {
                        advertencia = "Imagen subida pero no se obtuvo enlace; producto guardado sin foto"
                    }
                }

                val docRef = if (producto.id.isNotBlank()) {
                    db.collection("productos").document(producto.id)
                } else {
                    db.collection("productos").document()
                }

                val productoFinal = producto.copy(
                    id = docRef.id,
                    urlImagen = urlImagenFinal
                )

                docRef.set(productoFinal).await()
                onExito(advertencia)
            } catch (e: Exception) {
                onError(e.message ?: "Error al guardar")
            }
        }
    }
}
