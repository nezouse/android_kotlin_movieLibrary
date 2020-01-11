package com.movielibrary.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.movielibrary.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://api.themoviedb.org/3/movie/"
private const val apiKey = BuildConfig.API_KEY

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface MovieApiService {

    @GET("popular?api_key=$apiKey")
    fun getPropertiesAsync(): Deferred<MoviesResult<Movie>>

    @GET("{movieId}?api_key=$apiKey")
    fun getMovieDetailsAsync(@Path("movieId") id: Int):
            Deferred<MovieDetails>
}

object MovieApi {
    val retrofitService: MovieApiService by lazy { retrofit.create(MovieApiService::class.java) }
}