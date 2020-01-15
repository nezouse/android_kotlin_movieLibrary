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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    var commentsList: LiveData<List<CommentEntity>> = repository.getMovieComments(movieId)
    val commentsListener = repository.subscribeToComments(movieId)

    fun initIcons() {
        coroutineScope.launch {
            val user = getUser()
            checkIfLiked(user)
            checkIfRated(user)
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
                val user = getUser()
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
            } catch (e: Exception) {
                Log.i("MOVIES/FAVOURITE", e.toString())
            }
        }
    }

    fun rateMovie(rating: Float) {
        coroutineScope.launch {
            try {
                val user = getUser()
                val ratedMovies = user.ratedMovies
                val correctRating = rating * 2
                ratedMovies[movie.value?.id!!.toString()] = correctRating

                repository.updateRatedMovies(user.id, ratedMovies)
                this@MovieDetailsViewModel.rating.postValue(correctRating)
            } catch (e: Exception) {
                Log.i("MOVIES/RATING", e.toString())
            }
        }
    }

    fun removeRating() {
        coroutineScope.launch {
            try {
                val user = getUser()
                val ratedMovies = user.ratedMovies
                ratedMovies.remove(movie.value?.id.toString())

                repository.updateRatedMovies(user.id, ratedMovies)
                rating.postValue(null)
            } catch (e: Exception) {
                Log.i("MOVIES/REMOVE_RATING", e.toString())
            }
        }
    }

    fun checkIfLiked(user: UserEntity) {
        val favouriteMovies = LinkedList(user.favouriteMovies)
        if (favouriteMovies.contains(movie.value?.id)) {
            liked.postValue(true)
        }
    }

    fun checkIfRated(user: UserEntity) {
        val ratedMovies = user.ratedMovies
        if (ratedMovies.containsKey(movie.value?.id.toString())) {
            val rating = user.ratedMovies[movie.value?.id.toString()]
            this.rating.postValue(rating)
        }
    }

    suspend fun getUser(): UserEntity {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        return repository.getUser(userId)[0]
    }
}
