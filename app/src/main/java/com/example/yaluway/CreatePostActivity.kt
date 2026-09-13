package com.example.yaluway

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.yaluway.data.PostRepository
import com.example.yaluway.model.MyCredentials
import com.example.yaluway.model.PostCategory
import com.example.yaluway.util.FormUi
import com.example.yaluway.util.ImageBinder
import com.example.yaluway.util.SriLankaAreas
import com.example.yaluway.util.Validators
import com.google.android.material.button.MaterialButton

class CreatePostActivity : AppCompatActivity() {

    private var editingId: Int = -1
    private var imagePath: String = ""
    private val dirty = mutableSetOf<Int>()
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtArea: AutoCompleteTextView
    private lateinit var edtContact: EditText
    private lateinit var btnPublish: MaterialButton

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val saved = ImageBinder.saveFromUri(this, uri) ?: return@registerForActivityResult
        imagePath = saved
        ImageBinder.bind(findViewById(R.id.imgPostPhoto), imagePath, R.drawable.yaluway_onboard_market)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        editingId = intent.getIntExtra(LoginActivity.EXTRA_POST_ID, -1)
        val existing = if (editingId > 0) PostRepository.findById(editingId) else null

        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        edtArea = findViewById(R.id.edtArea)
        edtContact = findViewById(R.id.edtContact)
        val rgCategory = findViewById<RadioGroup>(R.id.rgCategory)
        val rgMode = findViewById<RadioGroup>(R.id.rgMode)
        btnPublish = findViewById(R.id.btnPublish)
        FormUi.bindAreaPicker(edtArea)

        if (Validators.isKnownArea(MyCredentials.area)) {
            edtArea.setText(SriLankaAreas.canonical(MyCredentials.area))
        }
        if (MyCredentials.username.isNotBlank() && edtContact.text.isNullOrBlank()) {
            edtContact.hint = getString(R.string.contact_phone_hint)
        }

        if (existing != null) {
            btnPublish.setText(R.string.edit_post)
            edtTitle.setText(existing.title)
            edtDescription.setText(existing.description)
            edtArea.setText(existing.area)
            edtContact.setText(Validators.digitsOnly(existing.contact))
            imagePath = existing.imagePath
            rgCategory.check(
                when (existing.category) {
                    PostCategory.HELP -> R.id.rbHelp
                    PostCategory.SERVICES -> R.id.rbServices
                    PostCategory.MARKETPLACE -> R.id.rbMarketplace
                }
            )
            rgMode.check(
                when {
                    existing.mode.contains("LEND", true) -> R.id.rbLend
                    existing.mode.contains("DONATE", true) -> R.id.rbDonate
                    existing.mode.contains("FREE", true) -> R.id.rbFree
                    else -> R.id.rbBorrow
                }
            )
        }

