package com.movielibrary.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "",

    val overview: String = "",

    val popularity: Double = 0.0,

    @ColumnInfo(name = "poster_path")
    val posterPath: String = "",

    val rating: Double = 0.0,

    val releaseDate: String = ""
) : Parcelable

@Entity(
    tableName = "popular_movies_table",
    foreignKeys = [ForeignKey(
        entity = MovieEntity::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PopularMovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

fun List<MovieEntity>.toPopularMovie(): List<PopularMovieEntity> {
    return map {
        PopularMovieEntity(
            id = it.id
        )
    }
}
