package com.example.pacrypto.data

import androidx.room.withTransaction
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.ohlcvs.OhlcvsDatabase
import com.example.pacrypto.data.room.search_items.SearchItemDatabase
import com.example.pacrypto.util.*
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject


/**
 *
 * Repository of MVVM architecture that holds operations connected to data
 *
 * if request is connected with api and should pe stored - networkBoundResource called
 * otherwise - it is the simple request to database transferred to channelFlow
 *
 * */
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


    fun getOhlcv(
        id: String,
        shouldFetch: Boolean
    ) = networkBoundResource(
        query = {
            ohlcvsDao.getOhlcvs(id)
        },
        fetch = {
            val ohlcvsDay = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_1HRS, OhlcvsInfo.PERIOD_LIMIT_1HRS)
            val ohlcvsWeek = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_2HRS, OhlcvsInfo.PERIOD_LIMIT_2HRS)
            val ohlcvsMonth = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_8HRS, OhlcvsInfo.PERIOD_LIMIT_8HRS)
            val ohlcvsQuarter = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_1DAY, OhlcvsInfo.PERIOD_LIMIT_1DAY)
            val ohlcvsYear = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_5DAY, OhlcvsInfo.PERIOD_LIMIT_5DAY)
            val ohlcvsAll = api.getOhlcvs(id, OhlcvsInfo.PERIOD_ID_1MTH)

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

    fun getFavs(
        list: List<String>,
    ) = channelFlow<UiState<List<SearchItem>>> {
        searchDao.getFavs(list).collect { send(UiState.Success(it)) }
    }
}
