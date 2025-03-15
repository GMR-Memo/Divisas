package com.example.divisas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TipoCambio::class, TipoCambioDetalle::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tipoCambioDao(): TipoCambioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "divisas_database"
                )
                    // Para producción se recomienda definir migraciones en lugar de fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
