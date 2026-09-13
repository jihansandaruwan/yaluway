package com.example.yaluway.data

import android.content.Context
import com.example.yaluway.model.MyCredentials

/** Keeps the logged-in email in SharedPreferences so the session survives app restarts. */
object SessionManager {
    private const val PREFS = "yaluway_session"
    private const val KEY_EMAIL = "logged_in_email"
    private const val KEY_LAST_EMAIL = "last_email"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        restore()
    }

    fun login(email: String) {
        prefs().edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_LAST_EMAIL, email)
            .commit()
    }

    fun logout() {
        prefs().edit()
            .remove(KEY_EMAIL)
            .commit()
        MyCredentials.clear()
    }

    fun currentEmail(): String = prefs().getString(KEY_EMAIL, "").orEmpty()

    fun lastEmail(): String = prefs().getString(KEY_LAST_EMAIL, "").orEmpty()

    fun isLoggedIn(): Boolean {
        restore()
        val email = currentEmail()
        return email.isNotBlank() && UserRepository.find(email) != null
    }

    fun restore() {
        val email = currentEmail()
        if (email.isBlank() || !this::appContext.isInitialized) return
        UserRepository.find(email)?.let { UserRepository.applyToSession(it) }
    }

    private fun prefs() = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
