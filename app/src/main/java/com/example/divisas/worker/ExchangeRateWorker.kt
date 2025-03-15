package com.example.divisas.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.withTransaction
import com.example.divisas.data.TipoCambio
import com.example.divisas.data.AppDatabase
import com.example.divisas.data.TipoCambioDetalle
import com.example.divisas.Retrofit.ExchangeRateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExchangeRateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WorkManager", "Iniciando sincronización...")

        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://v6.exchangerate-api.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(ExchangeRateService::class.java)
                val response = service.getLatestRates("USD")

                if (response.isSuccessful) {
                    val exchangeRate = response.body()
                    if (exchangeRate != null) {
                        Log.d("WorkManager", "Datos recibidos de la API")
                        Log.d("WorkManager", "Datos de Tipo de Cambio: ${exchangeRate.conversion_rates}")

                        val db = AppDatabase.getDatabase(applicationContext)
                        db.withTransaction {
                            // Insertar la entrada de TipoCambio
                            val id = db.tipoCambioDao().insertTipoCambio(
                                TipoCambio(
                                    timeLastUpdate = exchangeRate.time_last_update_unix,
                                    timeNextUpdate = exchangeRate.time_next_update_unix,
                                    baseCode = exchangeRate.base_code
                                )
                            )

                            // Crear la lista de detalles para cada tipo de cambio
                            val detalles = exchangeRate.conversion_rates.map { (codigo, valor) ->
                                TipoCambioDetalle(
                                    idTipoCambio = id,
                                    codigoDeMoneda = codigo,
                                    valor = valor
                                )
                            }

                            // Insertar detalles en la base de datos
                            db.tipoCambioDao().insertDetalles(detalles)
                            Log.d("WorkManager", "Detalles insertados: ${detalles.size} registros")
                        }

                        Log.d("WorkManager", "Sincronización completada con éxito")
                        return@withContext Result.success()
                    } else {
                        Log.e("WorkManager", "Respuesta de la API vacía")
                        return@withContext Result.failure()
                    }
                } else {
                    Log.e("WorkManager", "Error en la respuesta de la API: ${response.code()}")
                    return@withContext Result.failure()
                }
            } catch (e: Exception) {
                Log.e("WorkManager", "Error durante la sincronización: ${e.message}", e)
                return@withContext Result.failure()
            }
        }
    }
}
