package com.movielibrary.network

import android.os.Parcelable
import com.movielibrary.database.MovieEntity
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

fun List<Movie>.toEntity(): Array<MovieEntity> {
    return map {
        MovieEntity(
            id = it.id,
            title = it.title,
            overview = it.overview,
            popularity = it.popularity,
            posterPath = it.posterPath,
            rating = it.rating,
            releaseDate = it.releaseDate
        )
    }.toTypedArray()
}

@Parcelize
data class MovieDetails(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    @Json(name = "poster_path") val posterPath: String = "",
    @Json(name = "vote_average") val rating: Double = 0.0,
    @Json(name = "release_date") val releaseDate: String = "",
    val budget: Int = 0,
    val revenue: Int = 0,
    @Json(name = "runtime") val runTime: Int = 0,
    val genres: List<Genre> = emptyList()
) : Parcelable

@Parcelize
data class SimpleMovie(
    val id: Int = 0,
    val title: String = ""
) : Parcelable

@Parcelize
data class Genre(
    val id: Int,
    val name: String
) : Parcelable