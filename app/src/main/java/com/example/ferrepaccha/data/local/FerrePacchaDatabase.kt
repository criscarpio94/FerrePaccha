package com.example.ferrepaccha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Base de datos local Room
@Database(
    entities = [CarritoItemEntity::class, CarritoConfigEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FerrePacchaDatabase : RoomDatabase() {
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: FerrePacchaDatabase? = null

        fun getDatabase(context: Context): FerrePacchaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FerrePacchaDatabase::class.java,
                    "ferre_paccha_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
