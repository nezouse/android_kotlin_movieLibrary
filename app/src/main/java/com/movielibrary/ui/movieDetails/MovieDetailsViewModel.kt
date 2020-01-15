package com.movielibrary.ui.movieDetails

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.movielibrary.R
import com.movielibrary.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MovieDetailsViewModel(
    val database: MoviesDao,
    application: Application
) : AndroidViewModel(application) {
    var movie = MutableLiveData<MovieEntity>()
    var liked = false
    var rated = false

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun initIcons(ratingView: ImageView, favouriteView: ImageView, ratingTextView: TextView) {
        coroutineScope.launch {
            val user = getUser()
            checkIfLiked(user, favouriteView)
            checkIfRated(user, ratingView, ratingTextView)
        }
    }

    fun addRecentlyViewedMovie(id: Int) {
        coroutineScope.launch {
            try {
                val recentRank = database.getMostRecentViewedMovieRank()
                database.insertRecentMovie(
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

    fun addToFavourite(imageView: ImageView) {
        coroutineScope.launch {
            try {
                val user = getUser()
                val favouriteMovies = LinkedList(user.favouriteMovies)
                if (!liked) {
                    favouriteMovies.add(movie.value?.id)
                    Repository.updateFavouriteMovies(user.id, favouriteMovies)
                    imageView.setImageResource(R.drawable.favourite_red)
                } else {
                    favouriteMovies.remove(movie.value?.id)
                    Repository.updateFavouriteMovies(user.id, favouriteMovies)
                    imageView.setImageResource(R.drawable.favorite_border)
                }
                liked = !liked
            } catch (e: Exception) {
                Log.i("MOVIES/FAVOURITE", e.toString())
            }
        }

    }

    fun rateMovie(imageView: ImageView, rating: Float) {
        coroutineScope.launch {
            try {
                val user = getUser()
                val ratedMovies = user.ratedMovies
                ratedMovies[movie.value?.id!!.toString()] = rating * 2

                Repository.updateRatedMovies(user.id, ratedMovies)
                rated = true
                imageView.setImageResource(R.drawable.star_blue)
            } catch (e: Exception) {
                Log.i("MOVIES/RATING", e.toString())
            }
        }
    }

    fun removeRating(imageView: ImageView, textView: TextView) {
        coroutineScope.launch {
            try {
                val user = getUser()
                val ratedMovies = user.ratedMovies
                ratedMovies.remove(movie.value?.id.toString())

                Repository.updateRatedMovies(user.id, ratedMovies)
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    textView.setText(R.string.rate_text)
                }
                rated = false
                imageView.setImageResource(R.drawable.star_border)
            } catch (e: Exception) {
                Log.i("MOVIES/REMOVE_RATING", e.toString())
            }
        }
    }

    fun checkIfLiked(user: UserEntity, imageView: ImageView) {
        val favouriteMovies = LinkedList(user.favouriteMovies)
        if (favouriteMovies.contains(movie.value?.id)) {
            imageView.setImageResource(R.drawable.favourite_red)
            liked = true
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkIfRated(user: UserEntity, imageView: ImageView, textView: TextView) {
        val ratedMovies = user.ratedMovies
        if (ratedMovies.containsKey(movie.value?.id.toString())) {
            imageView.setImageResource(R.drawable.star_blue)
            rated = true
            val rating = user.ratedMovies[movie.value?.id.toString()]
            coroutineScope.launch(Dispatchers.Main.immediate) {
                textView.text = "$rating/10"
            }
        }
    }

    suspend fun getUser(): UserEntity {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        return Repository.getUser(userId)[0]
    }
}
