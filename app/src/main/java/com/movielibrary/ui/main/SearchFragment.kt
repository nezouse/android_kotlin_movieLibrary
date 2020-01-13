package com.movielibrary.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.movielibrary.R
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {

    lateinit var searchMoviesViewModel: SearchMoviesViewModel
    private lateinit var adapter: FragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SearchFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = MoviesDatabase.getInstance(application).moviesDao
        val viewModelFactory = SearchFragmentViewModelFactory(dataSource, application)

        searchMoviesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SearchMoviesViewModel::class.java)

        binding.searchFragmentViewModel = searchMoviesViewModel
        binding.lifecycleOwner = this

        connectAdapter(binding)

        setHasOptionsMenu(true)
        return binding.root
    }

    fun connectAdapter(binding: SearchFragmentBinding){
        adapter = FragmentAdapter(MovieListener { movie ->
            searchMoviesViewModel.onMovieClicked(movie)
        })
        searchMoviesViewModel.adapter = adapter
        adapter.submitList(searchMoviesViewModel.searchMoviesList)
        binding.movieList.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.searchFragment)
        menuItem.expandActionView()

        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchMoviesViewModel.getSearchResult(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}
