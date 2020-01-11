package com.movielibrary.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesResult (
    @Json(name = "results") val movieList: List<Movie>
) : Parcelable