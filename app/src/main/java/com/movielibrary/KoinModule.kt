package com.movielibrary

import android.app.Application
import androidx.room.Room
import com.movielibrary.database.MoviesDao
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.ui.main.PopularMoviesViewModel
import com.movielibrary.ui.main.SearchMoviesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    fun provideMoviesDatabase(application: Application): MoviesDatabase {
        return Room.databaseBuilder(application, MoviesDatabase::class.java, "movies_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideRoomDao(database: MoviesDatabase): MoviesDao {
        return database.moviesDao
    }

    single { provideMoviesDatabase(get()) }
    single { provideRoomDao(get()) }

    viewModel { PopularMoviesViewModel(get()) }
    viewModel { SearchMoviesViewModel(get()) }
}