package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.room.CoinDatabase
import com.example.pacrypto.data.room.DBAsset
import com.example.pacrypto.util.asDBType
import com.example.pacrypto.util.networkBoundResource
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val TAG = "COIN_REPOSITORY"

class CoinRepository @Inject constructor(
    private val api: CoinApi,
    private val db: CoinDatabase
) {
    private val coinDao = db.coinDao()

    fun getAssets() = networkBoundResource(
        query = {
            coinDao.getAllAssets()
        },
        fetch = {
            api.getAssets()
        },
        saveFetchResult = { assets ->
            assets.forEach {
                Log.d(TAG, it.asset_id.toString())
            }
            db.withTransaction {
                coinDao.deleteAllAssets()
                coinDao.insertAssets(assets.asDBType())
            }
        }
    )
}
