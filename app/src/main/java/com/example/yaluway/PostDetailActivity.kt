package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.yaluway.data.PostRepository
import com.example.yaluway.model.MyCredentials
import com.example.yaluway.model.PostCategory
import com.example.yaluway.util.ImageBinder
import com.google.android.material.button.MaterialButton

class PostDetailActivity : AppCompatActivity() {

    private var postId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        postId = intent.getIntExtra(LoginActivity.EXTRA_POST_ID, -1)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        bind()
    }

    override fun onResume() {
        super.onResume()
        if (postId > 0) bind()
    }

    private fun bind() {
        val post = PostRepository.findById(postId)
        if (post == null) {
            finish()
            return
        }

        val fallback = when (post.category) {
            PostCategory.HELP -> R.drawable.yaluway_onboard_help
            PostCategory.SERVICES -> R.drawable.yaluway_onboard_services
            PostCategory.MARKETPLACE -> R.drawable.yaluway_hero_kettle
        }
        ImageBinder.bind(findViewById(R.id.imgDetailHero), post.imagePath, fallback)
        ImageBinder.bind(findViewById(R.id.imgDetailThumb), post.imagePath, fallback)

        findViewById<TextView>(R.id.tvDetailTitle).text = post.title
        findViewById<TextView>(R.id.tvDetailBadge).text =
            post.mode.ifBlank { post.category.badge }
        findViewById<TextView>(R.id.tvDetailDescription).text = post.description
        findViewById<TextView>(R.id.tvDetailMeta).text = getString(
            R.string.posted_meta,
            post.area,
            post.owner.ifBlank { MyCredentials.username.ifBlank { "Neighbour" } }
        )

        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val btnRequest = findViewById<MaterialButton>(R.id.btnSendRequest)
        val tvStatus = findViewById<TextView>(R.id.tvRequestStatus)
        val tvHint = findViewById<TextView>(R.id.tvRequestHint)
        val btnViewRequests = findViewById<MaterialButton>(R.id.btnViewRequests)

        btnSave.text = if (PostRepository.isSaved(post.id)) getString(R.string.unsave) else getString(R.string.save)

        if (post.mine) {
            btnRequest.visibility = View.GONE
            tvStatus.visibility = View.VISIBLE
            tvStatus.text = getString(R.string.your_post)
            tvHint.visibility = View.GONE
            val pending = PostRepository.pendingForPost(post.id)
            btnViewRequests.visibility = View.VISIBLE
            btnViewRequests.text = getString(R.string.view_requests, pending)
            btnViewRequests.setOnClickListener {
                val intent = Intent(this, RequestsActivity::class.java)
                intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
                startActivity(intent)
            }
        } else {
            btnRequest.visibility = View.VISIBLE
            btnViewRequests.visibility = View.GONE
            when (PostRepository.requestStatus(post.id)) {
                "PENDING" -> {
                    btnRequest.isEnabled = false
                    btnRequest.text = getString(R.string.already_requested)
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = getString(R.string.request_pending)
                    tvHint.visibility = View.VISIBLE
                }
                "ACCEPTED" -> {
                    btnRequest.isEnabled = false
                    btnRequest.text = getString(R.string.request_done)
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = getString(R.string.request_accepted, post.contact)
                    tvHint.visibility = View.GONE
                }
                "DECLINED" -> {
                    btnRequest.isEnabled = false
                    btnRequest.text = getString(R.string.request_declined)
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = getString(R.string.request_declined)
                    tvHint.visibility = View.GONE
                }
                else -> {
                    btnRequest.isEnabled = true
                    btnRequest.text = getString(R.string.send_request)
                    tvStatus.visibility = View.GONE
                    tvHint.visibility = View.GONE
                }
            }
        }

        btnRequest.setOnClickListener {
            if (PostRepository.sendRequest(post.id)) {
                Toast.makeText(this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show()
                bind()
            } else {
                Toast.makeText(this, getString(R.string.already_requested), Toast.LENGTH_SHORT).show()
            }
        }
        btnSave.setOnClickListener {
            val saved = PostRepository.toggleSave(post.id)
            btnSave.text = if (saved) getString(R.string.unsave) else getString(R.string.save)
            Toast.makeText(
                this,
                if (saved) getString(R.string.post_saved) else getString(R.string.save),
                Toast.LENGTH_SHORT
            ).show()
        }

        val ownerActions = findViewById<View>(R.id.ownerActions)
        ownerActions.visibility = if (post.mine) View.VISIBLE else View.GONE
        findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
            startActivity(intent)
        }
        findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.delete_post)
                .setMessage(post.title)
                .setPositiveButton(R.string.delete_post) { _, _ ->
                    PostRepository.delete(post.id)
                    Toast.makeText(this, getString(R.string.post_deleted), Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}
