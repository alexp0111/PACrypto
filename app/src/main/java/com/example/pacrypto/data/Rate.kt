package com.example.pacrypto.data

/**
 * Contains info about rate of coin at time
 * */
data class Rate(
    val asset_id_quote: String,
    val rate: Double,
    val time: String
)