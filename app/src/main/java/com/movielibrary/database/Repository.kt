package com.movielibrary.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object Repository {
    private const val TAG = "I/FIRESTORE"
    private var RepositoryJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + RepositoryJob)
    private val dao = FirebaseDao.getDao()

    fun insertUser() {
        coroutineScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser!!.uid
                val email = currentUser.email ?: throw Exception("Failed to get email")
                val userEntity = UserEntity(id = currentUser.uid, email = email)
                dao.insertUser(userEntity)
                Log.i(TAG, "DocumentSnapshot written with ID: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document", e)
            }
        }
    }

    fun insertUserComment(comment: CommentEntity) {
        coroutineScope.launch {
            try {
                dao.insertUserComment(comment)
                Log.i(TAG, "DocumentSnapshot written with ID: ${comment.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document", e)
            }
        }
    }

    suspend fun getMovieComments(movieId: Int): List<CommentEntity> {
        return try {
            val comments: List<CommentEntity> = dao.getMovieComments(movieId)
            Log.i(TAG, "DocumentSnapshots pulled correctly.")
            comments
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents", e)
            emptyList()
        }
    }
}