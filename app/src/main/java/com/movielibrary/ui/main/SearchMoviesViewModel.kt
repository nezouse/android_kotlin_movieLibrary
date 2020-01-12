package com.movielibrary.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.movielibrary.database.MoviesDao
import com.movielibrary.network.MovieApi
import com.movielibrary.network.SimpleMovie
import com.movielibrary.network.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchMoviesViewModel(
    val database: MoviesDao, application: Application
) : AndroidViewModel(application) {
//    var searchMoviesResult: LiveData<List<SimpleMovie>> = database.getPopularMovies()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        getSearchResult()
    }

    fun getSearchResult() {
        coroutineScope.launch {
            val getResponseDeferred = MovieApi.retrofitService.searchMoviesAsync("froze")
            val movieList = getResponseDeferred.await().movieList
            Log.i("MOVIES-QUERY", "Amount of found movies " + movieList.size.toString())
//            Log.i("MOVIES-QUERY", searchMoviesResult.value.toString())
//            searchMoviesResult.insertPopularMovies(*movieList.toEntity())
        }
    }
}