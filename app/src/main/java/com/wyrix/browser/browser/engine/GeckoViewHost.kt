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
    AndroidView(
        factory = { context ->
            GeckoView(context).apply {
                session?.let { setSession(it) }
            }
        },
        update = { view ->
            if (view.session != session) {
                if (view.session != null) {
                    view.releaseSession()
                }
                session?.let { view.setSession(it) }
            }
        },
        onRelease = { view ->
            if (view.session != null) {
                view.releaseSession()
            }
        },
        modifier = modifier
    )
}
