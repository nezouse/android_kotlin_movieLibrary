package com.movielibrary.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/movie/"
private const val API_KEY = "API_KEY"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface MovieApiService {

    @GET("popular")
    fun getPopularMoviesAsync(@Query("api_key") key: String = API_KEY):
            Deferred<MoviesResult<Movie>>

    @GET("{movieId}")
    fun getMovieDetailsAsync(@Path("movieId") id: Int, @Query("api_key") key: String = API_KEY):
            Deferred<MovieDetails>
}

object MovieApi {
    val retrofitService: MovieApiService by lazy { retrofit.create(MovieApiService::class.java) }
}