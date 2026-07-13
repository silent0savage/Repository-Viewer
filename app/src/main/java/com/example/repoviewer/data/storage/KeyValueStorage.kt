package com.example.repoviewer.data.storage

import android.content.Context
import androidx.core.content.edit

class KeyValueStorage(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }
    fun getToken(): String? = prefs.getString("auth_token", null)
    fun clearToken() = prefs.edit { remove("auth_token") }
}