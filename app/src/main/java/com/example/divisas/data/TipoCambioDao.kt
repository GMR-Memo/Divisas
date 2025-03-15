package com.example.divisas.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TipoCambioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTipoCambio(tipoCambio: TipoCambio): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<TipoCambioDetalle>)

    @Query("""
        SELECT tc.timeLastUpdate, tcd.codigoDeMoneda, tcd.valor 
        FROM tipo_cambio AS tc 
        INNER JOIN tipo_cambio_detalle AS tcd ON tc.id = tcd.idTipoCambio 
        WHERE tcd.codigoDeMoneda = :moneda 
          AND tc.timeLastUpdate BETWEEN :fechaInicio AND :fechaFin 
          AND tc.baseCode = 'MXN'
    """)
    fun getExchangeRateCursor(moneda: String, fechaInicio: Long, fechaFin: Long): Cursor
}
