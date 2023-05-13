package com.example.pacrypto.data.room.rates

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class DBListRates(
    @PrimaryKey val asset_id_base: String,
    val rates: List<Rate>
)