package com.eyepax.mvvm_app.database

import androidx.room.TypeConverter
import com.eyepax.mvvm_app.model.CountryName
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCountryName(value: CountryName): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCountryName(value: String): CountryName {
        val type = object : TypeToken<CountryName>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
