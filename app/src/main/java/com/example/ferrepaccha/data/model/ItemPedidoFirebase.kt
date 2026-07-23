package com.example.ferrepaccha.data.model

data class ItemPedidoFirebase(
    val productoId: String = "",
    val codigoProducto: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val medidaVenta: String = "",
    val subtotal: Double = 0.0
)
