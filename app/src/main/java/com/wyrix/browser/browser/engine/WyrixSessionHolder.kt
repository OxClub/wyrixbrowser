package com.wyrix.browser.browser.engine

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

class WyrixSessionHolder(
    val tabId: String,
    val isPrivate: Boolean,
    private val context: Context,
    private val onTitleChanged: (String) -> Unit,
    private val onUrlChanged: (String) -> Unit,
    private val onProgressChanged: (Int) -> Unit,
    private val onSecurityChanged: (Boolean) -> Unit
) {
    val session: GeckoSession

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    init {
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .build()

        session = GeckoSession(settings)
        setupDelegates()
        if (!session.isOpen) {
            session.open(WyrixGeckoRuntime.get(context))
        }
    }

    private fun setupDelegates() {
        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onProgressChange(session: GeckoSession, progress: Int) {
                onProgressChanged(progress)
            }
            override fun onSecurityChange(session: GeckoSession, securityInfo: GeckoSession.ProgressDelegate.SecurityInformation) {
                onSecurityChanged(securityInfo.isSecure)
            }
        }

        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                perms: List<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean
            ) {
                if (!url.isNullOrBlank() && url != "about:blank") {
                    onUrlChanged(url)
                }
            }
            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                _canGoBack.value = canGoBack
            }
            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                _canGoForward.value = canGoForward
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                if (!title.isNullOrBlank()) onTitleChanged(title)
            }
        }

        // YouTube aur baki websites ke liye Media aur Content Permissions grant karein taaki crash na ho
        session.permissionDelegate = object : GeckoSession.PermissionDelegate {
            override fun onContentPermissionRequest(
                session: GeckoSession,
                perm: GeckoSession.PermissionDelegate.ContentPermission
            ): GeckoResult<Int> {
                return GeckoResult.fromValue(GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW)
            }
        }
    }

    fun loadUri(uri: String) {
        if (!session.isOpen) {
            session.open(WyrixGeckoRuntime.get(context))
        }
        session.loadUri(uri)
    }

    fun goBack() { if (_canGoBack.value) session.goBack() }
    fun goForward() { if (_canGoForward.value) session.goForward() }
    fun reload() { session.reload() }
    fun stop() { session.stop() }
    fun close() {
        try {
            if (session.isOpen) session.close()
        } catch (e: Exception) {}
    }
}
