package com.movielibrary.ui.movieDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.movielibrary.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    val repository: Repository,
    val movieId: Int,
    val roomDao: MoviesDao
) :
    ViewModel() {
    var movie = MutableLiveData<MovieEntity>()
    var liked = MutableLiveData<Boolean>(false)
    var rating = MutableLiveData<Float?>(null)
    lateinit var user: UserEntity

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    var commentsList: LiveData<List<CommentEntity>> = repository.getMovieComments(movieId)
    val commentsListener = repository.subscribeToComments(movieId)

    init {
        coroutineScope.launch {
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                checkIfLiked(it)
                checkIfRated(it)
            }
            user = getUser()
        }
    }

    fun addRecentlyViewedMovie(id: Int) {
        repository.addRecentlyViewedMovie(id)
    }

    fun insertComment(comment: CommentEntity) {
        repository.insertUserComment(comment)
    }

    fun addToFavourite() {
        coroutineScope.launch {
            try {
                if (user.email.isNotEmpty()) {
                    val likedMovie = LikedMovie(movieId, user.id)
                    if (!liked.value!!) {
                        roomDao.insertLikedMovie(likedMovie)
                        liked.postValue(true)
                    } else {
                        roomDao.deleteLikedMovie(likedMovie)
                        liked.postValue(false)
                    }
                }
            } catch (e: Exception) {
                Log.i("MOVIES/FAVOURITE", e.toString())
            }
        }
    }

    fun rateMovie(userRating: Float) {
        coroutineScope.launch {
            try {
                if (user.email.isNotEmpty()) {
                    val correctRating = userRating * 2

                    roomDao.insertRating(RatedMovie(movieId, user.id, correctRating))
                    rating.postValue(correctRating)
                }
            } catch (e: Exception) {
                Log.i("MOVIES/RATING", e.toString())
            }
        }
    }

    fun removeRating() {
        coroutineScope.launch {
            try {
                if (user.email.isNotEmpty()) {
                    roomDao.deleteRatedMovie(movieId, user.id)
                    rating.postValue(null)
                }
            } catch (e: Exception) {
                Log.i("MOVIES/REMOVE_RATING", e.toString())
            }
        }
    }

    private fun checkIfLiked(userId: String) {
        roomDao.getLikedMovie(movieId, userId)
            .firstOrNull()?.let {
                liked.postValue(true)
            }
    }

    private fun checkIfRated(userId: String) {
        roomDao.getRatingForMovie(movieId, userId)
            .firstOrNull()?.let {
                rating.postValue(it)
            }
    }

    private suspend fun getUser(): UserEntity {
        FirebaseAuth.getInstance().currentUser?.let {
            return repository.getUser(it.uid)[0]
        }
        return UserEntity()
    }
}
