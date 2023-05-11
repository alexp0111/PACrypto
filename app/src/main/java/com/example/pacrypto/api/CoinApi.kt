package com.example.pacrypto.api

import com.example.pacrypto.data.api_data.ApiAsset
import retrofit2.http.GET
import retrofit2.http.Headers

interface CoinApi {

    companion object {
        const val BASE_URL = "https://rest-sandbox.coinapi.io/v1/"
        const val API_KEY = "373D10A6-2055-4A39-BC5D-65BD1685C0AD"
    }

    @Headers("X-CoinAPI-Key: $API_KEY")
    @GET("assets")
    suspend fun getAssets(): List<ApiAsset>
}