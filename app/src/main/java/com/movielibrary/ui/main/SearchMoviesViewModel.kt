package com.movielibrary.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.MoviesDao
import com.movielibrary.network.MovieApi
import com.movielibrary.network.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class SearchMoviesViewModel(
    val database: MoviesDao,
    application: Application
) : AndroidViewModel(application) {
    var searchMoviesList: LinkedList<MovieEntity> = LinkedList()
    lateinit var adapter: FragmentAdapter
    private val _navigateToDetailView = MutableLiveData<MovieEntity>()
    val navigateToDetailView: LiveData<MovieEntity>
        get() = _navigateToDetailView

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun onMovieClicked(movie: MovieEntity) {
        _navigateToDetailView.value = movie
    }

    fun onMovieNavigated() {
        _navigateToDetailView.value = null
    }

    fun getSearchResult(query: String?) {
        coroutineScope.launch {
            try {
                if (!query.isNullOrEmpty()) {
                    val getResponseDeferred = MovieApi.retrofitService.searchMoviesAsync(query)
                    val movieList = getResponseDeferred.await().movieList

                    database.insertMovies(*movieList.toEntity().toTypedArray())
                    val foundMovies = database.searchMovies("%$query%")

                    searchMoviesList.clear()
                    searchMoviesList.addAll(foundMovies)

                    notifyAdapter()
                }
            } catch (e: Exception) {
                Log.i("MOVIES/EXCEPTION", e.toString())
            }
        }
    }

    private fun notifyAdapter() {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            adapter.notifyDataSetChanged()
        }
    }
}