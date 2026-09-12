package com.sextou.local.di

import com.sextou.domain.places.repository.AutomaticRefreshRepository
import com.sextou.local.adapter.AutomaticRefreshLocalImpl
import com.sextou.local.database.MIGRATION_3_4
import com.sextou.local.database.MIGRATION_4_5
import androidx.room.Room
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import com.sextou.domain.places.repository.PlaceStatusRepository
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.local.adapter.PlacesLocalImpl
import com.sextou.local.database.SextouDatabase
import com.sextou.local.database.MIGRATION_1_2
import com.sextou.local.database.MIGRATION_2_3
import com.sextou.local.database.PlacesDao
import com.sextou.local.repository.FavoriteLocalRepository
import com.sextou.local.repository.IgnoredPlaceLocalRepository
import com.sextou.local.repository.PlaceStatusLocalRepository
import com.sextou.local.repository.VisitedPlaceLocalRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun localModule() = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            SextouDatabase::class.java,
            DATABASE_NAME,
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
    }
    factory<AutomaticRefreshRepository.Local> {
        AutomaticRefreshLocalImpl(get<SextouDatabase>().automaticRefreshDao())
    }
    single<PlacesDao> { get<SextouDatabase>().placesDao() }
    factory<FavoriteRepository.Local> { FavoriteLocalRepository(get()) }
    factory<IgnoredPlaceRepository.Local> { IgnoredPlaceLocalRepository(get()) }
    factory<PlaceStatusRepository.Local> { PlaceStatusLocalRepository(get()) }
    factory<PlacesRepository.Local> { PlacesLocalImpl(placesDao = get()) }
    factory<VisitRepository.Local> { VisitedPlaceLocalRepository(get()) }
}

private const val DATABASE_NAME = "sextou.db"
