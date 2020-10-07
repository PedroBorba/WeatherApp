package br.com.weatherapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FindResult(
    @SerializedName("list")
    val items: List<City>)

data class City(
    val id : Int = 0,
    val name : String = "",
    @SerializedName("weather")
    val weatherList : List<Weather>,
    val main : Main,
    val wind: Wind,
    val clouds: Clouds,
    @SerializedName("sys")
    val pais: Pais,
    var favorite: Boolean = false
)

data class Weather(
    val description : String = "",
    val icon : String = ""
)

data class Main(
    val temp : Float,
    val pressure : Float,
    val humidity: Float
)

data class Wind (
    val speed: Float
)


data class Pais (
    @SerializedName("country")
    val sigla : String
)


data class Clouds (
    val all: Float
)

@Entity(tableName = "tb_city")
data class FavoriteCity(
    @PrimaryKey
    val id : Int = 0,
    val name : String = "",
    val favorite: Boolean = false
)