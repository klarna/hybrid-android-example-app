package com.klarna.sample.hybrid.fullscreen

import android.app.Dialog
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.view.*
import android.webkit.WebView
import com.klarna.sample.hybrid.R

/**
 * Custom dialog for showing fullscreen content aka moving fullscreen
 */
class FullscreenDialog(context: Context, var webView: WebView?, var rootLayout: ConstraintLayout?): Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

    fun prepareToShowContent() {
        rootLayout?.apply {
            webView?.let {
                val screenshot = ScreenshotView(it.context, it)
                screenshot.id = View.generateViewId()
                addView(screenshot)
                val constraintSet = ConstraintSet()
                constraintSet.clone(this)
                constraintSet.connect(screenshot.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                constraintSet.applyTo(this)
            }
        }
    }

    fun showContent() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        rootLayout?.removeView(webView)
        webView?.let { setContentView(it) }

        val layoutParams = window?.attributes

        layoutParams?.gravity = Gravity.CENTER
        layoutParams?.flags = layoutParams?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
        window?.attributes = layoutParams
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        show()
    }

    fun prepareToHideContent() {
        webView?.let {
            val screenshotIndex = 2

            addContentView(
                ScreenshotView(it.context, it),
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
            (it.parent as ViewGroup?)?.removeView(it)

            // Capture the size of the ScreenshotView so the re-attached WebView has the same size.
            val contentHeight = rootLayout?.getChildAt(screenshotIndex)?.height
            rootLayout?.removeViewAt(screenshotIndex) // remove the screenshot
            rootLayout?.addView(webView)
            val constraintSet = ConstraintSet()
            constraintSet.clone(rootLayout)
            constraintSet.connect(it.id, ConstraintSet.TOP,
                R.id.editTextAddressBar, ConstraintSet.BOTTOM, 0)
            constraintSet.applyTo(rootLayout)
            it.layoutParams = it.layoutParams?.apply {
                contentHeight?.apply {
                    height = this
                }
            }
        }

    }

    fun hideContent() {
        dismiss()
    }

    override fun dismiss() {
        webView = null
        rootLayout = null
        super.dismiss()
    }
}