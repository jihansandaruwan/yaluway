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

class SavedPostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvSaved)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
            startActivity(intent)
        }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val rv = findViewById<RecyclerView>(R.id.rvSaved)
        val empty = findViewById<TextView>(R.id.tvSavedEmpty)
        val posts = PostRepository.savedPosts()
        (rv.adapter as PostAdapter).submitList(posts)
        empty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
    }
}
