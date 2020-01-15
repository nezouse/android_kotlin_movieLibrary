package com.movielibrary.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    if (!imgUrl.isNullOrEmpty()) {
        val completeUrl = "http://image.tmdb.org/t/p/w500$imgUrl"
        val imgUri = completeUrl.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .fitCenter()
            .into(imgView)
    }
}

@BindingAdapter("android:text")
fun bindRating(textView: TextView, rating: Double?) {
    val stringRating = rating.toString() + "/10"
    textView.text = stringRating
}
