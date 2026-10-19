package com.wyrix.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WyrixBrowserUnitTest {
    @Test
    fun testUrlFormatting() {
        val query = "android geckoview"
        val formatted = if (!query.startsWith("http://") && !query.startsWith("https://")) {
            if (query.contains(".") && !query.contains(" ")) "https://$query"
            else "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
        } else query

        assertTrue(formatted.startsWith("https://duckduckgo.com/?q="))
    }

    @Test
    fun testDirectDomainUrlFormatting() {
        val domain = "github.com"
        val formatted = if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            if (domain.contains(".") && !domain.contains(" ")) "https://$domain"
            else "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(domain, "UTF-8")}"
        } else domain

        assertEquals("https://github.com", formatted)
    }
}
