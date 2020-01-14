package com.movielibrary.di

import android.app.Application
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.movielibrary.database.FirebaseDao
import com.movielibrary.database.MoviesDao
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.database.Repository
import com.movielibrary.network.MovieApiService
import com.movielibrary.network.NullStringAdapter
import com.movielibrary.ui.popularMovies.PopularMoviesViewModel
import com.movielibrary.ui.searchMovies.SearchMoviesViewModel
import com.movielibrary.ui.movieDetails.MovieDetailsViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    fun provideMoviesDatabase(application: Application): MoviesDatabase {
        return Room.databaseBuilder(application, MoviesDatabase::class.java, "movies_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideRoomDao(database: MoviesDatabase): MoviesDao {
        return database.moviesDao
    }

    fun provideFirebaseDao(): FirebaseDao {
        return FirebaseDao()
    }

    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(NullStringAdapter)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://api.themoviedb.org/3/")
            .build()
    }

    fun provideRetrofitService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    single { provideMoshi() }
    single { provideRetrofit(get()) }
    single { provideRetrofitService(get()) }
    single { provideMoviesDatabase(get()) }
    single { provideRoomDao(get()) }
    single { provideFirebaseDao() }
    single { Repository(get(), get()) }

    viewModel {
        PopularMoviesViewModel(
            get(),
            get()
        )
    }
    viewModel { SearchMoviesViewModel(get(), get()) }
    viewModel { (movieId: Int) -> MovieDetailsViewModel(get(), movieId) }
}