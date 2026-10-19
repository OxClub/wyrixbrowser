package com.wyrix.browser.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TabEntity::class, HistoryEntity::class, BookmarkEntity::class, DownloadEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WyrixDatabase : RoomDatabase() {
    abstract fun browserDao(): BrowserDao

    companion object {
        @Volatile
        private var INSTANCE: WyrixDatabase? = null

        fun getInstance(context: Context): WyrixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WyrixDatabase::class.java,
                    "wyrix_browser.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
