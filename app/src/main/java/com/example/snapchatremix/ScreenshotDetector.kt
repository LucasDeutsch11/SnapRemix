package com.example.snapchatremix

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore

class ScreenshotDetector(
    private val contentResolver: ContentResolver,
    private val onScreenshotTaken: () -> Unit
) {
    private var observer: ContentObserver? = null

    fun start() {
        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                uri ?: return
                val projection = arrayOf(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA
                )

                contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                        val name = if (nameCol >= 0) {
                            cursor.getString(nameCol)?.lowercase() ?: ""
                        } else {
                            ""
                        }

                        if ("screenshot" in name || "screen_shot" in name || "screen shot" in name) {
                            onScreenshotTaken()
                        }
                    }
                }
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer!!
        )
    }

    fun stop() {
        observer?.let { contentResolver.unregisterContentObserver(it) }
        observer = null
    }
}