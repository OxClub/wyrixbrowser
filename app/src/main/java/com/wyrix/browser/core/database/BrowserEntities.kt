package com.wyrix.browser.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val isPrivate: Boolean,
    val workspaceId: String,
    val groupId: String?,
    val position: Int,
    val lastAccessed: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val visitCount: Int = 1
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val folder: String = "Root",
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: Long,
    val fileName: String,
    val uri: String,
    val mimeType: String,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val status: Int,
    val timestamp: Long = System.currentTimeMillis()
)
