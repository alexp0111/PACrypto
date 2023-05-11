package com.example.pacrypto.di

import android.app.Application
import androidx.room.Room
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.data.room.CoinDatabase
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
    fun provideDatabase(app: Application): CoinDatabase =
        Room.databaseBuilder(app, CoinDatabase::class.java, "coin_database")
            .fallbackToDestructiveMigration()
            .build()
}