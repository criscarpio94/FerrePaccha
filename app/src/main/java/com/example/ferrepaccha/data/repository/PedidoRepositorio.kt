package com.example.ferrepaccha.data.repository

import android.util.Log
import com.example.ferrepaccha.data.model.EstadoPedido
import com.example.ferrepaccha.data.model.PedidoFirebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.example.ferrepaccha.util.normalizarCedulaRuc
import com.example.ferrepaccha.util.soloValidos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale

class PedidoRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val pedidosColeccion = db.collection("pedidos")
    private val contadoresColeccion = db.collection("contadores")

    companion object {
        private const val TAG = "PedidoRepositorio"
    }

    suspend fun crearPedido(pedido: PedidoFirebase): Result<String> {
        return try {
            val docRef = pedidosColeccion.document()
            val numeroPedido = generarNumeroPedido()
            val datosPedido = construirMapaPedido(pedido, numeroPedido)
            docRef.set(datosPedido).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear pedido", e)
            Result.failure(e)
        }
    }

    fun escucharPedidosRecientes(cedulaRuc: String): Flow<List<PedidoFirebase>> {
        val cedula = normalizarCedulaRuc(cedulaRuc)
        if (cedula.isBlank()) return flowOf(emptyList())

        return pedidosColeccion
            .whereEqualTo("cedulaRuc", cedula)
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .filter { it.exists() }
                    .mapNotNull { doc -> mapearDocumentoAPedido(doc) }
                    .soloValidos()
            }
    }

    fun escucharPedidoPorId(pedidoId: String): Flow<PedidoFirebase?> {
        if (pedidoId.isBlank()) return flowOf(null)

        return pedidosColeccion.document(pedidoId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) mapearDocumentoAPedido(snapshot) else null
            }
    }

    suspend fun buscarPedidos(texto: String): List<PedidoFirebase> {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return emptyList()

        val consultaPedido = consulta.uppercase(Locale.getDefault())

        return try {
            val porNumero = pedidosColeccion
                .whereEqualTo("numeroPedido", consultaPedido)
                .get()
                .await()

            if (!porNumero.isEmpty) {
                return porNumero.documents.mapNotNull { doc ->
                    mapearDocumentoAPedido(doc)
                }.soloValidos()
            }

            val porCedula = pedidosColeccion
                .whereEqualTo("cedulaRuc", normalizarCedulaRuc(consulta))
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .await()

            porCedula.documents.mapNotNull { doc ->
                mapearDocumentoAPedido(doc)
            }.soloValidos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun escucharTodosPedidos(): Flow<List<PedidoFirebase>> {
        return pedidosColeccion
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .filter { it.exists() }
                    .mapNotNull { doc -> mapearDocumentoAPedido(doc) }
                    .soloValidos()
            }
    }

    fun escucharBusquedaPedidos(texto: String): Flow<List<PedidoFirebase>> {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return flowOf(emptyList())

        val consultaPedido = consulta.uppercase(Locale.getDefault())
        val consultaCedula = normalizarCedulaRuc(consulta)
        val query = if (consultaPedido.startsWith("PED-")) {
            pedidosColeccion.whereEqualTo("numeroPedido", consultaPedido)
        } else {
            pedidosColeccion
                .whereEqualTo("cedulaRuc", consultaCedula)
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
        }

        return query.snapshots().map { snapshot ->
            snapshot.documents
                .filter { it.exists() }
                .mapNotNull { doc -> mapearDocumentoAPedido(doc) }
                .soloValidos()
        }
    }

    suspend fun obtenerPedidoPorId(pedidoId: String): PedidoFirebase? {
        return try {
            val doc = pedidosColeccion.document(pedidoId).get().await()
            if (doc.exists()) {
                mapearDocumentoAPedido(doc)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizarEstadoPedido(pedidoId: String, estado: String): Boolean {
        return try {
            pedidosColeccion.document(pedidoId)
                .update("estado", estado)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estado", e)
            false
        }
    }

    private fun mapearDocumentoAPedido(doc: DocumentSnapshot): PedidoFirebase? {
        val pedido = doc.toObject(PedidoFirebase::class.java) ?: return null
        return pedido.copy(
            id = doc.id,
            estado = doc.getString("estado") ?: pedido.estado
        )
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
            "cedulaRuc" to normalizarCedulaRuc(pedido.cedulaRuc),
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
            Log.w(TAG, "Transacción contador falló, usando respaldo", e)
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
            Log.w(TAG, "Contador falló, calculando desde pedidos", e)
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
