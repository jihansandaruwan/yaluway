package com.example.yaluway.util

/** Shared register, login, and create-post rules. The UI shows red errors live. */
object Validators {

    private val namePattern = Regex("^[A-Za-z]+([ .'-][A-Za-z]+)*$")
    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun passwordsMatch(password: String, confirm: String): Boolean {
        return password.isNotBlank() && password == confirm
    }

    fun isStrongPassword(password: String): Boolean = password.length >= 8

    fun isValidEmail(email: String): Boolean {
        return emailPattern.matches(email.trim())
    }

    fun isValidFullName(name: String): Boolean {
        val value = name.trim()
        return value.length >= 2 && namePattern.matches(value) && value.none { it.isDigit() }
    }

    fun isValidPhone(phone: String): Boolean {
        val digits = digitsOnly(phone)
        return digits.length == 10 && digits.startsWith("0")
    }

    fun digitsOnly(value: String): String = value.filter { it.isDigit() }

    fun isValidTitle(title: String): Boolean {
        val value = title.trim()
        return value.length >= 3 && value.any { it.isLetter() }
    }

    fun isValidDescription(description: String): Boolean {
        return description.trim().length >= 10
    }

    fun isKnownArea(area: String): Boolean = SriLankaAreas.isKnown(area)

    fun allFilled(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() }
    }
}
