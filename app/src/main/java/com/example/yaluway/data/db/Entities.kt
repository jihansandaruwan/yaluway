package com.example.yaluway.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val username: String,
    val area: String,
    val password: String,
    val avatarPath: String = ""
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val area: String,
    val contact: String,
    val price: String,
    val timeAgo: String,
    val owner: String,
    val ownerEmail: String,
    val mode: String,
    val imagePath: String = "",
    val createdAt: Long
)

@Entity(tableName = "saved_posts", primaryKeys = ["email", "postId"])
data class SavedPostEntity(
    val email: String,
    val postId: Int
)

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requesterEmail: String,
    val postId: Int,
    val status: String,
    val createdAt: Long
)
