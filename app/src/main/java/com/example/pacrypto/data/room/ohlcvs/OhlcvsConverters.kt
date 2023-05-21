package com.example.pacrypto.data.room.ohlcvs

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Converters that are necessary for recognizing ohlcvs
 * */
class OhlcvsConverters {
    @TypeConverter
    fun jsonToList(value: String?): List<List<DBOhlcvsItem>> {
        val listType: Type = object : TypeToken<List<List<DBOhlcvsItem?>?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: List<List<DBOhlcvsItem?>?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}