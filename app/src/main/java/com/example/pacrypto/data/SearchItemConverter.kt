package com.example.pacrypto.data

import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.api_data.ApiListRates
import kotlin.math.abs


/**
 * Converter that transform few lists of info two one list of search items
 * */
object SearchItemConverter {


    private fun find(ratesUSDListActual: ApiListRates, ticker: String): Rate? {
        ratesUSDListActual.rates.forEach {
            if (it.asset_id_quote == ticker) return it
        }
        return null
    }

    fun convert(
        assetList: List<ApiAsset>,
        ratesUSDListActual: ApiListRates,
        ratesUSDListPrevious: ApiListRates,
        ratesRUBListActual: ApiListRates,
    ): List<SearchItem> {
        val searchItemList = mutableListOf<SearchItem>()
        assetList.forEach {
            val ticker = it.asset_id
            val name = it.name


            val rateCurrentUSDObject = find(ratesUSDListActual, ticker)
            val ratePreviousUSDObject = find(ratesUSDListPrevious, ticker)
            val rateCurrentRUBObject = find(ratesRUBListActual, ticker)


            if (rateCurrentUSDObject != null && rateCurrentUSDObject.rate != 0.0
                && ratePreviousUSDObject != null && ratePreviousUSDObject.rate != 0.0
                && rateCurrentRUBObject != null && rateCurrentRUBObject.rate != 0.0
            ) {

                val rateCurrentUSD = 1.0 / rateCurrentUSDObject.rate
                val ratePreviousUSD = 1.0 / ratePreviousUSDObject.rate
                val rateCurrentRUB = 1.0 / rateCurrentRUBObject.rate


                val percentsValue =
                    (rateCurrentUSD / ratePreviousUSD) * 100.0 - 100.0
                val percents = if (percentsValue >= 0.0) {
                    "+" + String.format("%.2f", abs(percentsValue)) + "%"
                } else {
                    "-" + String.format("%.2f", abs(percentsValue)) + "%"
                }


                val time = buildString {
                    append(rateCurrentUSDObject.time.substring(0, 10))
                    append("\n")
                    append(rateCurrentUSDObject.time.substring(11, 19))
                }

                searchItemList.add(
                    SearchItem(ticker, name, rateCurrentUSD, rateCurrentRUB, percents, time)
                )
            } else {
                searchItemList.add(
                    SearchItem(ticker, name)
                )
            }
        }

        return searchItemList
    }

}
