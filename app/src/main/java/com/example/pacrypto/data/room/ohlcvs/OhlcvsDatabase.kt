package com.example.pacrypto.data.room.ohlcvs

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pacrypto.data.room.rates.DBListRates
import com.example.pacrypto.data.room.rates.RateConverters
import com.example.pacrypto.data.room.rates.RateDao

@Database(entities = [DBOhlcvs::class], version = 1)
@TypeConverters(OhlcvsConverters::class)
abstract class OhlcvsDatabase : RoomDatabase() {

    abstract fun ohlcvsDao(): OhlcvsDao
}