package hr.ferit.matijasokol.sjedni5.ui.fragments.info

import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient(private val url: String) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (this.url == url) {
                view?.loadUrl(url)
            }
        }

        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request?.url?.toString()?.let { url ->
                if (this.url == url) {
                    view?.loadUrl(url)
                }
            }
        }

        return true
    }
}