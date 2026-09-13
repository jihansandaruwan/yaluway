package com.example.yaluway.model

data class PostRequest(
    val id: Int,
    val postId: Int,
    val postTitle: String,
    val requesterName: String,
    val requesterEmail: String,
    val status: String,
    val ownerContact: String,
    val postImage: String
)
