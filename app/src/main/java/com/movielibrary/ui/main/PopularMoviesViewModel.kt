package com.movielibrary.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.MoviesDao
import com.movielibrary.network.MovieApi
import com.movielibrary.network.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PopularMoviesViewModel(
    val database: MoviesDao, application: Application
) : AndroidViewModel(application) {
    var popularMoviesList: LiveData<List<MovieEntity>> = database.getPopularMovies()


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        coroutineScope.launch {
            val getResponseDeferred = MovieApi.retrofitService.getPopularMoviesAsync()
            val movieList = getResponseDeferred.await().movieList
            Log.i("MOVIES", "Amount of popular films " + movieList.size.toString())
            Log.i("MOVIES", popularMoviesList.value.toString())
            database.insertPopularMovies(*movieList.toEntity())
        }
    }
}
