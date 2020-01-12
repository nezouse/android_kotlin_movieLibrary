package com.movielibrary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "ABB",

    val overview: String = "",

    val popularity: Double = 0.0,

    @ColumnInfo(name = "poster_path")
    val posterPath: String = "",

    val rating: Double = 0.0,

    val releaseDate: String = ""
)