package com.example.divisas.Retrofit

data class ExchangeRateResponse(
    val result: String,
    val time_last_update_unix: Long,
    val time_next_update_unix: Long,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
