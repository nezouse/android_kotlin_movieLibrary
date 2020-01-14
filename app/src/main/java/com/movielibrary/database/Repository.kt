package com.movielibrary.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Repository(private val firebaseDao: FirebaseDao, private val roomDao: MoviesDao) {
    private val TAG = "I/FIRESTORE"
    private var RepositoryJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + RepositoryJob)

    fun insertUser() {
        coroutineScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser!!.uid
                val email = currentUser.email ?: throw Exception("Failed to get email")
                val userEntity = UserEntity(id = currentUser.uid, email = email)
                firebaseDao.insertUser(userEntity)
                Log.i(TAG, "DocumentSnapshot written with ID: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document", e)
            }
        }
    }

    fun insertUserComment(comment: CommentEntity) {
        coroutineScope.launch {
            try {
                comment.userEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                firebaseDao.insertUserComment(comment)
                roomDao.insertComment(comment)
                Log.i(TAG, "DocumentSnapshot written with ID: ${comment.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document", e)
            }
        }
    }

    fun getMovieComments(id: Int): LiveData<List<CommentEntity>> {
        return roomDao.getCommentsForMovie(id)
    }

    fun subscribeToComments(id: Int): ListenerRegistration {
        Log.w(TAG, "Listener attached")
        return firebaseDao.getCommentsQuery(id).addSnapshotListener { values, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            coroutineScope.launch {
                values?.forEach {
                    roomDao.insertComment(it.toObject())
                }
            }
        }
    }

    fun detachSubscription(listener: ListenerRegistration) {
        Log.w(TAG, "Listener detached")
        listener.remove()
    }

    fun addRecentlyViewedMovie(id: Int) {
        coroutineScope.launch {
            try {
                val recentRank = roomDao.getMostRecentViewedMovieRank()
                roomDao.insertRecentMovie(
                    RecentlyViewedMovie(
                        movieId = id,
                        recentRank = recentRank + 1
                    )
                )
            } catch (e: Exception) {
                Log.i("MOVIES/RECENT", e.toString())
            }
        }
    }
}