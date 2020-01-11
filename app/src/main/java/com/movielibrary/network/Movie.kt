package com.movielibrary.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_path") val posterPath: String,
    @Json(name = "vote_average") val rating: Double,
    @Json(name = "release_date") val releaseDate: String
) : Parcelable