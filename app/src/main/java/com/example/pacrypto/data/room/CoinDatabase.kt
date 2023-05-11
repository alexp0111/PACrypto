package com.example.pacrypto.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pacrypto.data.api_data.ApiAsset

@Database(entities = [DBAsset::class], version = 2)
abstract class CoinDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao
}