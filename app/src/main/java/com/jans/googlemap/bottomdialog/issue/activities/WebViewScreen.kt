package com.jans.googlemap.bottomdialog.issue.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jans.googlemap.bottomdialog.issue.R


class WebViewScreen : AppCompatActivity() {

    private val url = "https://markgroeningen.ris-portal.de/?appv3mode=yes"
    private lateinit var webView:WebView


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_screen)

        webView = findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        val progressBar: LinearLayout = findViewById(R.id.idLoader)
        webView.webViewClient = AppWebViewClients(progressBar,webView)
        webView.loadUrl(url)


        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

            Log.d("down123",
            "${url} ${mimetype} ${contentLength}")

            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack() === true) {
            webView.goBack()
        } else {
            finish()
        }
    }

    class AppWebViewClients(private val progressBar: LinearLayout,
                            private val webView: WebView) : WebViewClient() {
        init {
            progressBar.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return if (url.startsWith("tel:")) {
                val tel = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                webView.context.startActivity(tel)
                true
            } else if (url.contains("mailto:")) {
                view.context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
                true
            } else {
                view.loadUrl(url)
                true
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
            webView.visibility = View.VISIBLE

        }
    }




}