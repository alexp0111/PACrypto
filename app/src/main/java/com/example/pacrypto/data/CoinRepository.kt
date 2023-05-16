package com.example.pacrypto.data

import android.util.Log
import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.ohlcvs.OhlcvsDatabase
import com.example.pacrypto.data.room.search_items.SearchItemDatabase
import com.example.pacrypto.util.SearchType
import com.example.pacrypto.util.UiState
import com.example.pacrypto.util.asDBType
import com.example.pacrypto.util.networkBoundResource
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

private const val TAG = "COIN_REPOSITORY"

class CoinRepository @Inject constructor(
    private val api: CoinApi,
    private val db_search_item: SearchItemDatabase,
    private val db_ohlcvs: OhlcvsDatabase,
) {
    private val searchDao = db_search_item.searchDao()
    private val ohlcvsDao = db_ohlcvs.ohlcvsDao()

    fun getSearchItems(
        timeActual: String,
        timePrevious: String,
    ) = networkBoundResource(
        query = {
            searchDao.getAllSearchItems()
        },
        fetch = {
            val assetList = api.getAssets()
            val ratesUSDListActual = api.getUSDRates(timeActual)
            val ratesUSDListPrevious = api.getUSDRates(timePrevious)
            val ratesRUBListActual = api.getRUBRates(timeActual)
            return@networkBoundResource SearchItemConverter.convert(
                assetList,
                ratesUSDListActual,
                ratesUSDListPrevious,
                ratesRUBListActual,
            )
        },
        saveFetchResult = { searchItems ->
            searchItems.forEach {
                Log.d(TAG, it.toString())
            }
            db_search_item.withTransaction {
                searchDao.deleteAllSearchItems()
                searchDao.insertSearchItems(searchItems)
            }
        }
    )

    fun getExactSearchItem(
        pair: Pair<String, SearchType>,
    ) = channelFlow<UiState<List<SearchItem>>> {
        if (pair.second == SearchType.NAME) {
            searchDao.getSearchItemsByName(pair.first).collect { send(UiState.Success(it)) }
        } else {
            searchDao.getSearchItemsByTicker(pair.first).collect { send(UiState.Success(it)) }
        }
    }
    /*

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
     */


    fun getOhlcv(
        id: String,
        shouldFetch: Boolean
    ) = networkBoundResource(
        query = {
            ohlcvsDao.getOhlcvs(id)
        },
        fetch = {
            val ohlcvsDay = api.getOhlcvs(id, "1HRS", 24)
            val ohlcvsWeek = api.getOhlcvs(id, "2HRS", 84)
            val ohlcvsMonth = api.getOhlcvs(id, "8HRS", 90)
            val ohlcvsQuarter = api.getOhlcvs(id, "1DAY", 90)
            val ohlcvsYear = api.getOhlcvs(id, "5DAY", 73)
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
        },
        shouldFetch = { shouldFetch }
    )

/*
    fun getExactAsset(
        pair: Pair<String, SearchType>,
    ) = channelFlow<UiState<List<DBAsset>>> {
        if (pair.second == SearchType.NAME) {
            assetDao.getAssetsByName(pair.first).collect { send(UiState.Success(it)) }
        } else {
            assetDao.getAssetsByTicker(pair.first).collect { send(UiState.Success(it)) }
        }
    }

 */
}
