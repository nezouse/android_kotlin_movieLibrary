package com.movielibrary.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieEntity::class, PopularMovieEntity::class, RecentlyViewedMovie::class, CommentEntity::class],
    version = 9,
    exportSchema = false
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
}
