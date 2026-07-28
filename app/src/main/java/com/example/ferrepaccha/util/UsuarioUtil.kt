package com.example.ferrepaccha.util

import com.example.ferrepaccha.data.model.UsuarioFirebase

fun UsuarioFirebase.nombreMostrable(): String =
    nombreCompleto.ifBlank { nombre }.ifBlank { correo }

fun UsuarioFirebase.normalizarNombre(): UsuarioFirebase {
    val visible = nombreCompleto.ifBlank { nombre }
    return copy(
        nombre = nombre.ifBlank { visible },
        nombreCompleto = visible
    )
}
