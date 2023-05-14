package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.assets.AssetDatabase
import com.example.pacrypto.data.room.assets.DBAsset
import com.example.pacrypto.data.room.rates.RateDatabase
import com.example.pacrypto.util.SearchType
import com.example.pacrypto.util.UiState
import com.example.pacrypto.util.asDBType
import com.example.pacrypto.util.networkBoundResource
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

private const val TAG = "COIN_REPOSITORY"

class CoinRepository @Inject constructor(
    private val api: CoinApi,
    private val db_asset: AssetDatabase,
    private val db_rate: RateDatabase
) {
    private val assetDao = db_asset.assetDao()
    private val rateDao = db_rate.rateDao()

    fun getAssets() = networkBoundResource(
        query = {
            assetDao.getAllAssets()
        },
        fetch = {
            api.getAssets()
        },
        saveFetchResult = { assets ->
            assets.forEach {
                Log.d(TAG, it.asset_id)
            }
            db_asset.withTransaction {
                assetDao.deleteAllAssets()
                assetDao.insertAssets(assets.asDBType())
            }
        }
    )

    fun getUSDRates(
        time: String,
        actual: Boolean,
    ) = networkBoundResource(
        query = {
            if (actual) {
                rateDao.getAllUSDRatesAct()
            } else {
                rateDao.getAllUSDRatesPrv()
            }
        },
        fetch = {
            api.getUSDRates(time)
        },
        saveFetchResult = { rate ->
            rate.rates.forEach {
                Log.d(
                    TAG + "_USD",
                    it.rate.toString() + " | " + it.time + " | " + it.asset_id_quote
                )
            }
            db_rate.withTransaction {
                if (actual) {
                    rateDao.deleteAllUSDRatesAct()
                    rateDao.insertUSDRatesAct(rate.asDBType("usd", "act"))
                } else {
                    rateDao.deleteAllUSDRatesPrv()
                    rateDao.insertUSDRatesPrv(rate.asDBType("usd", "prv"))
                }
            }
        }
    )

    fun getRUBRates(
        time: String,
        actual: Boolean,
    ) = networkBoundResource(
        query = {
            if (actual) {
                rateDao.getAllRUBRatesAct()
            } else {
                rateDao.getAllRUBRatesPrv()
            }
        },
        fetch = {
            api.getRUBRates(time)
        },
        saveFetchResult = { rate ->
            rate.rates.forEach {
                Log.d(
                    TAG + "_RUB",
                    it.rate.toString() + " | " + it.time + " | " + it.asset_id_quote
                )
            }
            db_rate.withTransaction {
                if (actual) {
                    rateDao.deleteAllRUBRatesAct()
                    rateDao.insertRUBRatesAct(rate.asDBType("rub", "act"))
                } else {
                    rateDao.deleteAllRUBRatesPrv()
                    rateDao.insertRUBRatesPrv(rate.asDBType("rub", "prv"))
                }
            }
        }
    )

    fun getExactAsset(
        pair: Pair<String, SearchType>,
    ) = channelFlow<UiState<List<DBAsset>>> {
        if (pair.second == SearchType.NAME) {
            assetDao.getAssetsByName(pair.first).collect { send(UiState.Success(it)) }
        } else {
            assetDao.getAssetsByTicker(pair.first).collect { send(UiState.Success(it)) }
        }
    }
}
