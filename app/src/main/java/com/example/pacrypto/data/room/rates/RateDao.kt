package com.example.pacrypto.data.room.rates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RateDao {

    // Actual usd
    @Query("SELECT * FROM rates WHERE type='usd_act'")
    fun getAllUSDRatesAct(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUSDRatesAct(rates: DBListRates)

    @Query("DELETE FROM rates WHERE type='usd_act'")
    suspend fun deleteAllUSDRatesAct()

    // Previous USD
    @Query("SELECT * FROM rates WHERE type='usd_prv'")
    fun getAllUSDRatesPrv(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUSDRatesPrv(rates: DBListRates)

    @Query("DELETE FROM rates WHERE type='usd_prv'")
    suspend fun deleteAllUSDRatesPrv()

    // Actual RUB
    @Query("SELECT * FROM rates WHERE type='rub_act'")
    fun getAllRUBRatesAct(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRUBRatesAct(rates: DBListRates)

    @Query("DELETE FROM rates WHERE type='rub_act'")
    suspend fun deleteAllRUBRatesAct()

    // Previous RUB
    @Query("SELECT * FROM rates WHERE type='rub_prv'")
    fun getAllRUBRatesPrv(): Flow<DBListRates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRUBRatesPrv(rates: DBListRates)

    @Query("DELETE FROM rates WHERE type='rub_prv'")
    suspend fun deleteAllRUBRatesPrv()

}