package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yaluway.data.SessionManager
import com.example.yaluway.data.UserRepository
import com.example.yaluway.util.FormUi
import com.example.yaluway.util.Validators
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private val dirty = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val lastEmail = SessionManager.lastEmail()
        if (lastEmail.isNotBlank()) {
            edtEmail.setText(lastEmail)
        }

        findViewById<LinearLayout>(R.id.tvCreateAccount).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btnLogin.setOnClickListener { login() }

        listOf(edtEmail, edtPassword).forEach { field ->
            FormUi.onTextChange(field) {
                dirty.add(field.id)
                refresh()
            }
            field.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    dirty.add(field.id)
                    refresh()
                }
            }
        }
        refresh()
    }

    private fun refresh() {
        FormUi.showError(edtEmail, findViewById(R.id.tvErrorEmail), emailError()?.takeIf { dirty.contains(edtEmail.id) })
        FormUi.showError(edtPassword, findViewById(R.id.tvErrorPassword), passwordError()?.takeIf { dirty.contains(edtPassword.id) })
        FormUi.setEnabled(btnLogin, emailError() == null && passwordError() == null)
    }

    private fun emailError(): String? {
        val email = edtEmail.text.toString().trim()
        return when {
            email.isEmpty() -> getString(R.string.error_required)
            !Validators.isValidEmail(email) -> getString(R.string.error_email)
            else -> null
        }
    }

    private fun passwordError(): String? {
        val password = edtPassword.text.toString()
        return if (password.isEmpty()) getString(R.string.error_required) else null
    }

    private fun login() {
        dirty.addAll(listOf(edtEmail.id, edtPassword.id))
        refresh()
        if (emailError() != null || passwordError() != null) return

        if (UserRepository.login(edtEmail.text.toString().trim(), edtPassword.text.toString())) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            FormUi.showError(edtPassword, findViewById(R.id.tvErrorPassword), getString(R.string.login_failed))
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val EXTRA_USER_NAME = "user_name"
        const val EXTRA_POST_ID = "post_id"
        const val EXTRA_CATEGORY = "category"
    }
}
