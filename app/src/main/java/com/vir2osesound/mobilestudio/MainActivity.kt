package com.vir2osesound.mobilestudio

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var errorView: View? = null
    private val liveUrl = "https://mobile-studio.waldemar-krucinski.com/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make activity edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.parseColor("#0A0C10")
        window.navigationBarColor = Color.parseColor("#0A0C10")

        // Root Layout
        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#0A0C10"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // WebView with hardware acceleration
        webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.TRANSPARENT)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        setupWebSettings()
        setupAudioEngine()

        // Loading Indicator
        progressBar = ProgressBar(this).apply {
            val size = (48 * resources.displayMetrics.density).toInt()
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = android.view.Gravity.CENTER
            }
            visibility = View.VISIBLE
        }

        rootLayout.addView(webView)
        rootLayout.addView(progressBar)
        setContentView(rootLayout)

        applyImmersiveMode(resources.configuration.orientation)

        // Native JS Bridge for Haptics & Native Audio Controls
        webView.addJavascriptInterface(WebAppInterface(this), "nativeAudio")
        webView.addJavascriptInterface(WebAppInterface(this), "haptic")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                errorView?.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    progressBar.visibility = View.GONE
                    showErrorView(rootLayout)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 80) {
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Back button support
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        webView.loadUrl(liveUrl)
    }

    private fun setupWebSettings() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
        }
    }

    private fun setupAudioEngine() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .build()

            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyImmersiveMode(newConfig.orientation)
    }

    private fun applyImmersiveMode(orientation: Int) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Full Hardware Immersion for Groovebox Landscape Mode
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // Portrait: Show status bar cleanly
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun showErrorView(rootLayout: FrameLayout) {
        if (errorView != null) {
            errorView?.visibility = View.VISIBLE
            return
        }

        val density = resources.displayMetrics.density
        val container = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#0A0C10"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val textLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
                setMargins((24 * density).toInt(), 0, (24 * density).toInt(), 0)
            }
        }

        val title = TextView(this).apply {
            text = "BEATCLOUD PRO AUDIO"
            setTextColor(Color.parseColor("#C7B584"))
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "Verbindung wird überprüft...\nBitte überprüfe deine Internetverbindung."
            setTextColor(Color.WHITE)
            alpha = 0.85f
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setPadding(0, (12 * density).toInt(), 0, (20 * density).toInt())
        }

        val retryBtn = Button(this).apply {
            text = "ERNEUT VERBINDEN"
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.parseColor("#C7B584"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setOnClickListener {
                container.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                webView.loadUrl(liveUrl)
            }
        }

        textLayout.addView(title)
        textLayout.addView(subtitle)
        textLayout.addView(retryBtn)
        container.addView(textLayout)

        this.errorView = container
        rootLayout.addView(container)
    }

    class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun postMessage(type: String) {
            triggerHaptic(type)
        }

        @JavascriptInterface
        fun triggerHaptic(type: String) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type.lowercase()) {
                    "heavy" -> VibrationEffect.createOneShot(25, 255)
                    "light" -> VibrationEffect.createOneShot(10, 100)
                    else -> VibrationEffect.createOneShot(15, 180)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(15)
            }
        }
    }
}
