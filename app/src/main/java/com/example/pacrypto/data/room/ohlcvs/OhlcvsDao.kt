package com.example.pacrypto.data.room.ohlcvs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface OhlcvsDao {
    @Query("SELECT * FROM ohlcvs WHERE type=:type")
    fun getOhlcvs(type: String): Flow<DBOhlcvs>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOhlcvs(ohlcvs: DBOhlcvs)

    @Query("DELETE FROM ohlcvs WHERE type=:type")
    suspend fun deleteOhlcvs(type: String)
}