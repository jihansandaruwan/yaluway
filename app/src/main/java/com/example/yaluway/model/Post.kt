package com.example.yaluway.model

enum class PostCategory(val label: String, val badge: String) {
    HELP("Help", "HELP"),
    SERVICES("Services", "SERVICE"),
    MARKETPLACE("Marketplace", "BORROW")
}

data class Post(
    val id: Int,
    val title: String,
    val description: String,
    val category: PostCategory,
    val area: String,
    val contact: String,
    val price: String = "",
    val timeAgo: String = "just now",
    val owner: String = "You",
    val ownerEmail: String = "",
    val mode: String = "",
    val imagePath: String = "",
    val mine: Boolean = false
)
