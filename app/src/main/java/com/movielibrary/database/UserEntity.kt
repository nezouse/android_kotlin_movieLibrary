package com.movielibrary.database

data class UserEntity(
    val email: String,
    val favouriteMovies: List<Int> = emptyList(),
    val id: String
)


