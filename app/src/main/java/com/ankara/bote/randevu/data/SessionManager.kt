package com.ankara.bote.randevu.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bote_session", Context.MODE_PRIVATE)

    var userId: Int
        get() = prefs.getInt("user_id", -1)
        set(v) { prefs.edit().putInt("user_id", v).apply() }

    var userRole: String
        get() = prefs.getString("user_role", "STUDENT") ?: "STUDENT"
        set(v) { prefs.edit().putString("user_role", v).apply() }

    var userNumber: String
        get() = prefs.getString("user_number", "") ?: ""
        set(v) { prefs.edit().putString("user_number", v).apply() }

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(v) { prefs.edit().putString("user_name", v).apply() }

    val isLoggedIn: Boolean
        get() = userId != -1

    val isAcademician: Boolean
        get() = userRole == "ACADEMICIAN"

    fun clear() = prefs.edit().clear().apply()
}