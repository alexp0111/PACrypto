package com.example.pacrypto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pacrypto.util.DatabaseNames

/**
 * Item that holds all necessary information for cards in search
 *
 * It contains two rates in USD & RUB
 * and also info about percents per day & updateTime
 * */
@Entity(tableName = DatabaseNames.SEARCH_ENTITY)
data class SearchItem(
    @PrimaryKey var ticker: String = "",
    var name: String? = "",
    var rateCurrentUSD: Double? = null,
    var rateCurrentRUB: Double? = null,
    var percents: String? = null,
    var timeUpdate: String? = null,
) {
    override fun toString(): String {
        return "$ticker $name $rateCurrentUSD $rateCurrentRUB $percents $timeUpdate"
    }
}