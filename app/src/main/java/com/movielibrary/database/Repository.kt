package com.movielibrary.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

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

    fun deleteUserComment(commentId: String) {
        coroutineScope.launch {
            try {
                firebaseDao.deleteComment(commentId)
                Log.i(TAG, "Comment deleted with ID: $commentId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting comment", e)
            }
        }
    }

    fun editUserComment(comment: CommentEntity) {
        coroutineScope.launch {
            try {
                firebaseDao.editComment(comment)
                Log.i(TAG, "Comment edited with ID: ${comment.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error editing comment", e)
            }
        }
    }

    fun getMovieComments(id: Int): LiveData<List<CommentEntity>> {
        return roomDao.getCommentsForMovie(id)
    }

    fun subscribeToComments(movieId: Int): ListenerRegistration {
        Log.w(TAG, "Listener attached")
        return firebaseDao.getCommentsQuery(movieId).addSnapshotListener { values, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            coroutineScope.launch {
                values?.let { snapshotsFromFirestore ->
                    Log.w(TAG, "New snapshots from firestore")
                    val commentsInCache = roomDao.getCommentsForMovieSync(movieId)
                    val commentsFromFirestore = snapshotsFromFirestore.toObjects<CommentEntity>()
                    val union = commentsInCache + commentsFromFirestore
                    val itemsOnlyInCache = union.filter {
                        union.indexOf(it) == union.lastIndexOf(it) && commentsInCache.contains(it)
                    }

                    itemsOnlyInCache.forEach { roomDao.deleteComment(it) }
                    // Insert all from firestore because comments can be edited
                    roomDao.insertComment(*commentsFromFirestore.toTypedArray())
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

    suspend fun getUser(userId: String): List<UserEntity> {
        return try {
            val favMovies: List<UserEntity> = firebaseDao.getUser(userId)
            favMovies
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents - favourite movies", e)
            emptyList()
        }
    }

    fun updateFavouriteRoomMovies(userId: String){
        coroutineScope.launch {
            val user = firebaseDao.getUser(userId)
            if (user.isNotEmpty()){
                val favouriteMovies = user[0].favouriteMovies
                favouriteMovies.forEach {
                    roomDao.insertLikedMovie(LikedMovie(it, userId))
                }
            }
        }
    }

    fun updateFavouriteFirestoreMovies(userId: String){
        coroutineScope.launch {
            val likedMovies = roomDao.getAllLikedMovies(userId)
            firebaseDao.updateFavouriteMovies(userId, likedMovies)
        }
    }

    fun updateRoomRatings(userId: String){
        coroutineScope.launch {
            val user = firebaseDao.getUser(userId)
            if (user.isNotEmpty()){
                val ratedMovies = user[0].ratedMovies
                val roomRatings = LinkedList<RatedMovie>()

                ratedMovies.forEach {
                    roomRatings.add(RatedMovie(it.key.toInt(), userId, it.value))
                }
                roomDao.insertRating(*roomRatings.toTypedArray())
            }
        }
    }

    fun updateFirestoreRatings(userId: String){
        coroutineScope.launch {
            val ratedMovies = roomDao.getAllRatings(userId)
            val firebaseRatings = HashMap<String, Float>()
            ratedMovies.forEach {
                firebaseRatings[it.movieId.toString()] = it.rating
            }
            firebaseDao.updateRatedMovies(userId, firebaseRatings)
        }
    }
}