package com.movielibrary.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movielibrary.R
import com.movielibrary.network.Movie

class MovieViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    fun bind(model: Movie) {
//        model.title
        Log.i("QUERY", model.title)
//        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.message, parent, false)

            return MovieViewHolder(view)
        }
    }
}