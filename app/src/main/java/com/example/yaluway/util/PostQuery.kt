package com.example.yaluway.util

import com.example.yaluway.model.Post
import com.example.yaluway.model.PostCategory

/** Search, category, help-chip, and nearby filters for the home list. */
object PostQuery {

    fun filter(
        posts: List<Post>,
        query: String,
        category: PostCategory? = null,
        helpChip: String? = null,
        userArea: String = ""
    ): List<Post> {
        val base = if (category == null) posts else posts.filter { it.category == category }
        val afterChip = when (helpChip?.lowercase()) {
            "urgent" -> base.filter { it.title.contains("urgent", true) || it.title.contains("blood", true) || it.description.contains("urgent", true) }
            "volunteer" -> base.filter { it.title.contains("volunteer", true) || it.description.contains("volunteer", true) }
            else -> base
        }
        val nearby = if (userArea.isBlank()) {
            afterChip
        } else {
            afterChip.filter { SriLankaAreas.isNearby(it.area, userArea) }
        }
        if (query.isBlank()) return nearby
        val needle = query.trim().lowercase()
        return nearby.filter { post ->
            post.title.lowercase().contains(needle) ||
                post.description.lowercase().contains(needle) ||
                post.area.lowercase().contains(needle)
        }
    }
}
