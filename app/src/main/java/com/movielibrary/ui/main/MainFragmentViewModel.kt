package com.movielibrary.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.movielibrary.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainFragmentViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getPopularMovies() {
        coroutineScope.launch {
            val getResponseDeferred =
                MovieApi.retrofitService.getPopularMoviesAsync()
            val movieList = getResponseDeferred.await().movieList
            movieList.forEach {
                Log.i("POPULAR MOVIE", it.toString())
            }
        }
    }

    fun getMovieDetails() {
        coroutineScope.launch {
            val getResponseDeferred =
                MovieApi.retrofitService.getMovieDetailsAsync(419704)
            val movieDetails = getResponseDeferred.await()
            Log.i("MOVIE DETAIL", movieDetails.toString())
        }
    }
}
