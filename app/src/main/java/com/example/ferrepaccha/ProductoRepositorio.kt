package com.example.ferrepaccha

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepositorio {
    //Intancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    private val productosColeccion = db.collection("productos")

    //Obtener la lista de productos desde la nube en tiempo real

    suspend fun obtenerProductos(): List<ProductoFirebase> {
        return try {
            val querySnapshot =  productosColeccion.get().await()

            //convertir documentos no SQL en clase Kotlin
            querySnapshot.toObjects(ProductoFirebase::class.java)
        } catch (e: Exception) {
            //en caso de erro por ejemplo no conexion a internet se devuelve una lista vacia
            emptyList()
        }
    }
}