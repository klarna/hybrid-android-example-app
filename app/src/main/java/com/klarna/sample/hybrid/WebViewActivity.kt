package com.klarna.sample.hybrid

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.klarna.mobile.sdk.api.KlarnaEventListener
import com.klarna.mobile.sdk.api.hybrid.KlarnaHybridSDK
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val KEY_URL: String = "URL"
    }

    private lateinit var klarnaHybridSDK: KlarnaHybridSDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        setupSDK()
        setupAddressBar()
        intent.getStringExtra(KEY_URL)?.let {
            loadUrl(it)
        }
    }

    private fun setupSDK() {
        klarnaHybridSDK = KlarnaHybridSDK(
            "${getString(R.string.return_url_scheme)}://${getString(R.string.return_url_host)}",
            KlarnaHybridSDKCallback(findViewById(R.id.rootLayout))
        )
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        klarnaHybridSDK.addWebView(webView)
        klarnaHybridSDK.registerEventListener(KlarnaEventListener {
            // Replace with your implementation for events
            Log.d("Hybrid Klarna Event", it.bodyString ?: "")
        })
    }

    private fun setupAddressBar() {
        imageViewLoadAddress.setOnClickListener {
            loadUrl(editTextAddressBar.text.toString().trim())
        }
        editTextAddressBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    imageViewLoadAddress.performClick()
                    return true
                }
                return false
            }
        })
    }

    private fun loadUrl(url: String) {
        var address = url
        if (!address.contains("://")) {
            address = "https://$address"
        }
        webView.loadUrl(address)
        editTextAddressBar.setText(address)
        editTextAddressBar.setSelection(address.length)

        // Dismiss the keyboard
        editTextAddressBar.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editTextAddressBar?.windowToken, 0)
    }

    inner class MyWebViewClient :
        WebViewClient() {

        // If shouldFollowNavigation(url) returns false, the url loading should be overridden and ignored. Otherwise load the url.
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return !klarnaHybridSDK.shouldFollowNavigation(url)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            request?.url?.toString()?.let {
                return !klarnaHybridSDK.shouldFollowNavigation(it)
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        // The SDK needs to be notified every time a new url has loaded.
        override fun onPageFinished(view: WebView, url: String) {
            klarnaHybridSDK.newPageLoad(view)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            editTextAddressBar.setText(url)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}