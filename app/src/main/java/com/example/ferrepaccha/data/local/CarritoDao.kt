package com.example.ferrepaccha.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//Dao para Querys
@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito_items ORDER BY id ASC")
    fun observarItems(): Flow<List<CarritoItemEntity>>

    @Query("SELECT COUNT(*) FROM carrito_items")
    fun observarCantidadTotal(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: CarritoItemEntity): Long

    @Query("UPDATE carrito_items SET cantidad = :cantidad WHERE id = :itemId")
    suspend fun actualizarCantidad(itemId: Long, cantidad: Int)

    @Query("DELETE FROM carrito_items WHERE id = :itemId")
    suspend fun eliminarItem(itemId: Long)

    @Query("DELETE FROM carrito_items")
    suspend fun vaciarCarrito()

    @Query("SELECT * FROM carrito_config WHERE id = 1 LIMIT 1")
    fun observarConfig(): Flow<CarritoConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarConfig(config: CarritoConfigEntity)

    @Query("SELECT * FROM carrito_items WHERE productoId = :productoId AND medidaVenta = :medida LIMIT 1")
    suspend fun buscarItemExistente(productoId: String, medida: String): CarritoItemEntity?
}
