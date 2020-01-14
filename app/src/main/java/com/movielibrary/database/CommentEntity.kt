package com.movielibrary.database

import java.util.UUID

data class CommentEntity(
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val movieId: Int = -1,
    val id: String = UUID.randomUUID().toString()
)