package com.example.pacrypto.di

import android.app.Application
import androidx.room.Room
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.ohlcvs.OhlcvsDatabase
import com.example.pacrypto.data.room.search_items.SearchItemDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(CoinApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCoinApi(retrofit: Retrofit): CoinApi =
        retrofit.create(CoinApi::class.java)

    @Provides
    @Singleton
    fun provideOhlcvsDatabase(app: Application): OhlcvsDatabase =
        Room.databaseBuilder(app, OhlcvsDatabase::class.java, "ohlcvs_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSearchItemDatabase(app: Application): SearchItemDatabase =
        Room.databaseBuilder(app, SearchItemDatabase::class.java, "search_database")
            .fallbackToDestructiveMigration()
            .build()
}