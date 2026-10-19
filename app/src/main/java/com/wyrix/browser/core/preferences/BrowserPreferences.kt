package com.wyrix.browser.core.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "wyrix_browser_prefs")

enum class ToolbarPosition { TOP, BOTTOM }
enum class TrackingLevel { STRICT, STANDARD, OFF }
enum class CookiePolicy { ACCEPT_ALL, BLOCK_THIRD_PARTY, BLOCK_ALL }
enum class AppTheme { AMOLED, DARK, LIGHT, SYSTEM }
enum class AccentColorChoice { NEON_BLUE, NEON_VIOLET, NEON_PINK, ELECTRIC_GREEN }
enum class TabViewStyle { GRID, LIST }

data class WyrixPreferences(
    val searchEngine: String = "DuckDuckGo",
    val customSearchUrl: String = "",
    val searchSuggestionsEnabled: Boolean = true,
    val toolbarPosition: ToolbarPosition = ToolbarPosition.TOP,
    val trackingLevel: TrackingLevel = TrackingLevel.STRICT,
    val httpsOnlyMode: Boolean = true,
    val doNotTrack: Boolean = true,
    val clearOnExit: Boolean = false,
    val biometricLockEnabled: Boolean = false,
    val autofillEnabled: Boolean = true,
    val tabViewStyle: TabViewStyle = TabViewStyle.GRID,
    val restoreSessionOnStartup: Boolean = true,
    val showHomepageShortcuts: Boolean = true,
    val showHomepageShieldStats: Boolean = true,
    val appTheme: AppTheme = AppTheme.AMOLED,
    val accentColor: AccentColorChoice = AccentColorChoice.NEON_BLUE,
    val textZoomPercent: Int = 100,
    val forceEnableZoom: Boolean = false,
    val javaScriptEnabled: Boolean = true,
    val cookiePolicy: CookiePolicy = CookiePolicy.BLOCK_THIRD_PARTY,
    val appLanguage: String = "SYSTEM",
    val downloadWifiOnly: Boolean = false,
    val askDownloadLocation: Boolean = false
)

class WyrixPreferencesRepository(private val context: Context) {
    private val dataStore = context.dataStore

    private object Keys {
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val CUSTOM_SEARCH_URL = stringPreferencesKey("custom_search_url")
        val SEARCH_SUGGESTIONS = booleanPreferencesKey("search_suggestions")
        val TOOLBAR_POSITION = stringPreferencesKey("toolbar_position")
        val TRACKING_LEVEL = stringPreferencesKey("tracking_level")
        val HTTPS_ONLY = booleanPreferencesKey("https_only")
        val DO_NOT_TRACK = booleanPreferencesKey("do_not_track")
        val CLEAR_ON_EXIT = booleanPreferencesKey("clear_on_exit")
        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val AUTOFILL = booleanPreferencesKey("autofill")
        val TAB_VIEW_STYLE = stringPreferencesKey("tab_view_style")
        val RESTORE_SESSION = booleanPreferencesKey("restore_session")
        val SHOW_SHORTCUTS = booleanPreferencesKey("show_shortcuts")
        val SHOW_SHIELD_STATS = booleanPreferencesKey("show_shield_stats")
        val APP_THEME = stringPreferencesKey("app_theme")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val TEXT_ZOOM = intPreferencesKey("text_zoom")
        val FORCE_ZOOM = booleanPreferencesKey("force_zoom")
        val JAVASCRIPT = booleanPreferencesKey("javascript")
        val COOKIE_POLICY = stringPreferencesKey("cookie_policy")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val DOWNLOAD_WIFI_ONLY = booleanPreferencesKey("download_wifi_only")
        val ASK_DOWNLOAD_LOC = booleanPreferencesKey("ask_download_loc")
    }

