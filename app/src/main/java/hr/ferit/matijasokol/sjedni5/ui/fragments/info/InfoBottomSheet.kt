package hr.ferit.matijasokol.sjedni5.ui.fragments.info

import android.content.Context
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.gone
import kotlinx.android.synthetic.main.bottom_sheet_webview.view.*

class InfoBottomSheet(context: Context) : FrameLayout(context) {

    private val mBottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context)
    private var mCurrentWebViewScrollY = 0

    init {
        inflateLayout(context)
        setupBottomSheetBehaviour()
        setupWebView()
    }

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.bottom_sheet_webview, this)

        mBottomSheetDialog.setContentView(this)

        mBottomSheetDialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
    }

    private fun setupBottomSheetBehaviour() {
        (parent as? View)?.let { view ->
            BottomSheetBehavior.from(view).let { behaviour ->
                behaviour.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING && mCurrentWebViewScrollY > 0) {
                            behaviour.setState(BottomSheetBehavior.STATE_EXPANDED)
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            close()
                        }
                    }
                })
            }
        }
    }

    private fun setupWebView() {
        webView.apply {
            onScrollChangedCallback = object :
                ObservableWebView.OnScrollChangeListener {
                override fun onScrollChanged(currentHorizontalScroll: Int, currentVerticalScroll: Int,
                                             oldHorizontalScroll: Int, oldcurrentVerticalScroll: Int) {
                    mCurrentWebViewScrollY = currentVerticalScroll
                }
            }
        }
    }

    fun showWithUrl(url: String) {
        mBottomSheetDialog.show()
        webView.apply {
            settings.builtInZoomControls = true
            loadUrl(url)
            webChromeClient = chromeClient
            webViewClient = MyWebViewClient(url)
        }
    }

    fun close() = mBottomSheetDialog.dismiss()

    private val chromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress < 100) {
                webProgressBar?.progress = newProgress
            } else {
                webProgressBar?.gone()
            }
        }
    }


}