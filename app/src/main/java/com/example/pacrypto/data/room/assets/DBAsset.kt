package com.example.pacrypto.data.room.assets

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class DBAsset(
    @PrimaryKey val asset_id: String,
    val data_end: String?,
    val data_start: String?,
    val data_trade_end: String?,
    val data_trade_start: String?,
    val name: String?,
    val price_usd: Double?,
    val type_is_crypto: Int?,
    val data_symbols_count: Int?,
) {

}