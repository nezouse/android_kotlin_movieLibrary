package com.movielibrary.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies_table AS mv INNER JOIN popular_movies_table AS pmt ON mv.id=pmt.id ORDER BY popularity DESC")
    fun getPopularMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies_table WHERE title LIKE :query ORDER BY popularity DESC")
    fun searchMovies(query: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPopularMovies(vararg movies: PopularMovieEntity): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(vararg movies: MovieEntity)
}