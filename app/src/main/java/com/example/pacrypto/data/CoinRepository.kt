package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.CoinDatabase
import com.example.pacrypto.data.room.DBAsset
import com.example.pacrypto.util.UiState
import com.example.pacrypto.util.asDBType
import com.example.pacrypto.util.networkBoundResource
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "COIN_REPOSITORY"

class CoinRepository @Inject constructor(
    private val api: CoinApi,
    private val db: CoinDatabase
) {
    private val coinDao = db.coinDao()

    fun getAssets(
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkBoundResource(
        query = {
            coinDao.getAllAssets()
        },
        fetch = {
            api.getAssets()
        },
        saveFetchResult = { assets ->
            assets.forEach {
                Log.d(TAG, it.asset_id)
            }
            db.withTransaction {
                coinDao.deleteAllAssets()
                coinDao.insertAssets(assets.asDBType())
            }
        },
        shouldFetch = {
            if (forceRefresh) {
                true
            } else {
                true
            }
        },
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { error ->
            if (error !is HttpException && error !is IOException) {
                throw error
            }
            onFetchFailed(error)
        }
    )

    /*
    fun getAssetsByTicker(
        ticker: String,
    ) = networkBoundResource(
        query = {
            coinDao.getAssetsByTicker()
        },
        fetch = { mutableListOf<DBAsset>() },
        shouldFetch = {
            false
        },
        saveFetchResult = {}
    )
    */

    fun getAssetsByTicker(
        ticker: String,
    ) = channelFlow<UiState<List<DBAsset>>> {
        coinDao.getAssetsByTicker(ticker).collect { send(UiState.Success(it)) }
    }
}
