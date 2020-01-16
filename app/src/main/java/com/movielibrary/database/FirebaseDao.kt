package com.movielibrary.database

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseDao {
    private val db = Firebase.firestore

    suspend fun insertUser(user: UserEntity) {
        db.collection("users").document(user.id).set(user).await()
    }

    suspend fun insertUserComment(comment: CommentEntity) {
        db.collection("comments").document(comment.id).set(comment).await()
    }

    fun getCommentsQuery(movieId: Int): Query {
        return db.collection("comments").whereEqualTo("movieId", movieId)
    }

    suspend fun deleteComment(commentId: String) {
        db.collection("comments").document(commentId).delete().await()
    }

    suspend fun editComment(comment: CommentEntity) {
        db.collection("comments")
            .document(comment.id)
            .update(
                "title", comment.title,
                "body", comment.body
            ).await()
    }

    suspend fun getUser(userId: String): List<UserEntity> {
        val snapshots = db.collection("users").whereEqualTo("id", userId).get().await()
        return snapshots.toObjects()
    }

    suspend fun updateFavouriteMovies(userId: String, favouriteMovies: List<Int>) {
        db.collection("users").document(userId).update("favouriteMovies", favouriteMovies).await()
    }

    suspend fun updateRatedMovies(userId: String, ratedMovies: HashMap<String, Float>) {
        db.collection("users").document(userId).update("ratedMovies", ratedMovies).await()
    }
}
