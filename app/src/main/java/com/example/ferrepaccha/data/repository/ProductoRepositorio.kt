package com.example.ferrepaccha.data.repository

import com.example.ferrepaccha.data.model.ProductoFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val productosColeccion = db.collection("productos")

    suspend fun obtenerProductos(): List<ProductoFirebase> {
        return try {
            val querySnapshot = productosColeccion.get().await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(ProductoFirebase::class.java)?.apply {
                    id = document.id
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun eliminarProducto(productoId: String): Boolean {
        return try {
            productosColeccion.document(productoId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarProducto(producto: ProductoFirebase): Boolean {
        return try {
            productosColeccion.document(producto.id).set(producto).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerProductoPorId(productoId: String): ProductoFirebase? {
        return try {
            val doc = productosColeccion.document(productoId).get().await()
            if (doc.exists()) {
                doc.toObject(ProductoFirebase::class.java)?.apply { id = doc.id }
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
