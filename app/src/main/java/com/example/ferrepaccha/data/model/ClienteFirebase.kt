package com.example.ferrepaccha.data.model

data class ClienteFirebase(
    val cedulaRuc: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val direccion: String = "",
    val correo: String = "",
    val telefono: String = "",
    val esEmpresa: Boolean = false
)
