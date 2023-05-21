package com.example.pacrypto.data.api_data

/**
 * Contains info about asset, that API provides
 *
 * asset_id is unique and stands for ticker in coins
 * */
data class ApiAsset(
    val asset_id: String,
    val data_end: String,
    val data_orderbook_end: String,
    val data_orderbook_start: String,
    val data_quote_end: String,
    val data_quote_start: String,
    val data_start: String,
    val data_symbols_count: Int,
    val data_trade_end: String,
    val data_trade_start: String,
    val name: String,
    val price_usd: Double,
    val type_is_crypto: Int,
    val volume_1day_usd: Double,
    val volume_1hrs_usd: Double,
    val volume_1mth_usd: Double
) {
    override fun toString(): String {
        return "ApiAsset(asset_id='$asset_id', data_end='$data_end', data_orderbook_end='$data_orderbook_end', data_orderbook_start='$data_orderbook_start', data_quote_end='$data_quote_end', data_quote_start='$data_quote_start', data_start='$data_start', data_symbols_count=$data_symbols_count, data_trade_end='$data_trade_end', data_trade_start='$data_trade_start', name='$name', price_usd=$price_usd, type_is_crypto=$type_is_crypto, volume_1day_usd=$volume_1day_usd, volume_1hrs_usd=$volume_1hrs_usd, volume_1mth_usd=$volume_1mth_usd)"
    }
}