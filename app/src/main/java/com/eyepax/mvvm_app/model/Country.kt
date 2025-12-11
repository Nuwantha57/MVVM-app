package com.eyepax.mvvm_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "countries")
data class Country(
    @PrimaryKey
    @SerializedName("cca3")
    val code: String, // 3-letter code like "USA"

    @SerializedName("name")
    val name: CountryName,

    @SerializedName("capital")
    val capital: List<String>? = null,

    @SerializedName("region")
    val region: String? = null,

    @SerializedName("subregion")
    val subregion: String? = null,

    @SerializedName("population")
    val population: Long? = null,

    @SerializedName("area")
    val area: Double? = null
)

data class CountryName(
    @SerializedName("common")
    val common: String,

    @SerializedName("official")
    val official: String
)
