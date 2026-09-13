package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.adapter.PostAdapter
import com.example.yaluway.data.PostRepository

class MyPostsFragment : Fragment() {

    private lateinit var adapter: PostAdapter
    private lateinit var empty: View
    private lateinit var list: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my_posts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        empty = view.findViewById(R.id.emptyState)
        list = view.findViewById(R.id.rvMyPosts)
        adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(requireContext(), PostDetailActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
            startActivity(intent)
        }
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (!this::adapter.isInitialized) return
        val posts = PostRepository.mine()
        adapter.submitList(posts)
        empty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        list.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
    }
}
