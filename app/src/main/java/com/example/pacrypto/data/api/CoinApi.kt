package com.example.pacrypto.data.api

import com.example.pacrypto.data.api.models.ApiAsset
import com.example.pacrypto.data.api.models.ApiListRates
import com.example.pacrypto.data.api.models.ApiOhlcv
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
    }

    @GET("assets")
    suspend fun getAssets(): List<ApiAsset>

    @GET("assets/{asset_id}")
    suspend fun getExactAsset(
        @Path("asset_id") id: String
    ): List<ApiAsset>

    @GET("exchangerate/USD")
    suspend fun getUSDRates(
        @Query("time") time: String
    ): ApiListRates

    @GET("exchangerate/RUB")
    suspend fun getRUBRates(
        @Query("time") time: String
    ): ApiListRates

    @GET("ohlcv/{symbol_id}/latest")
    suspend fun getOhlcvs(
        @Path("symbol_id") id: String,
        @Query("period_id") period_id: String,
        @Query("limit") limit: Int,
    ): List<ApiOhlcv>

    @GET("ohlcv/{symbol_id}/latest")
    suspend fun getOhlcvs(
        @Path("symbol_id") id: String,
        @Query("period_id") period_id: String,
    ): List<ApiOhlcv>
}