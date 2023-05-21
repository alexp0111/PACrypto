package com.example.pacrypto.data.room.ohlcvs

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pacrypto.util.DatabaseNames

/**
 * Contains info about ohlcv data for periods (day, week, month, quarter, year, all)
 * */
@Entity(tableName = DatabaseNames.OHLCVS_ENTITY)
data class DBOhlcvs(
    @PrimaryKey val type: String,
    val periods: List<List<DBOhlcvsItem>>
)