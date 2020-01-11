package com.movielibrary.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.movielibrary.R
import com.movielibrary.databinding.MainFragmentBinding
import com.movielibrary.network.Movie

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        val mainFragmentViewModel =
            ViewModelProviders.of(requireActivity()).get(MainFragmentViewModel::class.java)
        binding.mainFragmentViewModel = mainFragmentViewModel
        binding.lifecycleOwner = this

        val adapter = getDbAdapter()
        binding.movieList.adapter = adapter

        return binding.root
    }

    private fun getDbAdapter(): MainFragmentAdapter {
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()

        val query = FirebaseFirestore.getInstance()
            .collection("movies")

        val options =
            FirestoreRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie::class.java)
                .setLifecycleOwner(this)
                .build()

        return MainFragmentAdapter(options)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        // TODO: Use the ViewModel
//        viewModel.getPopularMovies()
    }

}
