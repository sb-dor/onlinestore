package com.sbdor.onlinestoreclaude.core

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "app_prefs"
    }

    // Lazy initialization — SharedPreferences is created only on first access.
    // MODE_PRIVATE = only this app can read/write these preferences.
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // -------------------------------------------------------------------------
    // WRITE — save values by key
    // -------------------------------------------------------------------------

    // edit { } is the Kotlin idiomatic way to apply SharedPreferences changes.
    // It automatically calls apply() (async write) at the end of the block.
    // Use commit() instead of apply() if you need a synchronous write with a result.

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    // -------------------------------------------------------------------------
    // READ — get values by key, with a default fallback
    // -------------------------------------------------------------------------

    // The second argument is the default value returned when the key does not exist.
    // Equivalent to: prefs.getString('key') ?? 'default' in Dart.

    fun getString(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }

    fun getInt(key: String, default: Int = 0): Int {
        return prefs.getInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return prefs.getFloat(key, default)
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return prefs.getLong(key, default)
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    // Removes a single key-value pair.
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    // Removes all stored key-value pairs.
    // Equivalent to: await prefs.clear() in Dart.
    fun clear() {
        prefs.edit().clear().apply()
    }

    // -------------------------------------------------------------------------
    // UTILITY
    // -------------------------------------------------------------------------

    // Check if a key exists before reading it.
    // Useful to distinguish "key not set" from "key set to default value".
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    // Returns all stored key-value pairs as a Map.
    // Useful for debugging — print all stored preferences at once.
    fun getAll(): Map<String, *> {
        return prefs.all
    }
}
