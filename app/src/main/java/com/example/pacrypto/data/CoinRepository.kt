package com.example.pacrypto.data

import android.util.Log
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.api_data.ApiAsset
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "COIN_REPOSITORY"

class CoinRepository @Inject constructor(
    private val api: CoinApi
) {

    fun getAssets() = flow<ApiAsset> {
        // temporary
        delay(2000)
        val flow = api.getAssets().asFlow()
        Log.d(TAG, flow.toString())
        emitAll(flow)
    }
}