package com.app.k2t.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PrefsHelper {
    private const val PREFS_NAME = "k2t_prefs"
    private const val KEY_USER_ROLE = "user_role"

    fun saveUserRole(context: Context, role: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_USER_ROLE, role) }
    }

    fun getUserRole(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun clearUserRole(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_USER_ROLE) }
    }
}

