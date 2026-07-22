package com.example.ferrepaccha.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AccesoRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val accesoColeccion = db.collection("acceso")

    suspend fun obtenerCodigoMaestro(): String? {
        return try {
            val snapshot = accesoColeccion.limit(1).get().await()
            snapshot.documents.firstOrNull()?.getString("codigoMaestro")
        } catch (e: Exception) {
            null
        }
    }
}
