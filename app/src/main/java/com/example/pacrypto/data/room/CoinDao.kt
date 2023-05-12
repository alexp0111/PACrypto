package com.example.pacrypto.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<DBAsset>>

    @Query("SELECT * FROM assets WHERE asset_id LIKE '%' || :ticker || '%' ORDER BY price_usd DESC")
    fun getAssetsByTicker(ticker: String): Flow<List<DBAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(restaurants: List<DBAsset>)

    @Query("DELETE FROM assets")
    suspend fun deleteAllAssets()

}