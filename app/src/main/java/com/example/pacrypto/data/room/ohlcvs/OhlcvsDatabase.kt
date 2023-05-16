package com.example.pacrypto.data.room.ohlcvs

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DBOhlcvs::class], version = 1)
@TypeConverters(OhlcvsConverters::class)
abstract class OhlcvsDatabase : RoomDatabase() {

    abstract fun ohlcvsDao(): OhlcvsDao
}