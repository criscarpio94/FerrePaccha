package com.example.ferrepaccha.data.model

import com.google.firebase.Timestamp

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
