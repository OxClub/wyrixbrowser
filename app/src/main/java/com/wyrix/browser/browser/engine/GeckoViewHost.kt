package com.wyrix.browser.browser.engine

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

@Composable
fun GeckoViewHost(
    session: GeckoSession?,
    modifier: Modifier = Modifier
) {
    if (session == null) return

    AndroidView(
        factory = { context ->
            GeckoView(context).apply {
                // Jetpack Compose ke sath TextureView bilkul stable chalta hai
                setViewBackend(GeckoView.BACKEND_TEXTURE_VIEW)
                setSession(session)
            }
        },
        update = { view ->
            try {
                if (view.session !== session) {
                    view.releaseSession()
                    view.setSession(session)
                }
            } catch (e: Exception) {}
        },
        onRelease = { view ->
            try {
                view.releaseSession()
            } catch (e: Exception) {}
        },
        modifier = modifier
    )
}
