package com.wyrix.browser.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {
    @Query("SELECT * FROM tabs WHERE isPrivate = 0 ORDER BY position ASC")
    fun getNormalTabs(): Flow<List<TabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTab(tab: TabEntity)

    @Delete
    suspend fun deleteTab(tab: TabEntity)

    @Query("DELETE FROM tabs WHERE id = :tabId")
    suspend fun deleteTabById(tabId: String)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Query("SELECT * FROM bookmarks ORDER BY dateAdded DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)
}
