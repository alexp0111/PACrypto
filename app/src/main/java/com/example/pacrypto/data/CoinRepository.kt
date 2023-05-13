package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.assets.AssetDatabase
import com.example.pacrypto.data.room.assets.DBAsset
import com.example.pacrypto.data.room.rates.RateDatabase
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
    private val db_asset: AssetDatabase,
    private val db_rate: RateDatabase
) {
    private val coinDao = db_asset.assetDao()
    private val rateDao = db_rate.rateDao()

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
            db_asset.withTransaction {
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

    fun getUSDRates(
        time: String,
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkBoundResource(
        query = {
            rateDao.getAllUSDRates()
        },
        fetch = {
            api.getUSDRates(time)
        },
        saveFetchResult = { rate ->
            rate.rates.forEach {
                Log.d(TAG + "_USD", it.rate.toString())
            }
            db_rate.withTransaction {
                rateDao.deleteAllUSDRates()
                rateDao.insertUSDRates(rate.asDBType())
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

    fun getRUBRates(
        time: String,
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkBoundResource(
        query = {
            rateDao.getAllRUBRates()
        },
        fetch = {
            api.getRUBRates(time)
        },
        saveFetchResult = { rate ->
            rate.rates.forEach {
                Log.d(TAG + "_RUB", it.rate.toString())
            }
            db_rate.withTransaction {
                rateDao.deleteAllRUBRates()
                rateDao.insertRUBRates(rate.asDBType())
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
