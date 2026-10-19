package com.wyrix.browser.browser.model

enum class Workspace(val title: String, val icon: String) {
    PERSONAL("Personal", "👤"),
    WORK("Work", "💼"),
    STUDY("Study", "📚")
}

data class TabModel(
    val id: String,
    val title: String,
    val url: String,
    val isPrivate: Boolean,
    val workspace: Workspace = Workspace.PERSONAL,
    val progress: Int = 0,
    val isSecure: Boolean = true
)
