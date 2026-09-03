package com.realeapp.util

import android.util.Log

/**
 * Centralized logger.
 *
 * Design goals:
 *  - Every log line goes to logcat with a tag prefixed by [APP_TAG], so the whole
 *    app can be filtered with a single logcat filter: `tag:appTags*`.
 *  - Class and function names are auto-derived from the call site's stack frame,
 *    so callers do not need to pass them manually. Format:
 *        Logcat tag:   appTags:ClassName
 *        Logcat body:  methodName() -> message
 *  - Backward compatible with the previous `Logger.d(TAG, message)` API. If a
 *    caller passes an explicit tag it overrides the auto-detected class name.
 */
object Logger {

    /** Unique parent prefix – single point of change for logcat filtering. */
    private const val APP_TAG = "appTags"

    private const val MAX_TAG_LENGTH = 60

    // region public API – no explicit tag (class + function are auto-detected)
    fun d(message: String) = log(Log.DEBUG, null, message, null)
    fun i(message: String) = log(Log.INFO, null, message, null)
    fun w(message: String) = log(Log.WARN, null, message, null)
    fun e(message: String, throwable: Throwable? = null) = log(Log.ERROR, null, message, throwable)
    // endregion

    // region public API – explicit tag (kept for backward compatibility)
    fun d(tag: String, message: String) = log(Log.DEBUG, tag, message, null)
    fun i(tag: String, message: String) = log(Log.INFO, tag, message, null)
    fun w(tag: String, message: String) = log(Log.WARN, tag, message, null)
    fun e(tag: String, message: String, throwable: Throwable? = null) = log(Log.ERROR, tag, message, throwable)
    // endregion

    private fun log(priority: Int, explicitTag: String?, message: String, throwable: Throwable?) {
        val caller = resolveCaller()
        val className = explicitTag ?: caller?.className ?: "Unknown"
        val methodName = caller?.methodName ?: "?"

        val fullTag = buildTag(className)
        val body = "$methodName() -> $message"

        if (throwable != null) {
            Log.println(priority, fullTag, body + '\n' + Log.getStackTraceString(throwable))
        } else {
            Log.println(priority, fullTag, body)
        }
    }

    private fun buildTag(className: String): String {
        val raw = "$APP_TAG:$className"
        return if (raw.length <= MAX_TAG_LENGTH) raw else raw.substring(0, MAX_TAG_LENGTH)
    }

    /**
     * Walks the stack trace and returns the first frame that is not inside [Logger].
     * Returns a normalized (className, methodName) pair – nested/anonymous class
     * suffixes and Kotlin lambda mangling are stripped so tags stay readable.
     */
    private fun resolveCaller(): CallerInfo? {
        val loggerName = Logger::class.java.name
        val stack = Throwable().stackTrace
        for (frame in stack) {
            val cls = frame.className
            if (cls == loggerName || cls.startsWith("$loggerName$")) continue
            val simple = cls.substringAfterLast('.').substringBefore('$')
            val method = frame.methodName.let {
                when {
                    it == "<init>" -> "init"
                    it.startsWith("invokeSuspend") -> "invokeSuspend"
                    it.startsWith("access$") -> it.removePrefix("access$")
                    else -> it
                }
            }
            return CallerInfo(simple, method)
        }
        return null
    }

    private data class CallerInfo(val className: String, val methodName: String)
}
