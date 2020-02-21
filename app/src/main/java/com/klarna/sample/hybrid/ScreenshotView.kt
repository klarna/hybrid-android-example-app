package com.klarna.sample.hybrid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View which creates and shows an "screenshot" of the a _target_ View.
 */
@SuppressLint("ViewConstructor")
class ScreenshotView(context: Context, private val target: View) : ImageView(context) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        GlobalScope.launch (Dispatchers.IO) {
            val bitmap = Bitmap.createBitmap(target.width, target.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            target.draw(canvas)

            launch(Dispatchers.Main) {
                setImageBitmap(bitmap)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setImageBitmap(null)
    }
}