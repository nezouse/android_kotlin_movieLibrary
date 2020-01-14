package com.movielibrary.ui.searchMovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.movielibrary.R
import com.movielibrary.databinding.SearchFragmentBinding
import com.movielibrary.ui.recyclerAdapters.FragmentAdapter
import com.movielibrary.ui.recyclerAdapters.MovieListener
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchMoviesViewModel: SearchMoviesViewModel by viewModel()
    private lateinit var adapter: FragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SearchFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.search_fragment, container, false
        )

        binding.searchFragmentViewModel = searchMoviesViewModel
        binding.lifecycleOwner = this

        searchMoviesViewModel.navigateToDetailView.observe(this, Observer { movie ->
            movie?.let {
                this.findNavController()
                    .navigate(
                        SearchFragmentDirections.actionSearchFragmentToMovieDetails(movie)
                    )
                searchMoviesViewModel.onMovieNavigated()
            }
        })

        connectAdapter(binding)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun connectAdapter(binding: SearchFragmentBinding) {
        adapter = FragmentAdapter(
            MovieListener { movie ->
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
