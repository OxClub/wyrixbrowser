package com.wyrix.browser

import android.app.Application
import com.wyrix.browser.browser.engine.WyrixGeckoRuntime

class WyrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WyrixGeckoRuntime.get(this)
    }
}
