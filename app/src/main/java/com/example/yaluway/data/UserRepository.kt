package com.example.yaluway.data

import android.content.Context
import com.example.yaluway.data.db.UserEntity
import com.example.yaluway.data.db.YaluwayDatabase
import com.example.yaluway.model.MyCredentials

object UserRepository {
    private lateinit var db: YaluwayDatabase
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        db = YaluwayDatabase.get(appContext)
        restoreBackup()
    }

    fun find(email: String): UserEntity? {
        return db.userDao().findByEmail(email.trim().lowercase())
    }

    fun register(username: String, email: String, area: String, password: String): Boolean {
        val key = email.trim().lowercase()
        if (find(key) != null) return false
        db.userDao().insert(
            UserEntity(
                email = key,
                username = username,
                area = area,
                password = password
            )
        )
        saveBackup()
        return true
    }

    fun login(email: String, password: String): Boolean {
        val user = find(email) ?: return false
        if (user.password != password) return false
        applyToSession(user)
        SessionManager.login(user.email)
        return true
    }

    fun updateArea(area: String) {
        val email = SessionManager.currentEmail()
        val user = find(email) ?: return
        db.userDao().update(user.copy(area = area))
        MyCredentials.area = area
        saveBackup()
    }

    fun applyToSession(user: UserEntity) {
        MyCredentials.username = user.username
        MyCredentials.email = user.email
        MyCredentials.area = user.area
        MyCredentials.avatarPath = user.avatarPath
        MyCredentials.setPassword(user.password, user.password)
    }

    fun updateAvatar(path: String) {
        val email = SessionManager.currentEmail()
        val user = find(email) ?: return
        db.userDao().update(user.copy(avatarPath = path))
        MyCredentials.avatarPath = path
        saveBackup()
    }

    private fun restoreBackup() {
        if (db.userDao().count() > 0) {
            saveBackup()
            return
        }
        UserBackup.load(appContext).forEach { db.userDao().upsert(it) }
    }

    private fun saveBackup() {
        if (!this::appContext.isInitialized) return
        UserBackup.save(appContext, db.userDao().getAll())
    }
}
