package com.movielibrary;

import android.app.Activity;
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CursorAdapter
import com.movielibrary.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchableActivity : Activity() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

//    private val CursorAdapter adapter<


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.search)

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchMovies(query)
            }
        }
    }

    fun searchMovies(query: String) {
        coroutineScope.launch {
            val getResponseDeferred =
                MovieApi.retrofitService.searchMoviesAsync(query)
            val foundMovies = getResponseDeferred.await().movieList
            Log.i("MOVIE QUERY", foundMovies.toString())
        }
    }
}
