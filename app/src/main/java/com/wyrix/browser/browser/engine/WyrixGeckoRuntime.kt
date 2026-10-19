package com.wyrix.browser.browser.engine

import android.content.Context
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

object WyrixGeckoRuntime {
    @Volatile
    private var runtime: GeckoRuntime? = null

    fun get(context: Context): GeckoRuntime {
        return runtime ?: synchronized(this) {
            runtime ?: buildRuntime(context.applicationContext).also { runtime = it }
        }
    }

    private fun buildRuntime(context: Context): GeckoRuntime {
        val settings = GeckoRuntimeSettings.Builder()
            .javaScriptEnabled(true)
            .consoleOutput(false)
            .build()

        return GeckoRuntime.create(context, settings)
    }
}
