package com.example.varahanest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PropertyEntity::class,
        FavoriteEntity::class,
        SearchHistoryEntity::class,
        RecentlyViewedEntity::class,
        PropertyDraftEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VarahaDatabase : RoomDatabase() {
    abstract val dao: LocalDao
}
