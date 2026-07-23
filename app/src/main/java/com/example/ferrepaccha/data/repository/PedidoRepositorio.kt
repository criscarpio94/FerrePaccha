package com.example.ferrepaccha.data.repository

import android.util.Log
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale

class PedidoRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val pedidosColeccion = db.collection("pedidos")
    private val contadoresColeccion = db.collection("contadores")

    suspend fun crearPedido(pedido: PedidoFirebase): Result<String> {
        return try {
            val docRef = pedidosColeccion.document()
            val numeroPedido = generarNumeroPedido()
            val datosPedido = construirMapaPedido(pedido, numeroPedido)
            docRef.set(datosPedido).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("PedidoRepositorio", "Error al crear pedido", e)
            Result.failure(e)
        }
    }

    fun escucharPedidosRecientes(cedulaRuc: String): Flow<List<PedidoFirebase>> = callbackFlow {
        if (cedulaRuc.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = pedidosColeccion
            .whereEqualTo("cedulaRuc", cedulaRuc.trim())
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val pedidos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PedidoFirebase::class.java)?.copy(id = doc.id)
                    }
                    trySend(pedidos)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun buscarPedidos(texto: String): List<PedidoFirebase> {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return emptyList()

        return try {
            val porNumero = pedidosColeccion
                .whereEqualTo("numeroPedido", consulta.uppercase(Locale.getDefault()))
                .get()
                .await()

            if (!porNumero.isEmpty) {
                return porNumero.documents.mapNotNull { doc ->
                    doc.toObject(PedidoFirebase::class.java)?.copy(id = doc.id)
                }
            }

            val porCedula = pedidosColeccion
                .whereEqualTo("cedulaRuc", consulta)
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .await()

            porCedula.documents.mapNotNull { doc ->
                doc.toObject(PedidoFirebase::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun construirMapaPedido(pedido: PedidoFirebase, numeroPedido: String): HashMap<String, Any> {
        val itemsMap = pedido.items.map { item ->
            hashMapOf(
                "productoId" to item.productoId,
                "codigoProducto" to item.codigoProducto,
                "nombre" to item.nombre,
                "cantidad" to item.cantidad,
                "precioUnitario" to item.precioUnitario,
                "medidaVenta" to item.medidaVenta,
                "subtotal" to item.subtotal
            )
        }

        return hashMapOf(
            "numeroPedido" to numeroPedido,
            "cedulaRuc" to pedido.cedulaRuc,
            "nombresCliente" to pedido.nombresCliente,
            "apellidosCliente" to pedido.apellidosCliente,
            "direccionEntrega" to pedido.direccionEntrega,
            "correoCliente" to pedido.correoCliente,
            "telefonoCliente" to pedido.telefonoCliente,
            "tipoEntrega" to pedido.tipoEntrega,
            "recargoEntrega" to pedido.recargoEntrega,
            "subtotal" to pedido.subtotal,
            "total" to pedido.total,
            "estado" to EstadoPedido.RECIBIDO.name,
            "fechaCreacion" to Timestamp.now(),
            "items" to itemsMap
        )
    }

    private suspend fun generarNumeroPedido(): String {
        val calendario = Calendar.getInstance()
        val mes = String.format(Locale.US, "%02d", calendario.get(Calendar.MONTH) + 1)
        val anio = String.format(Locale.US, "%02d", calendario.get(Calendar.YEAR) % 100)
        val claveMes = "$mes$anio"
        val prefijo = "PED-$claveMes-"
        val docContador = contadoresColeccion.document("pedidos_$claveMes")

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docContador)
                val siguiente = if (snapshot.exists()) {
                    (snapshot.getLong("ultimo") ?: 0L) + 1L
                } else {
                    1L
                }
                transaction.set(docContador, mapOf("ultimo" to siguiente))
                "$prefijo$siguiente"
            }.await()
        } catch (e: Exception) {
            Log.w("PedidoRepositorio", "Transacción contador falló, usando respaldo", e)
            generarNumeroPedidoRespaldo(prefijo, docContador)
        }
    }

    private suspend fun generarNumeroPedidoRespaldo(
        prefijo: String,
        docContador: com.google.firebase.firestore.DocumentReference
    ): String {
        return try {
            val snapshot = docContador.get().await()
            val siguiente = if (snapshot.exists()) {
                (snapshot.getLong("ultimo") ?: 0L) + 1L
            } else {
                1L
            }
            docContador.set(mapOf("ultimo" to siguiente)).await()
            "$prefijo$siguiente"
        } catch (e: Exception) {
            Log.w("PedidoRepositorio", "Contador falló, calculando desde pedidos", e)
            val todos = pedidosColeccion.get().await()
            val maxNum = todos.documents.mapNotNull { doc ->
                doc.getString("numeroPedido")
                    ?.takeIf { it.startsWith(prefijo) }
                    ?.removePrefix(prefijo)
                    ?.toLongOrNull()
            }.maxOrNull() ?: 0L
            "$prefijo${maxNum + 1}"
        }
    }
}
