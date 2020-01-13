package com.movielibrary.database

import com.google.firebase.auth.FirebaseAuth

data class UserEntity(
    val email: String,
    val favouriteMovies: List<Int> = emptyList()
) {
    val id = FirebaseAuth.getInstance().currentUser?.uid
}


