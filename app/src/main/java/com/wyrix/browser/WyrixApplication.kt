package com.wyrix.browser

import android.app.Application
import android.os.Build
import android.util.Log

class WyrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (isMainProcess()) {
            try {
                com.wyrix.browser.browser.engine.WyrixGeckoRuntime.get(this)
            } catch (e: Throwable) {
                Log.e("WyrixApp", "GeckoRuntime init failed", e)
            }
        }
    }

    private fun isMainProcess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getProcessName() == packageName
        } else {
            true
        }
    }
}
