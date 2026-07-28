package com.example.ferrepaccha.util

import com.example.ferrepaccha.data.model.PedidoFirebase

fun normalizarCedulaRuc(valor: String): String =
    valor.trim().replace("\\s".toRegex(), "")

fun PedidoFirebase.esPedidoValido(): Boolean {
    return numeroPedido.isNotBlank() &&
        cedulaRuc.isNotBlank() &&
        nombresCliente.isNotBlank() &&
        items.isNotEmpty() &&
        total > 0.0
}

fun List<PedidoFirebase>.soloValidos(): List<PedidoFirebase> = filter { it.esPedidoValido() }
