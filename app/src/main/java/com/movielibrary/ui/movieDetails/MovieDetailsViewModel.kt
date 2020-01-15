package com.movielibrary.ui.movieDetails

import android.annotation.SuppressLint
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.movielibrary.R
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
    var liked2 = MutableLiveData<Boolean>(false)
    var rated2 = MutableLiveData<Boolean>(false)

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    var commentsList: LiveData<List<CommentEntity>> = repository.getMovieComments(movieId)
    val commentsListener = repository.subscribeToComments(movieId)

    fun initIcons(ratingView: ImageView, favouriteView: ImageView, ratingTextView: TextView) {
        coroutineScope.launch {
            val user = getUser()
            checkIfLiked(user)
            checkIfRated(user, ratingTextView)
        }
    }

    fun addRecentlyViewedMovie(id: Int) {
        repository.addRecentlyViewedMovie(id)
    }

    fun insertComment(comment: CommentEntity) {
        repository.insertUserComment(comment)
    }

    fun rateMovie(imageView: ImageView) {
        //TODO add movie rating
        imageView.setImageResource(R.drawable.star_blue)
    }

    fun addToFavourite() {
        coroutineScope.launch {
            try {
                val user = getUser()
                val favouriteMovies = LinkedList(user.favouriteMovies)
                if (!liked2.value!!) {
                    favouriteMovies.add(movie.value?.id)
                    repository.updateFavouriteMovies(user.id, favouriteMovies)
                    liked2.postValue(true)
                } else {
                    favouriteMovies.remove(movie.value?.id)
                    repository.updateFavouriteMovies(user.id, favouriteMovies)
                    liked2.postValue(false)
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
                ratedMovies[movie.value?.id!!.toString()] = rating * 2

                repository.updateRatedMovies(user.id, ratedMovies)
                rated = true
                imageView.setImageResource(R.drawable.star_blue)
                rated2.postValue( true)
            } catch (e: Exception) {
                Log.i("MOVIES/RATING", e.toString())
            }
        }
    }

    fun removeRating(textView: TextView) {
        coroutineScope.launch {
            try {
                val user = getUser()
                val ratedMovies = user.ratedMovies
                ratedMovies.remove(movie.value?.id.toString())

                repository.updateRatedMovies(user.id, ratedMovies)
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    textView.setText(R.string.rate_text)
                }
                rated2.postValue( false)
            } catch (e: Exception) {
                Log.i("MOVIES/REMOVE_RATING", e.toString())
            }
        }
    }

    fun checkIfLiked(user: UserEntity) {
        val favouriteMovies = LinkedList(user.favouriteMovies)
        if (favouriteMovies.contains(movie.value?.id)) {
            liked2.postValue(true)
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkIfRated(user: UserEntity, textView: TextView) {
        val ratedMovies = user.ratedMovies
        if (ratedMovies.containsKey(movie.value?.id.toString())) {
            rated2.postValue( true)
            val rating = user.ratedMovies[movie.value?.id.toString()]
            coroutineScope.launch(Dispatchers.Main.immediate) {
                textView.text = "$rating/10"
            }
        }
    }

    suspend fun getUser(): UserEntity {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        return repository.getUser(userId)[0]
    }
}
