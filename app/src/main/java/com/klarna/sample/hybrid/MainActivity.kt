package com.klarna.sample.hybrid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setClickListeners()
    }

    private fun setClickListeners() {
        buttonKlarnaDemo.setOnClickListener {
            startSDK("https://www.klarna.com/demo/")
        }

        buttonCustomUrl.setOnClickListener {
            startSDK(null)
        }
    }

    private fun startSDK(url: String?) {
        Intent(this, WebViewActivity::class.java)
            .apply {
                putExtra(WebViewActivity.KEY_URL, url)
                startActivity(this)
            }
    }
}
