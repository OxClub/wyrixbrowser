package com.wyrix.browser.core.bookmarks

import com.wyrix.browser.core.database.BookmarkEntity

object BookmarkExporter {
    fun exportToNetscapeHtml(bookmarks: List<BookmarkEntity>): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n")
        sb.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n")
        sb.append("<TITLE>WYRIX Bookmarks</TITLE>\n")
        sb.append("<H1>Bookmarks</H1>\n<DL><p>\n")
        for (b in bookmarks) {
            sb.append("    <DT><A HREF=\"").append(b.url).append("\">").append(b.title).append("</A>\n")
        }
        sb.append("</DL><p>\n")
        return sb.toString()
    }
}
