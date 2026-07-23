package com.example.ferrepaccha.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito_items")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productoId: String,
    val codigoProducto: String,
    val nombre: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val medidaVenta: String,
    val urlImagen: String
)

@Entity(tableName = "carrito_config")
data class CarritoConfigEntity(
    @PrimaryKey val id: Int = 1,
    val tipoEntrega: String = "LOCAL",
    val ultimaCedulaConsultada: String = ""
)
