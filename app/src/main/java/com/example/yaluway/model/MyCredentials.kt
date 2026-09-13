package com.example.yaluway.model

object MyCredentials {

    var username: String = ""
    var email: String = ""
    var area: String = ""
    var avatarPath: String = ""
    private var password: String = ""

    fun setPassword(password: String, rePassword: String): Boolean {
        if (password.isBlank() || password != rePassword) {
            return false
        }
        this.password = password
        return true
    }

    fun matchesPassword(candidate: String): Boolean {
        return password.isNotBlank() && password == candidate
    }

    fun hasAccount(): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }

    fun clear() {
        username = ""
        email = ""
        area = ""
        avatarPath = ""
        password = ""
    }
}
