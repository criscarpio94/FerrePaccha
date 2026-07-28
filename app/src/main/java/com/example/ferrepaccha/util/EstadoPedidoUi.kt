package com.example.ferrepaccha.util

import androidx.compose.ui.graphics.Color
import com.example.ferrepaccha.data.model.EstadoPedido

object EstadoPedidoUi {

    fun colores(estado: EstadoPedido): Pair<Color, Color> = when (estado) {
        EstadoPedido.RECIBIDO -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        EstadoPedido.PREPARANDO -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        EstadoPedido.LISTO -> Color(0xFFD1FAE5) to Color(0xFFB6BD2F)
        EstadoPedido.ENTREGADO -> Color(0xFFE2E8F0) to Color(0xFF4BA208)
    }

    fun colorPaso(estadoPaso: EstadoPedido, alcanzado: Boolean): Color {
        if (!alcanzado) return Color(0xFFE2E8F0)
        return colores(estadoPaso).second
    }

    fun parsear(estadoRaw: String): EstadoPedido = try {
        EstadoPedido.valueOf(estadoRaw)
    } catch (_: Exception) {
        EstadoPedido.RECIBIDO
    }
}
