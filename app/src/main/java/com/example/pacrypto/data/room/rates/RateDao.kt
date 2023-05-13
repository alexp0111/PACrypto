package com.example.pacrypto.data.room.rates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RateDao {

    @Query("SELECT * FROM rates WHERE asset_id_base='USD'")
    fun getAllUSDRates(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUSDRates(rates: DBListRates)

    @Query("DELETE FROM rates WHERE asset_id_base='USD'")
    suspend fun deleteAllUSDRates()

    @Query("SELECT * FROM rates WHERE asset_id_base='RUB'")
    fun getAllRUBRates(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRUBRates(rates: DBListRates)

    @Query("DELETE FROM rates WHERE asset_id_base='RUB'")
    suspend fun deleteAllRUBRates()

}