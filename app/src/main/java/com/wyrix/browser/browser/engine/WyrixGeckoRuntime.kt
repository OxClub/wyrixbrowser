package com.wyrix.browser.browser.engine

import android.content.Context
import com.wyrix.browser.core.preferences.CookiePolicy
import com.wyrix.browser.core.preferences.TrackingLevel
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.StorageController

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
            .consoleOutput(true)
            .aboutConfigEnabled(true)
            .contentBlocking(
                ContentBlocking.Settings.Builder()
                    .antiTracking(ContentBlocking.AntiTracking.STRICT)
                    .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)
                    .cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS)
                    .build()
            )
            .build()

        return GeckoRuntime.create(context, settings)
    }

    fun applyTrackingProtection(context: Context, level: TrackingLevel) {
        val rt = get(context)
        val etp = when (level) {
            TrackingLevel.STRICT -> ContentBlocking.EtpLevel.STRICT
            TrackingLevel.STANDARD -> ContentBlocking.EtpLevel.DEFAULT
            TrackingLevel.OFF -> ContentBlocking.EtpLevel.NONE
        }
        rt.settings.contentBlocking.setEnhancedTrackingProtectionLevel(etp)
    }

    fun applyCookiePolicy(context: Context, policy: CookiePolicy) {
        val rt = get(context)
        val behavior = when (policy) {
            CookiePolicy.ACCEPT_ALL -> ContentBlocking.CookieBehavior.ACCEPT_ALL
            CookiePolicy.BLOCK_THIRD_PARTY -> ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS
            CookiePolicy.BLOCK_ALL -> ContentBlocking.CookieBehavior.ACCEPT_NONE
        }
        rt.settings.contentBlocking.setCookieBehavior(behavior)
    }

    fun applyJavaScript(context: Context, enabled: Boolean) {
        get(context).settings.javaScriptEnabled = enabled
    }

    fun clearAllWebStorage(context: Context, onComplete: () -> Unit) {
        get(context).storageController.clearData(StorageController.ClearFlags.ALL)
            .then({
                onComplete()
                org.mozilla.geckoview.GeckoResult.fromValue(null)
            }, {
                onComplete()
                org.mozilla.geckoview.GeckoResult.fromValue(null)
            })
    }
}
