package com.example.pacrypto.data.room.assets

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DBAsset::class], version = 3)
abstract class AssetDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao
}