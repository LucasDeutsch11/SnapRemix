package com.example.snapchatremix

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.yourapp.snapchatremix.ActivityStatusManager
import com.yourapp.snapchatremix.ScreenshotDetector

class MainActivity : ComponentActivity() {
    private lateinit var screenshotDetector: ScreenshotDetector
    private var isScreenshotActive by mutableStateOf(false)

    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { ActivityStatusManager.setIdle() }
    private val IDLE_TIMEOUT_MS = 3 * 60 * 1000L

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startScreenshotDetector()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.restoreTheme(this)

        ActivityStatusManager.setupOnDisconnect()
        ActivityStatusManager.setActive()

        val permission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startScreenshotDetector()
        } else {
            permissionLauncher.launch(permission)
        }

    }

    private fun startScreenshotDetector() {
        screenshotDetector = ScreenshotDetector(contentResolver) {
            runOnUiThread {
                isScreenshotActive = true
                window.decorView.postDelayed({ isScreenshotActive = false }, 4_000)
            }
        }
        screenshotDetector.start()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        idleHandler.removeCallbacks(idleRunnable)
        ActivityStatusManager.setActive()
        idleHandler.postDelayed(idleRunnable, IDLE_TIMEOUT_MS)
    }

    override fun onResume() {
        super.onResume()
        ActivityStatusManager.setActive()
    }

    override fun onStop() {
        super.onStop()
        ActivityStatusManager.setOffline()
        idleHandler.removeCallbacks(idleRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::screenshotDetector.isInitialized) screenshotDetector.stop()
        idleHandler.removeCallbacks(idleRunnable)
    }
}