package com.example.pacrypto.util

import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.room.DBAsset

fun List<ApiAsset>.asDBType(): List<DBAsset> {
    val dBList = mutableListOf<DBAsset>()
    this.forEach {
        dBList.add(
            DBAsset(
                asset_id = it.asset_id,
                data_end = it.data_end,
                data_start = it.data_start,
                data_trade_end = it.data_trade_end,
                data_trade_start = it.data_trade_start,
                name = it.name,
                price_usd = it.price_usd,
                type_is_crypto = it.type_is_crypto,
                data_symbols_count = it.data_symbols_count,
            )
        )
    }
    return dBList
}