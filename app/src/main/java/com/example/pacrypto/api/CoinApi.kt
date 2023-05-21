package com.example.pacrypto.api

import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.api_data.ApiListRates
import com.example.pacrypto.data.api_data.ApiOhlcv
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface that holds all operations connected to api requests
 *
 * API_KEY_RESERVED is used to circumvent restrictions of free access plan
 * */
interface CoinApi {

    companion object {
        const val BASE_URL = "https://rest.coinapi.io/v1/"
        const val API_KEY = "373D10A6-2055-4A39-BC5D-65BD1685C0AD"
        const val API_KEY_RESERVED = "F4D66C7B-04A8-4715-9F14-A15074A4F322"
    }

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("assets")
    suspend fun getAssets(): List<ApiAsset>

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("assets/{asset_id}")
    suspend fun getExactAsset(
        @Path("asset_id") id: String
    ): List<ApiAsset>

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("exchangerate/USD")
    suspend fun getUSDRates(
        @Query("time") time: String
    ): ApiListRates

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("exchangerate/RUB")
    suspend fun getRUBRates(
        @Query("time") time: String
    ): ApiListRates

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("ohlcv/{symbol_id}/latest")
    suspend fun getOhlcvs(
        @Path("symbol_id") id: String,
        @Query("period_id") period_id: String,
        @Query("limit") limit: Int,
    ): List<ApiOhlcv>

    @Headers("X-CoinAPI-Key: $API_KEY_RESERVED")
    @GET("ohlcv/{symbol_id}/latest")
    suspend fun getOhlcvs(
        @Path("symbol_id") id: String,
        @Query("period_id") period_id: String,
    ): List<ApiOhlcv>
}