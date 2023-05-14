package com.example.pacrypto.data.room.rates

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DBListRates::class], version = 4)
@TypeConverters(RateConverters::class)
abstract class RateDatabase : RoomDatabase() {

    abstract fun rateDao(): RateDao
}