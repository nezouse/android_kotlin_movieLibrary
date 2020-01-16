package com.movielibrary.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.UUID

@Entity(tableName = "comments_table")
data class CommentEntity(
    var title: String = "",
    var body: String = "",
    val movieId: Int = -1,
    var userEmail: String = "",
    var date: Timestamp = Timestamp.now(),
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)

