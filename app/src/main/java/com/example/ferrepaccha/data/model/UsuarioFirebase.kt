package com.example.ferrepaccha.data.model

data class UsuarioFirebase(
    val uid: String = "",
    val correo: String = "",
    val nombreCompleto: String = "",
    val rol: String = "Cliente",
    val activo: Boolean = true,
    val intentosFallidos: Int = 0,
    val tipoCliente: String = "Natural",
    val cedulaRuc: String = "",
    val nombreFacturacion: String = "",
    val apellidosFacturacion: String? = null,
    val direccion: String = "",
    val telefono: String = ""
)
