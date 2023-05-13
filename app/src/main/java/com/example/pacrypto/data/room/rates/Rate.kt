package com.example.pacrypto.data.room.rates

data class Rate(
    val asset_id_quote: String,
    val rate: Double,
    val time: String
)