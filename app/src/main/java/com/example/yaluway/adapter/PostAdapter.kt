package com.example.yaluway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.R
import com.example.yaluway.model.Post
import com.example.yaluway.model.PostCategory
import com.example.yaluway.util.ImageBinder

class PostAdapter(
    private var items: List<Post>,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumb: ImageView = itemView.findViewById(R.id.imgPostThumb)
        val tvBadge: TextView = itemView.findViewById(R.id.tvPostBadge)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        val tvMeta: TextView = itemView.findViewById(R.id.tvPostMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = items[position]
        val badgeText = post.mode.ifBlank { post.category.badge }

        ImageBinder.bind(holder.imgThumb, post.imagePath, fallbackFor(post.category))
        holder.tvBadge.text = badgeText
        holder.tvBadge.setBackgroundResource(badgeBackgroundFor(post, badgeText))
        holder.tvBadge.setTextColor(
            holder.itemView.context.getColor(
                if (badgeText.contains("BORROW", true) || badgeText.contains("LEND", true) || badgeText.contains("FREE", true) || badgeText.contains("DONATE", true)) {
                    R.color.yalu_navy
                } else {
                    R.color.white
                }
            )
        )
        holder.tvTitle.text = post.title
        holder.tvMeta.text = "${post.area} · ${post.category.label} · ${post.timeAgo}"
        holder.itemView.setOnClickListener { onPostClick(post) }
    }

    fun submitList(newItems: List<Post>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun fallbackFor(category: PostCategory): Int = when (category) {
        PostCategory.HELP -> R.drawable.yaluway_onboard_help
        PostCategory.SERVICES -> R.drawable.yaluway_onboard_services
        PostCategory.MARKETPLACE -> R.drawable.yaluway_hero_kettle
    }

    private fun badgeBackgroundFor(post: Post, badgeText: String): Int {
        if (badgeText.contains("BORROW", true) || badgeText.contains("LEND", true) ||
            badgeText.contains("FREE", true) || badgeText.contains("DONATE", true)
        ) {
            return R.drawable.bg_badge_market
        }
        return when (post.category) {
            PostCategory.HELP -> R.drawable.bg_badge_help
            PostCategory.SERVICES -> R.drawable.bg_badge_services
            PostCategory.MARKETPLACE -> R.drawable.bg_badge_market
        }
    }
}
