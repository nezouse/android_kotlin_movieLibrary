package com.movielibrary.ui.movieDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.movielibrary.database.CommentEntity
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.Repository
import com.movielibrary.database.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.LinkedList

class MovieDetailsViewModel(val repository: Repository, val movieId: Int) :
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
            user = getUser()
            if(user.email.isNotEmpty()){
                checkIfLiked()
                checkIfRated()
            }
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
                    val favouriteMovies = LinkedList(user.favouriteMovies)
                    if (!liked.value!!) {
                        favouriteMovies.add(movie.value?.id)
                        repository.updateFavouriteMovies(user.id, favouriteMovies)
                        liked.postValue(true)
                    } else {
                        favouriteMovies.remove(movie.value?.id)
                        repository.updateFavouriteMovies(user.id, favouriteMovies)
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
                    val ratedMovies = user.ratedMovies
                    val correctRating = userRating * 2
                    ratedMovies[movie.value?.id!!.toString()] = correctRating

                    repository.updateRatedMovies(user.id, ratedMovies)
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
                    val ratedMovies = user.ratedMovies
                    ratedMovies.remove(movie.value?.id.toString())

                    repository.updateRatedMovies(user.id, ratedMovies)
                    rating.postValue(null)
                }
            } catch (e: Exception) {
                Log.i("MOVIES/REMOVE_RATING", e.toString())
            }
        }
    }

    private fun checkIfLiked() {
        val favouriteMovies = LinkedList(user.favouriteMovies)
        if (favouriteMovies.contains(movie.value?.id)) {
            liked.postValue(true)
        }
    }

    private fun checkIfRated() {
        val ratedMovies = user.ratedMovies
        if (ratedMovies.containsKey(movie.value?.id.toString())) {
            val rating = user.ratedMovies[movie.value?.id.toString()]
            this.rating.postValue(rating)
        }
    }

    private suspend fun getUser(): UserEntity {
        FirebaseAuth.getInstance().currentUser?.let {
            return repository.getUser(it.uid)[0]
        }
        return UserEntity()
    }
}
