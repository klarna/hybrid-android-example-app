package com.klarna.sample.hybrid

import android.webkit.WebView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.klarna.mobile.sdk.KlarnaMobileSDKError
import com.klarna.mobile.sdk.api.OnCompletion
import com.klarna.mobile.sdk.api.hybrid.KlarnaHybridSDKCallback
import com.klarna.sample.hybrid.fullscreen.FullscreenDialog

/**
 * Implementation of the [KlarnaHybridSDKCallback]
 */
class KlarnaHybridSDKCallback(private val rootLayout: ConstraintLayout): KlarnaHybridSDKCallback {
    var fullscreenDialog: FullscreenDialog? = null

    override fun willShowFullscreenContent(webView: WebView, completion: OnCompletion) {
        Toast.makeText(webView.context, "willShowFullscreenContent", Toast.LENGTH_LONG).show()

        fullscreenDialog = FullscreenDialog(
            webView.context,
            webView,
            rootLayout
        )
        fullscreenDialog?.prepareToShowContent()

        completion.run()
    }

    override fun didShowFullscreenContent(webView: WebView, completion: OnCompletion) {
        Toast.makeText(webView.context, "didShowFullscreenContent", Toast.LENGTH_LONG).show()

        fullscreenDialog?.showContent()

        completion.run()
    }

    override fun willHideFullscreenContent(webView: WebView, completion: OnCompletion) {
        Toast.makeText(webView.context, "willHideFullscreenContent", Toast.LENGTH_LONG).show()

        fullscreenDialog?.prepareToHideContent()

        completion.run()
    }

    override fun didHideFullscreenContent(webView: WebView, completion: OnCompletion) {
        Toast.makeText(webView.context, "didHideFullscreenContent", Toast.LENGTH_LONG).show()

        fullscreenDialog?.hideContent()
        fullscreenDialog = null

        completion.run()
    }

    override fun onErrorOccurred(webView: WebView, error: KlarnaMobileSDKError) {
        Toast.makeText(webView.context, "An error occurred: $error", Toast.LENGTH_LONG).show()
    }
}