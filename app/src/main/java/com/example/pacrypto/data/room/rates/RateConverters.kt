package com.example.pacrypto.data.room.rates

import androidx.room.TypeConverter
import com.example.pacrypto.data.room.rates.Rate
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class RateConverters {
    @TypeConverter
    fun jsonToList(value: String?): List<Rate> {
        val listType: Type = object : TypeToken<List<Rate?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: List<Rate?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}