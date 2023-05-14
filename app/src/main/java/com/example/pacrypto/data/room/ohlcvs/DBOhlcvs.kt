package com.example.pacrypto.data.room.ohlcvs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ohlcvs")
data class DBOhlcvs(
    @PrimaryKey val type: String,
    val periods: List<List<DBOhlcvsItem>>
)