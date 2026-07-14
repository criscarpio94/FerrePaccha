package com.example.ferrepaccha.ui.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferrepaccha.data.model.ProductoFirebase
import com.example.ferrepaccha.data.repository.ProductoRepositorio
import com.example.ferrepaccha.network.ImgBbRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductoViewModel : ViewModel() {

    private val repositorio = ProductoRepositorio()
    private val db = FirebaseFirestore.getInstance()

    private val _listaProductos = MutableStateFlow<List<ProductoFirebase>>(emptyList())
    val listaProductos: StateFlow<List<ProductoFirebase>> = _listaProductos

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        escucharProductosDelCatalogo()
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
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var urlImagenFinal = ""

                if (imagenUri != null) {
                    val urlSubida = ImgBbRepository.subirFotoAdmin(context, imagenUri)
                    if (urlSubida != null) {
                        urlImagenFinal = urlSubida
                    }
                }

                val docRef = db.collection("productos").document()
                val productoFinal = producto.copy(
                    id = docRef.id,
                    urlImagen = urlImagenFinal
                )

                docRef.set(productoFinal).await()
                onExito()
            } catch (e: Exception) {
                onError(e.message ?: "Error al guardar")
            }
        }
    }
}
