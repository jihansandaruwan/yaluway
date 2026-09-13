package com.example.yaluway.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.example.yaluway.R
import com.google.android.material.button.MaterialButton

object FormUi {

    fun bindAreaPicker(field: AutoCompleteTextView) {
        field.setAdapter(
            ArrayAdapter(
                field.context,
                android.R.layout.simple_dropdown_item_1line,
                SriLankaAreas.all
            )
        )
        field.threshold = 1
        field.setOnClickListener { field.showDropDown() }
        field.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) field.showDropDown()
        }
    }

    fun onTextChange(field: EditText, onChange: () -> Unit) {
        field.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onChange()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    fun showError(field: View, errorView: TextView, message: String?) {
        if (message == null) {
            errorView.visibility = View.GONE
            errorView.text = ""
            field.setBackgroundResource(R.drawable.bg_input)
        } else {
            errorView.visibility = View.VISIBLE
            errorView.text = message
            field.setBackgroundResource(R.drawable.bg_input_error)
        }
    }

    fun setEnabled(button: MaterialButton, enabled: Boolean) {
        button.isEnabled = enabled
        button.alpha = if (enabled) 1f else 0.45f
    }
}
