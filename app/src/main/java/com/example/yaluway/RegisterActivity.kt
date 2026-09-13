package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yaluway.data.UserRepository
import com.example.yaluway.util.FormUi
import com.example.yaluway.util.SriLankaAreas
import com.example.yaluway.util.Validators
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirm: EditText
    private lateinit var edtArea: AutoCompleteTextView
    private lateinit var btnCreate: MaterialButton
    private val dirty = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edtName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirm = findViewById(R.id.edtConfirmPassword)
        edtArea = findViewById(R.id.edtArea)
        btnCreate = findViewById(R.id.btnCreateAccount)

        FormUi.bindAreaPicker(edtArea)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnCreate.setOnClickListener { register() }

        listOf(edtName, edtEmail, edtPassword, edtConfirm, edtArea).forEach { field ->
            FormUi.onTextChange(field) {
                dirty.add(field.id)
                refresh()
            }
            field.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    dirty.add(field.id)
                    refresh()
                } else if (field.id == R.id.edtArea) {
                    edtArea.showDropDown()
                }
            }
        }
        refresh()
    }

    private fun refresh() {
        FormUi.showError(edtName, findViewById(R.id.tvErrorName), nameError()?.takeIf { dirty.contains(edtName.id) })
        FormUi.showError(edtEmail, findViewById(R.id.tvErrorEmail), emailError()?.takeIf { dirty.contains(edtEmail.id) })
        FormUi.showError(edtPassword, findViewById(R.id.tvErrorPassword), passwordError()?.takeIf { dirty.contains(edtPassword.id) })
        FormUi.showError(edtConfirm, findViewById(R.id.tvErrorConfirm), confirmError()?.takeIf { dirty.contains(edtConfirm.id) })
        FormUi.showError(edtArea, findViewById(R.id.tvErrorArea), areaError()?.takeIf { dirty.contains(edtArea.id) })
        FormUi.setEnabled(
            btnCreate,
            nameError() == null && emailError() == null && passwordError() == null &&
                confirmError() == null && areaError() == null
        )
    }

    private fun nameError(): String? {
        val name = edtName.text.toString().trim()
        return when {
            name.isEmpty() -> getString(R.string.error_required)
            !Validators.isValidFullName(name) -> getString(R.string.error_name)
            else -> null
        }
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
        return when {
            password.isEmpty() -> getString(R.string.error_required)
            !Validators.isStrongPassword(password) -> getString(R.string.error_password)
            else -> null
        }
    }

    private fun confirmError(): String? {
        val confirm = edtConfirm.text.toString()
        return when {
            confirm.isEmpty() -> getString(R.string.error_required)
            !Validators.passwordsMatch(edtPassword.text.toString(), confirm) -> getString(R.string.error_password_match)
            else -> null
        }
    }

    private fun areaError(): String? {
        val area = edtArea.text.toString().trim()
        return when {
            area.isEmpty() -> getString(R.string.error_required)
            !Validators.isKnownArea(area) -> getString(R.string.error_area)
            else -> null
        }
    }

    private fun register() {
        dirty.addAll(listOf(edtName.id, edtEmail.id, edtPassword.id, edtConfirm.id, edtArea.id))
        refresh()
        if (nameError() != null || emailError() != null || passwordError() != null ||
            confirmError() != null || areaError() != null
        ) {
            return
        }

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString()
        val area = SriLankaAreas.canonical(edtArea.text.toString())

        if (!UserRepository.register(name, email, area, password)) {
            dirty.add(edtEmail.id)
            FormUi.showError(edtEmail, findViewById(R.id.tvErrorEmail), getString(R.string.email_already_used))
            FormUi.setEnabled(btnCreate, false)
            return
        }

        UserRepository.login(email, password)
        Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}
