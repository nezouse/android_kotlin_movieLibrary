package com.movielibrary.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.movielibrary.R
import com.movielibrary.databinding.MainFragmentBinding

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

        val query = FirebaseFirestore.getInstance()
            .collection("movies")

//        query.get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d("QUERY", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("QUERY", "Error getting documents: ", exception)
//            }

        val options =
            FirestoreRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter = MainFragmentAdapter(options)

        binding.movieList.adapter = adapter


        Log.i("QUERY", adapter.itemCount.toString())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.getPopularMovies()
    }

}
