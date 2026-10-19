package com.wyrix.browser.ui.screens.settings

enum class SettingsCategory(val title: String, val subtitle: String, val iconName: String) {
    SEARCH("Search Settings", "Default engine, query suggestions, custom templates", "Search"),
    TOOLBAR("Address Bar Position", "Configure top or bottom toolbar placement", "ViewStream"),
    PRIVACY("Privacy & Security", "Wyrix Shield, ETP, HTTPS-Only mode, Clear browsing data", "Shield"),
    SAFETY_CHECK("Safety Check", "Instant audit of browser safety, passwords and shields", "CheckCircle"),
    DEFAULT_BROWSER("Default Browser", "Set WYRIX as your system-wide web browser", "Launch"),
    AUTOFILL("Autofill & Passwords", "System credential manager and password saving", "Password"),
    TABS("Tabs & Tab Groups", "Grid/List layout, tab recovery, background tabs", "Tab"),
    HOMEPAGE("Homepage Customization", "Shortcuts grid, live privacy stats, custom URL", "Home"),
    NOTIFICATIONS("Website Notifications", "Manage push notifications and site permissions", "Notifications"),
    APPEARANCE("Appearance & Themes", "AMOLED Black, Cyber Blue, Neon Violet accents", "Palette"),
    ACCESSIBILITY("Accessibility", "Text scaling (50% - 200%), Force pinch-to-zoom", "Accessibility"),
    SITE_PERMISSIONS("Site Permissions", "Camera, Mic, Location, JavaScript, and Cookies", "Security"),
    LANGUAGES("Languages & Translation", "App language (English/Hindi), Accept-Language", "Translate"),
    DOWNLOADS("Advanced Download Manager", "Storage path, Wi-Fi only, prompt on download", "Download"),
    ABOUT("About WYRIX Browser", "GeckoView 133 build, licenses, zero-tracking audit", "Info")
}
