package com.example.ferrepaccha

import com.google.firebase.Timestamp

//Modelo para productos

data class ProductoFirebase(
    val id: String = "",
    val codigoIncremental : Int = 0,
    val codigoProducto: String = "",
    val nombre: String = "",
    val marca: String = "",
    val descripcion: String = "",
    val caegoria: String = "",
    val porcentajeIva: Double = 15.0,
    val medidaPrincipal: String = "",
    val precioPrincipal: Double = 0.0,
    val tieneSubMedida: Boolean = false,
    val subMedida: SubMedidaModel? =  null,
    val emoji: String = "\uD83D\uDCE6",
    val urlImagen: String = ""
)

//Sub medida de productos
data class SubMedidaModel(
    val nombreSubMedida: String = "",
    val precioSubMedida: Double = 0.0
)

//Modelo para usuarios y roles
data class UsuarioFirebase(
    val uid: String = "",
    val correo: String = "",
    val nombreCompleto: String = "",
    val rol: String = "Cliente",
    val activo: Boolean = true,
    val intentosFallidos: Int = 0,
    //Datos adicionales para facturacion de los clientes
    val tipoCliente: String = "Natural",
    val cedulaRuc: String = "",
    val nombreFacturacion: String = "",
    val apellidosFacturacion: String? = null,
    val direccion: String = "",
    val telefono: String = ""
)

//Modelo par auditorias
data class AuditoriaProducto(
    val idAuditoria: String = "",
    val productoId: String = "",
    val codigoProducto: String = "",
    val nombreProducto: String = "",
    val usuarioId: String = "",
    val usuarioNombre: String = "",
    val rolUsuario: String = "",
    val fechaCambio: Timestamp = Timestamp.now(),
    val motivo: String = "",
    val valoresAnteriores: Map<String, Any> = emptyMap(),
    val valoresNuevos: Map<String, Any> = emptyMap()
)
