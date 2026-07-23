package com.example.ferrepaccha.data.repository

import com.example.ferrepaccha.data.model.ClienteFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClienteRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val clientesColeccion = db.collection("clientes")

    suspend fun obtenerClientePorCedula(cedulaRuc: String): ClienteFirebase? {
        return try {
            val documento = clientesColeccion.document(cedulaRuc.trim()).get().await()
            if (documento.exists()) {
                documento.toObject(ClienteFirebase::class.java)?.copy(
                    cedulaRuc = documento.id
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun guardarCliente(cliente: ClienteFirebase): Boolean {
        return try {
            clientesColeccion.document(cliente.cedulaRuc.trim())
                .set(cliente)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
