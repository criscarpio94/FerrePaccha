package com.example.ferrepaccha.data.repository

import com.example.ferrepaccha.data.model.AuditoriaProducto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuditoriaRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("auditoria_productos")

    suspend fun registrar(auditoria: AuditoriaProducto): Boolean {
        return try {
            val docRef = coleccion.document()
            val registro = auditoria.copy(idAuditoria = docRef.id, fechaCambio = Timestamp.now())
            docRef.set(registro).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
