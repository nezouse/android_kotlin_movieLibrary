package com.movielibrary.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MoviesResult<T> (
    @Json(name = "results") val movieList: @RawValue List<T>
) : Parcelable
