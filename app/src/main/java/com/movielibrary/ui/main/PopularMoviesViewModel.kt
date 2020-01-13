package com.movielibrary.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.MoviesDao
import com.movielibrary.database.toPopularMovie
import com.movielibrary.network.MovieApi
import com.movielibrary.network.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PopularMoviesViewModel(
    val database: MoviesDao,
    application: Application
) : AndroidViewModel(application) {
    var popularMoviesList: LiveData<List<MovieEntity>> = database.getPopularMovies()
    private val _navigateToDetailView = MutableLiveData<MovieEntity>()
    val navigateToDetailView: LiveData<MovieEntity>
        get() = _navigateToDetailView

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        getPopularMovies()
    }

    fun onMovieClicked(movie: MovieEntity) {
        _navigateToDetailView.value = movie
    }

    fun onMovieNavigated() {
        _navigateToDetailView.value = null
    }

    private fun getPopularMovies() {
        coroutineScope.launch {
            try {
                val getResponseDeferred = MovieApi.retrofitService.getPopularMoviesAsync()
                val movieList = getResponseDeferred.await().movieList

                val movieEntityList = movieList.toEntity()
                database.insertMovies(*movieEntityList.toTypedArray())
                database.insertPopularMovies(*movieEntityList.toPopularMovie().toTypedArray())
            } catch (e: Exception) {
                Log.i("MOVIES/EXCEPTION", e.toString())
            }
        }
    }
}
