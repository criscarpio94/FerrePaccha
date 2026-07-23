package com.example.ferrepaccha.data.model

import com.google.firebase.Timestamp

data class PedidoFirebase(
    var id: String = "",
    val numeroPedido: String = "",
    val cedulaRuc: String = "",
    val nombresCliente: String = "",
    val apellidosCliente: String = "",
    val direccionEntrega: String = "",
    val correoCliente: String = "",
    val telefonoCliente: String = "",
    val tipoEntrega: String = "LOCAL",
    val recargoEntrega: Double = 0.0,
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val estado: String = EstadoPedido.RECIBIDO.name,
    val fechaCreacion: Timestamp? = null,
    val items: List<ItemPedidoFirebase> = emptyList()
)

enum class EstadoPedido {
    RECIBIDO,
    PREPARANDO,
    LISTO,
    ENTREGADO;

    fun etiqueta(): String = when (this) {
        RECIBIDO -> "Recibido"
        PREPARANDO -> "Preparando"
        LISTO -> "Pedido Listo"
        ENTREGADO -> "Entregado"
    }
}
