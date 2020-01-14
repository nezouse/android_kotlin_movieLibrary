package com.movielibrary.network

import com.movielibrary.BuildConfig
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val apiKey = BuildConfig.API_KEY

interface MovieApiService {

    @GET("movie/popular?api_key=$apiKey")
    fun getPopularMoviesAsync(): Deferred<MoviesResult<Movie>>

    @GET("movie/{movieId}?api_key=$apiKey")
    fun getMovieDetailsAsync(@Path("movieId") id: Int):
        Deferred<MovieDetails>

    @GET("search/movie?api_key=$apiKey")
    fun searchMoviesAsync(@Query("query") query: String):
        Deferred<MoviesResult<Movie>>
}

object NullStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextString()
        }
        reader.nextNull<Unit>()
        return ""
    }
}
