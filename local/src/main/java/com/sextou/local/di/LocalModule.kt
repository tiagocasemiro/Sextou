package com.sextou.local.di

import androidx.room.Room
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.local.adapter.PlacesLocalImpl
import com.sextou.local.database.SextouDatabase
import com.sextou.local.database.MIGRATION_1_2
import com.sextou.local.database.PlacesDao
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
        ).addMigrations(MIGRATION_1_2).build()
    }
    single<PlacesDao> { get<SextouDatabase>().placesDao() }
    factory<FavoriteRepository.Local> { FavoriteLocalRepository(get()) }
    factory<PlacesRepository.Local> { PlacesLocalImpl(placesDao = get()) }
    factory<VisitRepository.Local> { VisitedPlaceLocalRepository(get()) }
}

private const val DATABASE_NAME = "sextou.db"
