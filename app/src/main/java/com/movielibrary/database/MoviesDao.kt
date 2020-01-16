package com.movielibrary.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies_table AS mv INNER JOIN popular_movies_table AS pmt ON mv.id=pmt.id ORDER BY popularity DESC LIMIT 20")
    fun getPopularMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies_table WHERE title LIKE :query ORDER BY popularity DESC LIMIT 20")
    fun searchMovies(query: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPopularMovies(vararg movies: PopularMovieEntity): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(vararg movies: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentMovie(movie: RecentlyViewedMovie)

    @Query("SELECT recentRank from recently_viewed_table ORDER BY recentRank DESC LIMIT 1")
    fun getMostRecentViewedMovieRank(): Int

    @Query("SELECT mv.id, title, overview, popularity, poster_path, rating, releaseDate FROM movies_table AS mv INNER JOIN recently_viewed_table AS rvt ON mv.id=rvt.movie_id ORDER BY recentRank DESC LIMIT 20")
    fun getRecentlyViewedMovies(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(vararg comment: CommentEntity)

    @Query("SELECT * FROM comments_table WHERE movieId = :movieId")
    fun getCommentsForMovie(movieId: Int): LiveData<List<CommentEntity>>


    @Query("SELECT rating FROM rated_movies_table WHERE movieId = :movieId AND userId = :userId")
    fun getRatingForMovie(movieId: Int, userId: String): List<Float>

    @Query("SELECT * FROM rated_movies_table WHERE userId = :userId")
    fun getAllRatings(userId: String): List<RatedMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRating(vararg ratedMovies:RatedMovie)

    @Query("DELETE FROM rated_movies_table WHERE movieId = :movieId AND userId = :userId")
    fun deleteRatedMovie(movieId: Int, userId: String)

    @Query("SELECT movieId FROM liked_movies_table WHERE movieId = :movieId AND userId = :userId")
    fun getLikedMovie(movieId: Int, userId: String): List<Int>

    @Query("SELECT movieId FROM liked_movies_table WHERE userId = :userId")
    fun getAllLikedMovies(userId: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLikedMovie(likedMovie: LikedMovie)

    @Delete
    fun deleteLikedMovie(likedMovie: LikedMovie)
}