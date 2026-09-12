package com.sextou.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `places` (
                `placeId` TEXT NOT NULL,
                `displayName` TEXT,
                `formattedAddress` TEXT,
                `latitude` REAL,
                `longitude` REAL,
                `primaryType` TEXT,
                `primaryTypeDisplayName` TEXT,
                `businessStatus` TEXT NOT NULL,
                `rating` REAL,
                `userRatingCount` INTEGER,
                `priceLevel` INTEGER,
                `googleMapsUri` TEXT,
                `providerAttribution` TEXT NOT NULL,
                PRIMARY KEY(`placeId`)
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `place_types` (
                `placeId` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                PRIMARY KEY(`placeId`, `type`),
                FOREIGN KEY(`placeId`) REFERENCES `places`(`placeId`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_place_types_placeId` " +
                "ON `place_types` (`placeId`)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `place_photos` (
                `placeId` TEXT NOT NULL,
                `photoIndex` INTEGER NOT NULL,
                `width` INTEGER NOT NULL,
                `height` INTEGER NOT NULL,
                `attributionHtml` TEXT,
                `googleMapsUri` TEXT,
                `flagContentUri` TEXT,
                PRIMARY KEY(`placeId`, `photoIndex`),
                FOREIGN KEY(`placeId`) REFERENCES `places`(`placeId`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_place_photos_placeId` " +
                "ON `place_photos` (`placeId`)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `place_photo_authors` (
                `placeId` TEXT NOT NULL,
                `photoIndex` INTEGER NOT NULL,
                `authorIndex` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `profileUri` TEXT,
                `photoUri` TEXT,
                PRIMARY KEY(`placeId`, `photoIndex`, `authorIndex`),
                FOREIGN KEY(`placeId`, `photoIndex`)
                    REFERENCES `place_photos`(`placeId`, `photoIndex`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_place_photo_authors_placeId_photoIndex` " +
                "ON `place_photo_authors` (`placeId`, `photoIndex`)"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `ignored_places` (
                `placeId` TEXT NOT NULL,
                `selectedAt` INTEGER NOT NULL,
                PRIMARY KEY(`placeId`)
            )
            """.trimIndent(),
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `automatic_refresh` " +
                "(`id` INTEGER NOT NULL, `successDay` INTEGER NOT NULL, PRIMARY KEY(`id`))",
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `places` ADD COLUMN `liveMusic` TEXT NOT NULL DEFAULT 'NOT_AVAILABLE'",
        )
        db.execSQL(
            "ALTER TABLE `places` ADD COLUMN `goodForChildren` TEXT NOT NULL DEFAULT 'NOT_AVAILABLE'",
        )
    }
}
