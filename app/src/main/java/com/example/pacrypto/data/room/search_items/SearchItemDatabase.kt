package com.example.pacrypto.data.room.search_items

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pacrypto.data.SearchItem

@Database(entities = [SearchItem::class], version = 1)
abstract class SearchItemDatabase : RoomDatabase() {

    abstract fun searchDao(): SearchDao
}