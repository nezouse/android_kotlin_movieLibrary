package com.movielibrary.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_movies_table")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val rating: Double,
    val releaseDate: String
)