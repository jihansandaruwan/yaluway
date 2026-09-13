package com.example.yaluway

import android.app.Application
import com.example.yaluway.data.PostRepository
import com.example.yaluway.data.SessionManager
import com.example.yaluway.data.UserRepository

/** Starts Room repositories and restores the last login before any screen opens. */
class YaluwayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.init(this)
        PostRepository.init(this)
        SessionManager.init(this)
    }
}
