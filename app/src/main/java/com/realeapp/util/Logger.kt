package com.realeapp.util

import android.util.Log

object Logger {
    private const val DEFAULT_TAG = "realeAppLogs"
private  const val appTags = "appTags"
    fun d(tag: String = DEFAULT_TAG, message: String) {
        Log.d(tag, appTags +" "+ message)
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        Log.i(tag, appTags +" "+ message)
    }

    fun w(tag: String = DEFAULT_TAG, message: String) {
        Log.w(tag, appTags +" "+ message)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, appTags +" "+ message, throwable)
        } else {
            Log.e(tag,appTags +" "+  message)
        }
    }
}
