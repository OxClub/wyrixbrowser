package com.wyrix.browser

import android.app.Application
import android.util.Log

class WyrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            com.wyrix.browser.browser.engine.WyrixGeckoRuntime.get(this)
        } catch (e: Throwable) {
            Log.e("WyrixApp", "Deferred GeckoRuntime init", e)
        }
    }
}