    val preferencesFlow: Flow<WyrixPreferences> = dataStore.data.map { p ->
        WyrixPreferences(
            searchEngine = p[Keys.SEARCH_ENGINE] ?: "DuckDuckGo",
            customSearchUrl = p[Keys.CUSTOM_SEARCH_URL] ?: "",
            searchSuggestionsEnabled = p[Keys.SEARCH_SUGGESTIONS] ?: true,
            toolbarPosition = ToolbarPosition.valueOf(p[Keys.TOOLBAR_POSITION] ?: ToolbarPosition.TOP.name),
            trackingLevel = TrackingLevel.valueOf(p[Keys.TRACKING_LEVEL] ?: TrackingLevel.STRICT.name),
            httpsOnlyMode = p[Keys.HTTPS_ONLY] ?: true,
            doNotTrack = p[Keys.DO_NOT_TRACK] ?: true,
            clearOnExit = p[Keys.CLEAR_ON_EXIT] ?: false,
            biometricLockEnabled = p[Keys.BIOMETRIC_LOCK] ?: false,
            autofillEnabled = p[Keys.AUTOFILL] ?: true,
            tabViewStyle = TabViewStyle.valueOf(p[Keys.TAB_VIEW_STYLE] ?: TabViewStyle.GRID.name),
            restoreSessionOnStartup = p[Keys.RESTORE_SESSION] ?: true,
            showHomepageShortcuts = p[Keys.SHOW_SHORTCUTS] ?: true,
            showHomepageShieldStats = p[Keys.SHOW_SHIELD_STATS] ?: true,
            appTheme = AppTheme.valueOf(p[Keys.APP_THEME] ?: AppTheme.AMOLED.name),
            accentColor = AccentColorChoice.valueOf(p[Keys.ACCENT_COLOR] ?: AccentColorChoice.NEON_BLUE.name),
            textZoomPercent = p[Keys.TEXT_ZOOM] ?: 100,
            forceEnableZoom = p[Keys.FORCE_ZOOM] ?: false,
            javaScriptEnabled = p[Keys.JAVASCRIPT] ?: true,
            cookiePolicy = CookiePolicy.valueOf(p[Keys.COOKIE_POLICY] ?: CookiePolicy.BLOCK_THIRD_PARTY.name),
            appLanguage = p[Keys.APP_LANGUAGE] ?: "SYSTEM",
            downloadWifiOnly = p[Keys.DOWNLOAD_WIFI_ONLY] ?: false,
            askDownloadLocation = p[Keys.ASK_DOWNLOAD_LOC] ?: false
        )
    }

    suspend fun setSearchEngine(engine: String) { dataStore.edit { it[Keys.SEARCH_ENGINE] = engine } }
    suspend fun setCustomSearchUrl(url: String) { dataStore.edit { it[Keys.CUSTOM_SEARCH_URL] = url } }
    suspend fun setSearchSuggestions(enabled: Boolean) { dataStore.edit { it[Keys.SEARCH_SUGGESTIONS] = enabled } }
    suspend fun setToolbarPosition(pos: ToolbarPosition) { dataStore.edit { it[Keys.TOOLBAR_POSITION] = pos.name } }
    suspend fun setTrackingLevel(level: TrackingLevel) { dataStore.edit { it[Keys.TRACKING_LEVEL] = level.name } }
    suspend fun setHttpsOnly(enabled: Boolean) { dataStore.edit { it[Keys.HTTPS_ONLY] = enabled } }
    suspend fun setDoNotTrack(enabled: Boolean) { dataStore.edit { it[Keys.DO_NOT_TRACK] = enabled } }
    suspend fun setClearOnExit(enabled: Boolean) { dataStore.edit { it[Keys.CLEAR_ON_EXIT] = enabled } }
    suspend fun setBiometricLock(enabled: Boolean) { dataStore.edit { it[Keys.BIOMETRIC_LOCK] = enabled } }
    suspend fun setAutofill(enabled: Boolean) { dataStore.edit { it[Keys.AUTOFILL] = enabled } }
    suspend fun setTabViewStyle(style: TabViewStyle) { dataStore.edit { it[Keys.TAB_VIEW_STYLE] = style.name } }
    suspend fun setRestoreSession(enabled: Boolean) { dataStore.edit { it[Keys.RESTORE_SESSION] = enabled } }
    suspend fun setShowShortcuts(show: Boolean) { dataStore.edit { it[Keys.SHOW_SHORTCUTS] = show } }
    suspend fun setShowShieldStats(show: Boolean) { dataStore.edit { it[Keys.SHOW_SHIELD_STATS] = show } }
    suspend fun setAppTheme(theme: AppTheme) { dataStore.edit { it[Keys.APP_THEME] = theme.name } }
    suspend fun setAccentColor(color: AccentColorChoice) { dataStore.edit { it[Keys.ACCENT_COLOR] = color.name } }
    suspend fun setTextZoom(percent: Int) { dataStore.edit { it[Keys.TEXT_ZOOM] = percent } }
    suspend fun setForceZoom(enabled: Boolean) { dataStore.edit { it[Keys.FORCE_ZOOM] = enabled } }
    suspend fun setJavaScript(enabled: Boolean) { dataStore.edit { it[Keys.JAVASCRIPT] = enabled } }
    suspend fun setCookiePolicy(policy: CookiePolicy) { dataStore.edit { it[Keys.COOKIE_POLICY] = policy.name } }
    suspend fun setAppLanguage(lang: String) { dataStore.edit { it[Keys.APP_LANGUAGE] = lang } }
    suspend fun setDownloadWifiOnly(wifiOnly: Boolean) { dataStore.edit { it[Keys.DOWNLOAD_WIFI_ONLY] = wifiOnly } }
    suspend fun setAskDownloadLocation(ask: Boolean) { dataStore.edit { it[Keys.ASK_DOWNLOAD_LOC] = ask } }
}
