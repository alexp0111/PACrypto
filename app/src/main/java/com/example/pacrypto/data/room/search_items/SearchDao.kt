package com.example.pacrypto.data.room.search_items

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pacrypto.data.SearchItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("SELECT * FROM search_items")
    fun getAllSearchItems(): Flow<List<SearchItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchItems(items: List<SearchItem>)

    @Query("DELETE FROM search_items")
    suspend fun deleteAllSearchItems()

    //

    @Query("SELECT * FROM search_items WHERE ticker LIKE '%' || :ticker || '%'")
    fun getSearchItemsByTicker(ticker: String): Flow<List<SearchItem>>

    @Query("SELECT * FROM search_items WHERE name LIKE '%' || :name || '%'")
    fun getSearchItemsByName(name: String): Flow<List<SearchItem>>

    //
    @Query("SELECT * FROM search_items WHERE ticker IN (:list)")
    fun getFavs(list: List<String>): Flow<List<SearchItem>>

}