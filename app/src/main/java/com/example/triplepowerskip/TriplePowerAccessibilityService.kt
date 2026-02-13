package com.example.triplepowerskip

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class TriplePowerAccessibilityService : AccessibilityService() {

    private var powerPressCount = 0
    private var firstPressTimeMs = 0L

    private val triggerWindowMs = 1400L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER && event.action == KeyEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()

            if (now - firstPressTimeMs > triggerWindowMs) {
                firstPressTimeMs = now
                powerPressCount = 1
            } else {
                powerPressCount += 1
            }

            if (powerPressCount >= 3) {
                powerPressCount = 0
                firstPressTimeMs = 0L
                skipToNextMedia()
            }
        }

        return super.onKeyEvent(event)
    }

    private fun skipToNextMedia() {
        val manager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val prioritizedPackages = listOf(
            "com.google.android.apps.youtube.music",
            "com.google.android.youtube"
        )

        val activeControllers = manager.getActiveSessions(null)
        val targetController = prioritizedPackages
            .asSequence()
            .mapNotNull { pkg -> activeControllers.firstOrNull { it.packageName == pkg } }
            .firstOrNull()
            ?: activeControllers.firstOrNull()

        if (targetController != null) {
            runCatching {
                targetController.transportControls.skipToNext()
                Log.i(TAG, "Dispatched skipToNext to ${targetController.packageName}")
            }.onFailure {
                Log.w(TAG, "skipToNext failed; falling back to media key", it)
                dispatchMediaNextFallback()
            }
        } else {
            dispatchMediaNextFallback()
        }
    }

    private fun dispatchMediaNextFallback() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val down = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
        val up = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.dispatchMediaKeyEvent(down)
        audioManager.dispatchMediaKeyEvent(up)
        Log.i(TAG, "Dispatched MEDIA_NEXT fallback")
    }

    companion object {
        private const val TAG = "TriplePowerSkip"
    }
}
