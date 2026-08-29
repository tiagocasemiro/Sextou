package com.sextou.local.di

import androidx.room.Room
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.local.database.SextouDatabase
import com.sextou.local.repository.FavoriteLocalRepository
import com.sextou.local.repository.VisitedPlaceLocalRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun localModule() = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            SextouDatabase::class.java,
            DATABASE_NAME,
        ).build()
    }
    factory<FavoriteRepository.Local> { FavoriteLocalRepository(get()) }
    factory<VisitRepository.Local> { VisitedPlaceLocalRepository(get()) }
}

private const val DATABASE_NAME = "sextou.db"
