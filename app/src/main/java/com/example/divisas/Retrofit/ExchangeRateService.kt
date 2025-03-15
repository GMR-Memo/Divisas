package com.example.divisas.Retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {

    @GET("v6/e622d448c89844c761683288/latest/{baseCode}")
    suspend fun getLatestRates(@Path("baseCode") baseCode: String): Response<ExchangeRateResponse>
}