        ImageBinder.bind(
            findViewById(R.id.imgPostPhoto),
            imagePath,
            defaultPhoto(selectedCategory())
        )

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnPickPhoto).setOnClickListener {
            pickPhoto.launch("image/*")
        }
        findViewById<ImageView>(R.id.imgPostPhoto).setOnClickListener {
            pickPhoto.launch("image/*")
        }
        btnPublish.setOnClickListener { publish() }

        listOf(edtTitle, edtDescription, edtArea, edtContact).forEach { field ->
            FormUi.onTextChange(field) {
                dirty.add(field.id)
                refreshForm()
            }
            field.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    dirty.add(field.id)
                    refreshForm()
                } else if (field.id == R.id.edtArea) {
                    edtArea.showDropDown()
                }
            }
        }
        refreshForm()

        rgCategory.setOnCheckedChangeListener { group, checkedId ->
            styleChips(group, checkedId, yellow = false)
            updateModeSection()
            if (imagePath.isBlank()) {
                ImageBinder.bind(
                    findViewById(R.id.imgPostPhoto),
                    "",
                    defaultPhoto(selectedCategory())
                )
            }
        }
        rgMode.setOnCheckedChangeListener { group, checkedId ->
            styleChips(group, checkedId, yellow = true)
            updateModeHelp()
        }
        styleChips(rgCategory, rgCategory.checkedRadioButtonId, yellow = false)
        styleChips(rgMode, rgMode.checkedRadioButtonId, yellow = true)
        updateModeSection()
    }

    private fun selectedCategory(): PostCategory {
        return when (findViewById<RadioGroup>(R.id.rgCategory).checkedRadioButtonId) {
            R.id.rbServices -> PostCategory.SERVICES
            R.id.rbHelp -> PostCategory.HELP
            else -> PostCategory.MARKETPLACE
        }
    }

    private fun defaultPhoto(category: PostCategory): Int = when (category) {
        PostCategory.HELP -> R.drawable.yaluway_onboard_help
        PostCategory.SERVICES -> R.drawable.yaluway_onboard_services
        PostCategory.MARKETPLACE -> R.drawable.yaluway_onboard_market
    }

    private fun updateModeSection() {
        val marketplace = selectedCategory() == PostCategory.MARKETPLACE
        findViewById<View>(R.id.modeSection).visibility =
            if (marketplace) View.VISIBLE else View.GONE
        if (marketplace) updateModeHelp()
    }

    private fun updateModeHelp() {
        val help = findViewById<TextView>(R.id.tvModeHelp)
        help.setText(
            when (findViewById<RadioGroup>(R.id.rgMode).checkedRadioButtonId) {
                R.id.rbLend -> R.string.mode_help_lend
                R.id.rbDonate -> R.string.mode_help_donate
                R.id.rbFree -> R.string.mode_help_free
                else -> R.string.mode_help_borrow
            }
        )
    }

    private fun styleChips(group: RadioGroup, checkedId: Int, yellow: Boolean) {
        for (i in 0 until group.childCount) {
            val button = group.getChildAt(i) as RadioButton
            val selected = button.id == checkedId
            button.setBackgroundResource(
                when {
                    selected && yellow -> R.drawable.bg_chip_yellow
                    selected -> R.drawable.bg_chip_selected
                    else -> R.drawable.bg_chip
                }
            )
            button.setTextColor(
                getColor(if (selected && !yellow) R.color.white else R.color.yalu_navy)
            )
        }
    }

    private fun refreshForm() {
        FormUi.showError(edtTitle, findViewById(R.id.tvErrorTitle), titleError()?.takeIf { dirty.contains(edtTitle.id) })
        FormUi.showError(edtDescription, findViewById(R.id.tvErrorDescription), descriptionError()?.takeIf { dirty.contains(edtDescription.id) })
        FormUi.showError(edtArea, findViewById(R.id.tvErrorArea), areaError()?.takeIf { dirty.contains(edtArea.id) })
        FormUi.showError(edtContact, findViewById(R.id.tvErrorContact), phoneError()?.takeIf { dirty.contains(edtContact.id) })
        FormUi.setEnabled(
            btnPublish,
            titleError() == null && descriptionError() == null && areaError() == null && phoneError() == null
        )
    }

    private fun titleError(): String? {
        val title = edtTitle.text.toString().trim()
        return when {
            title.isEmpty() -> getString(R.string.error_required)
            !Validators.isValidTitle(title) -> getString(R.string.error_title)
            else -> null
        }
    }

    private fun descriptionError(): String? {
        val description = edtDescription.text.toString().trim()
        return when {
            description.isEmpty() -> getString(R.string.error_required)
            !Validators.isValidDescription(description) -> getString(R.string.error_description)
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

    private fun phoneError(): String? {
        val phone = edtContact.text.toString().trim()
        return when {
            phone.isEmpty() -> getString(R.string.error_required)
            !Validators.isValidPhone(phone) -> getString(R.string.error_phone)
            else -> null
        }
    }

    private fun publish() {
        dirty.addAll(listOf(edtTitle.id, edtDescription.id, edtArea.id, edtContact.id))
        refreshForm()
        if (titleError() != null || descriptionError() != null || areaError() != null || phoneError() != null) {
            return
        }

        val title = edtTitle.text.toString().trim()
        val description = edtDescription.text.toString().trim()
        val area = SriLankaAreas.canonical(edtArea.text.toString())
        val contact = Validators.digitsOnly(edtContact.text.toString())

        val category = selectedCategory()
        val mode = if (category == PostCategory.MARKETPLACE) {
            when (findViewById<RadioGroup>(R.id.rgMode).checkedRadioButtonId) {
                R.id.rbLend -> "LEND"
                R.id.rbDonate -> "DONATE"
                R.id.rbFree -> "FREE"
                else -> "BORROW"
            }
        } else {
            ""
        }

        if (editingId > 0) {
            PostRepository.update(
                id = editingId,
                title = title,
                description = description,
                category = category,
                area = area,
                contact = contact,
                price = "",
                mode = mode,
                imagePath = imagePath
            )
            Toast.makeText(this, getString(R.string.post_updated), Toast.LENGTH_SHORT).show()
        } else {
            PostRepository.add(
                title = title,
                description = description,
                category = category,
                area = area,
                contact = contact,
                price = "",
                mode = mode,
                imagePath = imagePath
            )
            Toast.makeText(this, getString(R.string.post_published), Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
