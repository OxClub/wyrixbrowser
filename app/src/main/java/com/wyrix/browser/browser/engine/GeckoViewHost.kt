package com.wyrix.browser.browser.engine

import android.view.ViewGroup
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
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
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
        modifier = modifier
    )
}
