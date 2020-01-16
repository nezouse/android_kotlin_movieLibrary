package com.movielibrary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import java.util.Date

@Database(
    entities = [MovieEntity::class, PopularMovieEntity::class, RecentlyViewedMovie::class, CommentEntity::class, RatedMovie::class, LikedMovie::class],
    version = 15,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoviesDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(Date(it)) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Timestamp?): Long? {
        return date?.toDate()?.time
    }
}