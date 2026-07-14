package com.example.ferrepaccha.data.model

data class ProductoFirebase(
    var id: String = "",
    val codigoIncremental: Int = 0,
    val codigoProducto: String = "",
    val nombre: String = "",
    val marca: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val porcentajeIva: Double = 15.0,
    val medidaPrincipal: String = "",
    val precioPrincipal: Double = 0.0,
    val tieneSubMedida: Boolean = false,
    val subMedida: SubMedidaModel? = null,
    val emoji: String = "\uD83D\uDCE6",
    val urlImagen: String = ""
)
