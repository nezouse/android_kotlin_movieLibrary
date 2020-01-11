package com.movielibrary.ui.main

import android.util.Log
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MainFragmentAdapter(options: FirestoreRecyclerOptions<Movie>) :
    FirestoreRecyclerAdapter<Movie, MovieViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        Log.i("QUERY", "Holder created")
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int, model: Movie) {
        holder.bind(model)
    }

}