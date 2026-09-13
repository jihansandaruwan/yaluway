package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.adapter.PostAdapter
import com.example.yaluway.data.PostRepository
import com.example.yaluway.model.MyCredentials
import com.example.yaluway.model.PostCategory
import com.example.yaluway.util.ImageBinder

class HomeFragment : Fragment() {

    private lateinit var adapter: PostAdapter
    private lateinit var edtSearch: EditText
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindAreaHeader(view)
        val avatar = view.findViewById<ImageView>(R.id.imgUserAvatar)
        avatar.clipToOutline = true
        ImageBinder.bind(avatar, MyCredentials.avatarPath, R.drawable.yaluway_avatar)
        avatar.setOnClickListener {
            (activity as? HomeActivity)?.openMeTab()
        }

        edtSearch = view.findViewById(R.id.edtSearch)
        tvEmpty = view.findViewById(R.id.tvEmptyState)
        val rv = view.findViewById<RecyclerView>(R.id.rvPosts)

        adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(requireContext(), PostDetailActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_POST_ID, post.id)
            startActivity(intent)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        view.findViewById<View>(R.id.cardHelp).setOnClickListener {
            openCategory(PostCategory.HELP)
        }
        view.findViewById<View>(R.id.cardServices).setOnClickListener {
            openCategory(PostCategory.SERVICES)
        }
        view.findViewById<View>(R.id.cardMarketplace).setOnClickListener {
            openCategory(PostCategory.MARKETPLACE)
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = refresh()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        view?.let { current ->
            bindAreaHeader(current)
            val avatar = current.findViewById<ImageView>(R.id.imgUserAvatar)
            avatar.clipToOutline = true
            ImageBinder.bind(avatar, MyCredentials.avatarPath, R.drawable.yaluway_avatar)
        }
        if (this::adapter.isInitialized) refresh()
    }

    private fun bindAreaHeader(view: View) {
        val area = MyCredentials.area.ifBlank { getString(R.string.area_hint) }
        view.findViewById<TextView>(R.id.tvAreaLabel).text = area
        view.findViewById<TextView>(R.id.tvNearbyLabel).text = getString(R.string.posts_near, area)
    }

    private fun refresh() {
        val area = MyCredentials.area.ifBlank { getString(R.string.area_hint) }
        val posts = PostRepository.search(edtSearch.text.toString(), null)
        adapter.submitList(posts)
        tvEmpty.text = getString(R.string.empty_posts_in_area, area)
        tvEmpty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openCategory(category: PostCategory) {
        val intent = Intent(requireContext(), CategoryActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_CATEGORY, category.name)
        startActivity(intent)
    }
}
