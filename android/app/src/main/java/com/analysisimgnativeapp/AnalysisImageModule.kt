package com.analysisimgnativeapp

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class AnalysisImageModule(reactContext: ReactApplicationContext?) :
    ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "AnalysisImageActivity"
    }

    @ReactMethod
    fun open() {
        val intent = Intent(currentActivity, AnalysisImageActivity::class.java)
        currentActivity!!.startActivity(intent)
    }
}
