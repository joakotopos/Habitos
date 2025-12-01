package com.example.habitos

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "HabitosSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
    
    fun saveSession(userId: String, accessToken: String, refreshToken: String, email: String, name: String?) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
        }
    }
    
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    
    fun isLoggedIn(): Boolean = getUserId() != null && getAccessToken() != null
    
    fun clearSession() {
        prefs.edit {
            clear()
        }
    }
}

