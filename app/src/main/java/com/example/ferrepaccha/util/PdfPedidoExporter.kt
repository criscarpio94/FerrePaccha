package com.example.ferrepaccha.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.ferrepaccha.data.model.PedidoFirebase
import java.io.File
import java.io.FileOutputStream

object PdfPedidoExporter {

    fun exportar(context: Context, pedido: PedidoFirebase): Result<File> {
        return try {
            val documento = PdfDocument()
            val paginaInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val pagina = documento.startPage(paginaInfo)
            val canvas = pagina.canvas
            val titulo = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
            }
            val normal = Paint().apply { textSize = 12f }
            val negrita = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
            }

            var y = 40f
            canvas.drawText("FERRETERIA PACCHA E HIJOS", 40f, y, titulo)
            y += 28f
            canvas.drawText("Pedido: ${pedido.numeroPedido}", 40f, y, negrita)
            y += 22f
            canvas.drawText("Estado: ${pedido.estado}", 40f, y, normal)
            y += 30f

            canvas.drawText("DATOS DEL CLIENTE", 40f, y, negrita)
            y += 18f
            canvas.drawText(
                "Nombre: ${pedido.nombresCliente} ${pedido.apellidosCliente}".trim(),
                40f,
                y,
                normal
            )
            y += 16f
            canvas.drawText("CI/RUC: ${pedido.cedulaRuc}", 40f, y, normal)
            y += 16f
            canvas.drawText("Correo: ${pedido.correoCliente}", 40f, y, normal)
            y += 16f
            canvas.drawText("Telefono: ${pedido.telefonoCliente}", 40f, y, normal)
            y += 16f
            canvas.drawText("Direccion: ${pedido.direccionEntrega}", 40f, y, normal)
            y += 16f
            val tipoEntrega = if (pedido.tipoEntrega == "DOMICILIO") "Entrega a domicilio" else "Retiro en local"
            canvas.drawText("Tipo entrega: $tipoEntrega", 40f, y, normal)
            y += 28f

            canvas.drawText("DETALLE DE PRODUCTOS", 40f, y, negrita)
            y += 18f
            pedido.items.forEach { item ->
                canvas.drawText(
                    "${item.codigoProducto} | ${item.nombre} | ${item.medidaVenta} | x${item.cantidad} | $${String.format("%.2f", item.subtotal)}",
                    40f,
                    y,
                    normal
                )
                y += 16f
                if (y > 780f) y = 40f
            }

            y += 12f
            canvas.drawText("Subtotal: $${String.format("%.2f", pedido.subtotal)}", 40f, y, normal)
            y += 16f
            if (pedido.recargoEntrega > 0) {
                canvas.drawText(
                    "Recargo entrega: $${String.format("%.2f", pedido.recargoEntrega)}",
                    40f,
                    y,
                    normal
                )
                y += 16f
            }
            canvas.drawText("TOTAL: $${String.format("%.2f", pedido.total)}", 40f, y, titulo)

            documento.finishPage(pagina)

            val nombreSeguro = pedido.numeroPedido
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                .ifBlank { "pedido" }
            val archivo = File(context.cacheDir, "${nombreSeguro}_facturacion.pdf")
            FileOutputStream(archivo).use { documento.writeTo(it) }
            documento.close()
            Result.success(archivo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
