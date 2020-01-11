package com.movielibrary.ui.main

import java.util.*


data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val posterPath: String = "",
    val rating: Double = 0.0,
    val releaseDate: Date = Date()
)