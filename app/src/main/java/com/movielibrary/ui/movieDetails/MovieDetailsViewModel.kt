package com.movielibrary.ui.movieDetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.MoviesDao
import com.movielibrary.database.RecentlyViewedMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    val database: MoviesDao,
    application: Application
) : AndroidViewModel(application) {
    var movie = MutableLiveData<MovieEntity>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun addRecentlyViewedMovie(id: Int) {
        coroutineScope.launch {
            try {
                val recentRank = database.getMostRecentViewedMovieRank()
                database.insertRecentMovie(RecentlyViewedMovie(movieId = id, recentRank = recentRank + 1))
            } catch (e: Exception) {
                Log.i("MOVIES/RECENT", e.toString())
            }
        }
    }
}
