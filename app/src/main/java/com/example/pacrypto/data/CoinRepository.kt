package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.api_data.ApiOhlcv
import com.example.pacrypto.data.room.assets.AssetDatabase
import com.example.pacrypto.data.room.assets.DBAsset
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvs
import com.example.pacrypto.data.room.ohlcvs.OhlcvsDatabase
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
    private val db_rate: RateDatabase,
    private val db_ohlcvs: OhlcvsDatabase,
) {
    private val assetDao = db_asset.assetDao()
    private val rateDao = db_rate.rateDao()
    private val ohlcvsDao = db_ohlcvs.ohlcvsDao()

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


    fun getOhlcv(
        id: String,
    ) = networkBoundResource(
        query = {
            ohlcvsDao.getOhlcvs(id)
        },
        fetch = {
            val ohlcvsDay = api.getOhlcvs(id, "1HRS", 24)
            Log.d(TAG + "_1", System.currentTimeMillis().toString())
            val ohlcvsWeek = api.getOhlcvs(id, "2HRS", 84)
            Log.d(TAG + "_2", System.currentTimeMillis().toString())
            val ohlcvsMonth = api.getOhlcvs(id, "8HRS", 90)
            Log.d(TAG + "_3", System.currentTimeMillis().toString())
            val ohlcvsQuarter = api.getOhlcvs(id, "1DAY", 90)
            Log.d(TAG + "_4", System.currentTimeMillis().toString())
            val ohlcvsYear = api.getOhlcvs(id, "5DAY", 73)
            Log.d(TAG + "_5", System.currentTimeMillis().toString())
            val ohlcvsAll = api.getOhlcvs(id, "1MTH")

            return@networkBoundResource listOf(
                ohlcvsDay,
                ohlcvsWeek,
                ohlcvsMonth,
                ohlcvsQuarter,
                ohlcvsYear,
                ohlcvsAll
            )
        },
        saveFetchResult = { ohlcvs ->
            db_ohlcvs.withTransaction {
                ohlcvsDao.deleteOhlcvs(id)
                ohlcvsDao.insertOhlcvs(ohlcvs.asDBType(id))
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
