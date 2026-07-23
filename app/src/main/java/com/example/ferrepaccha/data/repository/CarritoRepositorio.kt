package com.example.ferrepaccha.data.repository

import com.example.ferrepaccha.data.local.CarritoConfigEntity
import com.example.ferrepaccha.data.local.CarritoDao
import com.example.ferrepaccha.data.local.CarritoItemEntity
import com.example.ferrepaccha.data.model.ItemCarrito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CarritoRepositorio(private val carritoDao: CarritoDao) {

    val itemsCarrito: Flow<List<ItemCarrito>> = carritoDao.observarItems().map { lista ->
        lista.map { it.toDomain() }
    }

    val cantidadTotalItems: Flow<Int> = carritoDao.observarCantidadTotal()

    val tipoEntrega: Flow<String> = carritoDao.observarConfig().map { config ->
        config?.tipoEntrega ?: "LOCAL"
    }

    val ultimaCedulaConsultada: Flow<String> = carritoDao.observarConfig().map { config ->
        config?.ultimaCedulaConsultada.orEmpty()
    }

    suspend fun agregarItem(item: ItemCarrito) {
        val existente = carritoDao.buscarItemExistente(item.productoId, item.medidaVenta)
        if (existente != null) {
            carritoDao.actualizarCantidad(existente.id, existente.cantidad + item.cantidad)
        } else {
            carritoDao.insertarItem(item.toEntity())
        }
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int) {
        if (cantidad <= 0) {
            carritoDao.eliminarItem(itemId)
        } else {
            carritoDao.actualizarCantidad(itemId, cantidad)
        }
    }

    suspend fun eliminarItem(itemId: Long) {
        carritoDao.eliminarItem(itemId)
    }

    suspend fun vaciarCarrito() {
        carritoDao.vaciarCarrito()
    }

    suspend fun guardarTipoEntrega(tipo: String) {
        val configActual = carritoDao.observarConfig().first()
        carritoDao.guardarConfig(
            CarritoConfigEntity(
                id = 1,
                tipoEntrega = tipo,
                ultimaCedulaConsultada = configActual?.ultimaCedulaConsultada.orEmpty()
            )
        )
    }

    suspend fun guardarUltimaCedula(cedula: String) {
        val configActual = carritoDao.observarConfig().first()
        carritoDao.guardarConfig(
            CarritoConfigEntity(
                id = 1,
                tipoEntrega = configActual?.tipoEntrega ?: "LOCAL",
                ultimaCedulaConsultada = cedula
            )
        )
    }

    suspend fun guardarConfigCompleta(tipoEntrega: String, cedula: String) {
        carritoDao.guardarConfig(
            CarritoConfigEntity(
                id = 1,
                tipoEntrega = tipoEntrega,
                ultimaCedulaConsultada = cedula
            )
        )
    }

    private fun CarritoItemEntity.toDomain() = ItemCarrito(
        id = id,
        productoId = productoId,
        codigoProducto = codigoProducto,
        nombre = nombre,
        precioUnitario = precioUnitario,
        cantidad = cantidad,
        medidaVenta = medidaVenta,
        urlImagen = urlImagen
    )

    private fun ItemCarrito.toEntity() = CarritoItemEntity(
        id = id,
        productoId = productoId,
        codigoProducto = codigoProducto,
        nombre = nombre,
        precioUnitario = precioUnitario,
        cantidad = cantidad,
        medidaVenta = medidaVenta,
        urlImagen = urlImagen
    )
}
