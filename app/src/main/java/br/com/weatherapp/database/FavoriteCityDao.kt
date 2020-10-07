package br.com.weatherapp.database

import androidx.room.*
import br.com.weatherapp.entity.FavoriteCity


@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM TB_CITY")
    fun getFavoriteCities() : List<FavoriteCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(favoriteCity: FavoriteCity)

    @Delete
    fun remove(favoriteCity: FavoriteCity)


    @Query("SELECT * FROM TB_CITY WHERE id = :id")
    fun selectById(id: Int): FavoriteCity
}