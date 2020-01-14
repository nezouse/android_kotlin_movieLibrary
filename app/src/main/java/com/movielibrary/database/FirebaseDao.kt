package com.movielibrary.database

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseDao {
    private val db = Firebase.firestore

    fun getDao() = this

    suspend fun insertUser(user: UserEntity) {
        db.collection("users").document(user.id).set(user).await()
    }

    suspend fun insertUserComment(comment: CommentEntity) {
        db.collection("comments").document(comment.id).set(comment).await()
    }

    fun getCommentsQuery(movieId: Int): Query {
        return db.collection("comments").whereEqualTo("movieId", movieId)
    }

    suspend fun getMovieComments(movieId: Int): List<CommentEntity> {
        val snapshots = db.collection("comments").whereEqualTo("movieId", movieId).get().await()
        return snapshots.toObjects()
    }
}
