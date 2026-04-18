package com.example.snapchatremix

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var screenshotDetector: ScreenshotDetector
    private var isScreenshotActive by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startScreenshotDetector()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.restoreTheme(this)

        val permission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            startScreenshotDetector()
        } else {
            permissionLauncher.launch(permission)
        }

        setContent {
            SnapRemixApp(isScreenshotActive = isScreenshotActive)
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

    override fun onDestroy() {
        super.onDestroy()
        if (::screenshotDetector.isInitialized) {
            screenshotDetector.stop()
        }
    }
}