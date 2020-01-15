package com.movielibrary.database

data class UserEntity(
    val email: String = "",
    val favouriteMovies: List<Int> = emptyList(),
    val ratedMovies: HashMap<String, Float> = HashMap(),
    val id: String = ""
)


