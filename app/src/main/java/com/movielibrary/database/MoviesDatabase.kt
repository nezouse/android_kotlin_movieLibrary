package com.movielibrary.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieEntity::class, PopularMovieEntity::class, RecentlyViewedMovie::class, CommentEntity::class, RatedMovie::class, LikedMovie::class],
    version = 14,
    exportSchema = false
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
}
