package com.example.pacrypto.data

import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.api_data.ApiAsset
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinApi
) {

    fun getAssets() = flow {
        // temporary
        val flow = api.getAssets().asFlow()
        emitAll(flow)
    }
}