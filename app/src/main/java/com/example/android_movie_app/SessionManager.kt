package com.example.android_movie_app

import android.content.Context
import android.content.SharedPreferences
class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "app_prefs"
        private const val KEY_USER_ID = "user_id"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1) // -1 = chưa đăng nhập
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}