package com.example.pacrypto.data.api_data

import com.example.pacrypto.data.room.rates.Rate

data class ApiListRates(
    val asset_id_base: String,
    val rates: List<Rate>
)