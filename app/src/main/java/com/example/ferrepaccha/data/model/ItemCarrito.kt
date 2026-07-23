package com.example.ferrepaccha.data.model

data class ItemCarrito(
    val id: Long = 0,
    val productoId: String = "",
    val codigoProducto: String = "",
    val nombre: String = "",
    val precioUnitario: Double = 0.0,
    val cantidad: Int = 1,
    val medidaVenta: String = "",
    val urlImagen: String = ""
) {
    val subtotal: Double get() = precioUnitario * cantidad
}
