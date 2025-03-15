package com.example.divisas.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divisas.data.ExchangeRateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChartViewModel : ViewModel() {
    private val _data = MutableStateFlow<List<ExchangeRateData>>(emptyList())
    val data: StateFlow<List<ExchangeRateData>> = _data

    fun loadData(context: Context, moneda: String, fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            val uri = Uri.parse("content://com.example.divisas.provider/tipo_cambio")
                .buildUpon()
                .appendQueryParameter("moneda", moneda)
                .appendQueryParameter("fechaInicio", fechaInicio.toString())
                .appendQueryParameter("fechaFin", fechaFin.toString())
                .build()
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val dataList = mutableListOf<ExchangeRateData>()
            cursor?.use {
                val indexTime = it.getColumnIndex("timeLastUpdate")
                val indexValue = it.getColumnIndex("valor")
                while (it.moveToNext()) {
                    val time = it.getLong(indexTime)
                    val value = it.getDouble(indexValue)
                    dataList.add(ExchangeRateData(time, value))
                }
            }
            _data.value = dataList
        }
    }
}
