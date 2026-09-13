package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.adapter.PostAdapter
import com.example.yaluway.data.PostRepository
import com.example.yaluway.model.PostCategory

class CategoryActivity : AppCompatActivity() {

    private lateinit var adapter: PostAdapter
    private lateinit var category: PostCategory
    private var helpChip: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        category = runCatching {
            PostCategory.valueOf(intent.getStringExtra(LoginActivity.EXTRA_CATEGORY) ?: "HELP")
        }.getOrDefault(PostCategory.HELP)

        val title = when (category) {
            PostCategory.HELP -> getString(R.string.help_board)
            PostCategory.SERVICES -> getString(R.string.tab_services)
            PostCategory.MARKETPLACE -> getString(R.string.tab_marketplace)
        }
        findViewById<TextView>(R.id.tvCategoryTitle).text = title
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
            startActivity(intent)
        }
        val rv = findViewById<RecyclerView>(R.id.rvCategoryPosts)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        if (category == PostCategory.HELP) {
            findViewById<View>(R.id.chipRow).visibility = View.VISIBLE
            findViewById<View>(R.id.chipAll).setOnClickListener { selectChip(null) }
            findViewById<View>(R.id.chipUrgent).setOnClickListener { selectChip("urgent") }
            findViewById<View>(R.id.chipVolunteer).setOnClickListener { selectChip("volunteer") }
        }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        if (this::adapter.isInitialized) refresh()
    }

    private fun selectChip(chip: String?) {
        helpChip = chip
        styleChip(R.id.chipAll, chip == null)
        styleChip(R.id.chipUrgent, chip == "urgent")
        styleChip(R.id.chipVolunteer, chip == "volunteer")
        refresh()
    }

    private fun styleChip(id: Int, selected: Boolean) {
        val view = findViewById<TextView>(id)
        view.setBackgroundResource(if (selected) R.drawable.bg_chip_selected else R.drawable.bg_chip)
        view.setTextColor(getColor(if (selected) R.color.white else R.color.yalu_navy))
    }

    private fun refresh() {
        adapter.submitList(PostRepository.search("", category, helpChip))
    }
}
