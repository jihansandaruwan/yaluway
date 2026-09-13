package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.yaluway.data.PostRepository
import com.example.yaluway.data.SessionManager
import com.example.yaluway.data.UserRepository
import com.example.yaluway.model.MyCredentials
import com.example.yaluway.util.FormUi
import com.example.yaluway.util.ImageBinder
import com.example.yaluway.util.SriLankaAreas
import com.example.yaluway.util.Validators

class ProfileFragment : Fragment() {

    private val pickAvatar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val path = ImageBinder.saveFromUri(requireContext(), uri) ?: return@registerForActivityResult
        UserRepository.updateAvatar(path)
        view?.let { bind(it) }
        Toast.makeText(requireContext(), getString(R.string.photo_updated), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind(view)

        view.findViewById<ImageView>(R.id.imgProfile).apply {
            clipToOutline = true
            setOnClickListener { pickAvatar.launch("image/*") }
        }
        view.findViewById<View>(R.id.rowMyPosts).setOnClickListener {
            (activity as? HomeActivity)?.openMyPostsTab()
        }
        view.findViewById<View>(R.id.rowRequests).setOnClickListener {
            startActivity(Intent(requireContext(), RequestsActivity::class.java))
        }
        view.findViewById<View>(R.id.rowSaved).setOnClickListener {
            startActivity(Intent(requireContext(), SavedPostsActivity::class.java))
        }
        view.findViewById<View>(R.id.rowArea).setOnClickListener { changeArea() }
        view.findViewById<View>(R.id.rowLogout).setOnClickListener {
            SessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { bind(it) }
    }

    private fun bind(view: View) {
        val name = MyCredentials.username.ifBlank { "Neighbour" }
        val area = MyCredentials.area.ifBlank { getString(R.string.area_hint) }
        view.findViewById<TextView>(R.id.tvProfileName).text = name
        view.findViewById<TextView>(R.id.tvProfileArea).text =
            getString(R.string.profile_area_line, area)
        view.findViewById<TextView>(R.id.tvStatPosts).text = PostRepository.mine().size.toString()
        view.findViewById<TextView>(R.id.tvStatSaved).text = PostRepository.savedCount().toString()
        val pending = PostRepository.pendingRequestCount()
        view.findViewById<TextView>(R.id.tvRequestsLabel).text =
            if (pending > 0) "${getString(R.string.requests)} ($pending)" else getString(R.string.requests)
        val img = view.findViewById<ImageView>(R.id.imgProfile)
        img.clipToOutline = true
        ImageBinder.bind(img, MyCredentials.avatarPath, R.drawable.yaluway_avatar)
    }

    private fun changeArea() {
        val input = AutoCompleteTextView(requireContext())
        input.setText(MyCredentials.area)
        input.hint = getString(R.string.area_hint)
        input.setPadding(48, 32, 48, 32)
        FormUi.bindAreaPicker(input)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.change_area)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val area = input.text.toString().trim()
                if (!Validators.isKnownArea(area)) {
                    Toast.makeText(requireContext(), getString(R.string.error_area), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                UserRepository.updateArea(SriLankaAreas.canonical(area))
                view?.let { bind(it) }
                Toast.makeText(requireContext(), getString(R.string.area_updated), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
