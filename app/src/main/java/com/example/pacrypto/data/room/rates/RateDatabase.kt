package com.example.pacrypto.data.room.rates

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pacrypto.data.room.Converters

@Database(entities = [DBListRates::class], version = 3)
@TypeConverters(Converters::class)
abstract class RateDatabase : RoomDatabase() {

    abstract fun rateDao(): RateDao
}