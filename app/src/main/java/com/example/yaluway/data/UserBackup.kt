package com.example.yaluway.data

import android.content.Context
import com.example.yaluway.data.db.UserEntity
import org.json.JSONArray
import org.json.JSONObject

object UserBackup {
    private const val PREFS = "yaluway_user_backup"
    private const val KEY = "users_json"

    fun save(context: Context, users: List<UserEntity>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(
                JSONObject()
                    .put("email", user.email)
                    .put("username", user.username)
                    .put("area", user.area)
                    .put("password", user.password)
                    .put("avatarPath", user.avatarPath)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, array.toString())
            .commit()
    }

    fun load(context: Context): List<UserEntity> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "")
            .orEmpty()
        if (raw.isBlank()) return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val obj = array.getJSONObject(index)
                UserEntity(
                    email = obj.optString("email"),
                    username = obj.optString("username"),
                    area = obj.optString("area"),
                    password = obj.optString("password"),
                    avatarPath = obj.optString("avatarPath")
                )
            }.filter { it.email.isNotBlank() && it.password.isNotBlank() }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
