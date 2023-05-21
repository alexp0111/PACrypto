package com.example.pacrypto.data.api_data

import com.example.pacrypto.data.Rate

/**
 * Contains list of rates of all coins connected to id base (RUB or USD)
 * */
data class ApiListRates(
    val asset_id_base: String,
    val rates: List<Rate>
) {
    override fun toString(): String {
        return "ApiListRates(asset_id_base='$asset_id_base', rates=$rates)"
    }
}