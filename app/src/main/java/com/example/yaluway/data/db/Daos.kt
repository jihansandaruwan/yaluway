package com.example.yaluway.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun findByEmail(email: String): UserEntity?

    @Update
    fun update(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    fun count(): Int

    @Query("SELECT * FROM users")
    fun getAll(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(user: UserEntity)
}

@Dao
interface PostDao {
    @Insert
    fun insert(post: PostEntity): Long

    @Update
    fun update(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAll(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun findById(id: Int): PostEntity?

    @Query("SELECT * FROM posts WHERE ownerEmail = :email ORDER BY createdAt DESC")
    fun mine(email: String): List<PostEntity>

    @Query("SELECT * FROM posts WHERE category = :category ORDER BY createdAt DESC")
    fun byCategory(category: String): List<PostEntity>

    @Query("SELECT COUNT(*) FROM posts")
    fun count(): Int
}

@Dao
interface SavedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(saved: SavedPostEntity)

    @Query("DELETE FROM saved_posts WHERE email = :email AND postId = :postId")
    fun delete(email: String, postId: Int)

    @Query("SELECT COUNT(*) FROM saved_posts WHERE email = :email AND postId = :postId")
    fun isSaved(email: String, postId: Int): Int

    @Query("SELECT postId FROM saved_posts WHERE email = :email")
    fun idsForUser(email: String): List<Int>

    @Query("SELECT COUNT(*) FROM saved_posts WHERE email = :email")
    fun countForUser(email: String): Int
}

@Dao
interface RequestDao {
    @Insert
    fun insert(request: RequestEntity): Long

    @Query("SELECT COUNT(*) FROM requests WHERE requesterEmail = :email AND postId = :postId")
    fun hasRequest(email: String, postId: Int): Int

    @Query("SELECT * FROM requests WHERE requesterEmail = :email AND postId = :postId LIMIT 1")
    fun find(email: String, postId: Int): RequestEntity?

    @Query("SELECT * FROM requests WHERE requesterEmail = :email ORDER BY createdAt DESC")
    fun sentBy(email: String): List<RequestEntity>

    @Query(
        "SELECT r.* FROM requests r INNER JOIN posts p ON p.id = r.postId " +
            "WHERE p.ownerEmail = :ownerEmail ORDER BY r.createdAt DESC"
    )
    fun incomingForOwner(ownerEmail: String): List<RequestEntity>

    @Query(
        "SELECT COUNT(*) FROM requests r INNER JOIN posts p ON p.id = r.postId " +
            "WHERE p.ownerEmail = :ownerEmail AND r.status = 'PENDING'"
    )
    fun pendingCountForOwner(ownerEmail: String): Int

    @Query("SELECT COUNT(*) FROM requests r INNER JOIN posts p ON p.id = r.postId WHERE p.id = :postId AND p.ownerEmail = :ownerEmail AND r.status = 'PENDING'")
    fun pendingCountForPost(postId: Int, ownerEmail: String): Int

    @Update
    fun update(request: RequestEntity)
}
