package com.movielibrary.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Query("SELECT * FROM popular_movies_table ORDER BY popularity")
    fun getPopularMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM popular_movies_table ORDER BY popularity")
    fun searchMovies(): LiveData<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPopularMovies(vararg movies: MovieEntity)


}