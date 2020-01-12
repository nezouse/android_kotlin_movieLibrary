package com.movielibrary.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.movielibrary.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
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

    @GET("movie/popular?api_key=$apiKey")
    fun getPopularMoviesAsync(): Deferred<MoviesResult<Movie>>

    @GET("movie/{movieId}?api_key=$apiKey")
    fun getMovieDetailsAsync(@Path("movieId") id: Int):
            Deferred<MovieDetails>

    @GET("search/movie?api_key=$apiKey")
    fun searchMoviesAsync(@Query("query") query: String):
            Deferred<MoviesResult<SimpleMovie>>

}

object MovieApi {
    val retrofitService: MovieApiService by lazy { retrofit.create(MovieApiService::class.java) }
}