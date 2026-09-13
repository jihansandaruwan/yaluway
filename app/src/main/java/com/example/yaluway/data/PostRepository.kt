package com.example.yaluway.data

import android.content.Context
import com.example.yaluway.data.db.PostEntity
import com.example.yaluway.data.db.RequestEntity
import com.example.yaluway.data.db.SavedPostEntity
import com.example.yaluway.data.db.YaluwayDatabase
import com.example.yaluway.model.MyCredentials
import com.example.yaluway.model.Post
import com.example.yaluway.model.PostCategory
import com.example.yaluway.model.PostRequest
import com.example.yaluway.util.PostQuery

object PostRepository {
    private lateinit var db: YaluwayDatabase

    fun init(context: Context) {
        db = YaluwayDatabase.get(context)
        if (db.postDao().count() == 0) {
            seed()
        }
        ensureAreaSamples()
    }

    fun getAll(): List<Post> = db.postDao().getAll().map { it.toPost() }

    fun getByCategory(category: PostCategory?): List<Post> {
        if (category == null) return getAll()
        return db.postDao().byCategory(category.name).map { it.toPost() }
    }

    fun mine(): List<Post> {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return emptyList()
        return db.postDao().mine(email).map { it.toPost() }
    }

    fun search(query: String, category: PostCategory?, helpChip: String? = null): List<Post> {
        return PostQuery.filter(getAll(), query, category, helpChip, MyCredentials.area)
    }

    fun findById(id: Int): Post? = db.postDao().findById(id)?.toPost()

    fun add(
        title: String,
        description: String,
        category: PostCategory,
        area: String,
        contact: String,
        price: String,
        mode: String = "",
        imagePath: String = ""
    ): Post {
        val entity = PostEntity(
            title = title,
            description = description,
            category = category.name,
            area = area,
            contact = contact,
            price = price,
            timeAgo = "just now",
            owner = MyCredentials.username.ifBlank { "You" },
            ownerEmail = SessionManager.currentEmail(),
            mode = mode,
            imagePath = imagePath.ifBlank { defaultImage(category) },
            createdAt = System.currentTimeMillis()
        )
        val id = db.postDao().insert(entity).toInt()
        return entity.copy(id = id).toPost()
    }

    fun update(
        id: Int,
        title: String,
        description: String,
        category: PostCategory,
        area: String,
        contact: String,
        price: String,
        mode: String,
        imagePath: String
    ): Boolean {
        val existing = db.postDao().findById(id) ?: return false
        db.postDao().update(
            existing.copy(
                title = title,
                description = description,
                category = category.name,
                area = area,
                contact = contact,
                price = price,
                mode = mode,
                imagePath = imagePath.ifBlank { existing.imagePath }
            )
        )
        return true
    }

    fun delete(id: Int): Boolean {
        db.postDao().deleteById(id)
        return true
    }

    fun isSaved(postId: Int): Boolean {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return false
        return db.savedPostDao().isSaved(email, postId) > 0
    }

    fun toggleSave(postId: Int): Boolean {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return false
        return if (isSaved(postId)) {
            db.savedPostDao().delete(email, postId)
            false
        } else {
            db.savedPostDao().insert(SavedPostEntity(email, postId))
            true
        }
    }

    fun savedPosts(): List<Post> {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return emptyList()
        return db.savedPostDao().idsForUser(email).mapNotNull { findById(it) }
    }

    fun savedCount(): Int {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return 0
        return db.savedPostDao().countForUser(email)
    }

    fun requestStatus(postId: Int): String? {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return null
        return db.requestDao().find(email, postId)?.status
    }

    fun hasRequested(postId: Int): Boolean = requestStatus(postId) != null

    fun sendRequest(postId: Int): Boolean {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return false
        val post = db.postDao().findById(postId) ?: return false
        if (post.ownerEmail == email) return false
        if (db.requestDao().hasRequest(email, postId) > 0) return false
        db.requestDao().insert(
            RequestEntity(
                requesterEmail = email,
                postId = postId,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
        )
        return true
    }

    fun incomingRequests(): List<PostRequest> {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return emptyList()
        return db.requestDao().incomingForOwner(email).mapNotNull { it.toModel() }
    }

    fun sentRequests(): List<PostRequest> {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return emptyList()
        return db.requestDao().sentBy(email).mapNotNull { it.toModel() }
    }

    fun pendingRequestCount(): Int {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return 0
        return db.requestDao().pendingCountForOwner(email)
    }

    fun pendingForPost(postId: Int): Int {
        val email = SessionManager.currentEmail()
        if (email.isBlank()) return 0
        return db.requestDao().pendingCountForPost(postId, email)
    }

    fun setRequestStatus(requestId: Int, status: String) {
        val email = SessionManager.currentEmail()
        val entity = db.requestDao().incomingForOwner(email)
            .firstOrNull { it.id == requestId } ?: return
        db.requestDao().update(entity.copy(status = status))
    }

    private fun RequestEntity.toModel(): PostRequest? {
        val post = db.postDao().findById(postId) ?: return null
        val user = db.userDao().findByEmail(requesterEmail)
        return PostRequest(
            id = id,
            postId = postId,
            postTitle = post.title,
            requesterName = user?.username ?: requesterEmail,
            requesterEmail = requesterEmail,
            status = status,
            ownerContact = post.contact,
            postImage = post.imagePath
        )
    }

    private fun defaultImage(category: PostCategory): String = when (category) {
        PostCategory.HELP -> "drawable:yaluway_onboard_help"
        PostCategory.SERVICES -> "drawable:yaluway_onboard_services"
        PostCategory.MARKETPLACE -> "drawable:yaluway_onboard_market"
    }

    private fun seed() {
        val samples = listOf(
            PostEntity(title = "Need jumper cables", description = "Car battery died near the junction. Need jumper cables for 10 minutes.", category = "HELP", area = "Nugegoda", contact = "071 234 5678", price = "", timeAgo = "2 min ago", owner = "Kasun", ownerEmail = "kasun@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_help", createdAt = System.currentTimeMillis() - 120_000),
            PostEntity(title = "Free textbooks", description = "Grade 10 textbooks in good condition. Free to collect.", category = "MARKETPLACE", area = "Nugegoda", contact = "077 555 1200", price = "", timeAgo = "5 min ago", owner = "Nimali", ownerEmail = "nimali@yaluway.lk", mode = "DONATE", imagePath = "drawable:yaluway_onboard_market", createdAt = System.currentTimeMillis() - 300_000),
            PostEntity(title = "Plumber needed", description = "Kitchen tap leak. Same-day help appreciated.", category = "SERVICES", area = "Nugegoda", contact = "070 121 9080", price = "", timeAgo = "12 min ago", owner = "Amal", ownerEmail = "amal@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_services", createdAt = System.currentTimeMillis() - 720_000),
            PostEntity(title = "Need blood donor (B+)", description = "Urgent request for my father at Nugegoda hospital.", category = "HELP", area = "Nugegoda", contact = "071 234 5678", price = "", timeAgo = "3 min ago", owner = "Sandun", ownerEmail = "sandun@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_help", createdAt = System.currentTimeMillis() - 180_000),
            PostEntity(title = "Volunteer: weekend cleanup", description = "Looking for volunteers this Saturday morning at the park.", category = "HELP", area = "Nugegoda", contact = "071 888 2211", price = "", timeAgo = "1 hr ago", owner = "Ishara", ownerEmail = "ishara@yaluway.lk", mode = "", imagePath = "drawable:yaluway_banner_home", createdAt = System.currentTimeMillis() - 3_600_000),
            PostEntity(title = "Electric kettle", description = "Can borrow for 2 days. Pickup from hostel kitchen. Return by Sunday evening.", category = "MARKETPLACE", area = "Nugegoda", contact = "077 442 7719", price = "", timeAgo = "20 min ago", owner = "Nimali", ownerEmail = "nimali@yaluway.lk", mode = "LEND", imagePath = "drawable:yaluway_hero_kettle", createdAt = System.currentTimeMillis() - 1_200_000),
            PostEntity(title = "Maths tutor for Grade 10", description = "Home visits on weekends. 5 years of O/L class experience.", category = "SERVICES", area = "Kotte", contact = "076 880 4411", price = "Rs. 1500 / hour", timeAgo = "1 hr ago", owner = "Iresha", ownerEmail = "iresha@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_services", createdAt = System.currentTimeMillis() - 3_600_000)
        )
        samples.forEach { db.postDao().insert(it) }
        ensureAreaSamples()
    }

    private fun ensureAreaSamples() {
        val titles = db.postDao().getAll().map { it.title }.toSet()
        val extras = listOf(
            PostEntity(title = "Need a boat ride", description = "Need a short ride from Negombo lagoon jetty this evening.", category = "HELP", area = "Negombo", contact = "077 111 2200", price = "", timeAgo = "8 min ago", owner = "Sajith", ownerEmail = "sajith@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_help", createdAt = System.currentTimeMillis() - 480_000),
            PostEntity(title = "Lend fishing net", description = "Can lend a fishing net for the weekend. Pickup in Negombo.", category = "MARKETPLACE", area = "Negombo", contact = "076 333 4411", price = "", timeAgo = "25 min ago", owner = "Ruwan", ownerEmail = "ruwan@yaluway.lk", mode = "LEND", imagePath = "drawable:yaluway_onboard_market", createdAt = System.currentTimeMillis() - 1_500_000),
            PostEntity(title = "Airport drop needed", description = "Need a lift to the airport from Ja-Ela tomorrow morning.", category = "HELP", area = "Ja-Ela", contact = "071 999 3322", price = "", timeAgo = "15 min ago", owner = "Dilani", ownerEmail = "dilani@yaluway.lk", mode = "", imagePath = "drawable:yaluway_banner_home", createdAt = System.currentTimeMillis() - 900_000),
            PostEntity(title = "Kandy temple guide", description = "Local guide available this weekend around the temple.", category = "SERVICES", area = "Kandy", contact = "075 222 1100", price = "", timeAgo = "40 min ago", owner = "Nuwan", ownerEmail = "nuwan@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_services", createdAt = System.currentTimeMillis() - 2_400_000),
            PostEntity(title = "Borrow tent for Knuckles", description = "Need a 2-person tent for 2 nights. Can collect in Kandy.", category = "MARKETPLACE", area = "Kandy", contact = "077 444 5566", price = "", timeAgo = "1 hr ago", owner = "Tharindu", ownerEmail = "tharindu@yaluway.lk", mode = "BORROW", imagePath = "drawable:yaluway_hero_kettle", createdAt = System.currentTimeMillis() - 3_600_000),
            PostEntity(title = "Galle fort walking tour", description = "Evening walking tour around Galle Fort.", category = "SERVICES", area = "Galle", contact = "072 888 0099", price = "", timeAgo = "50 min ago", owner = "Fathima", ownerEmail = "fathima@yaluway.lk", mode = "", imagePath = "drawable:yaluway_onboard_services", createdAt = System.currentTimeMillis() - 3_000_000)
        )
        extras.filter { it.title !in titles }.forEach { db.postDao().insert(it) }
    }

    private fun PostEntity.toPost(): Post {
        val category = runCatching { PostCategory.valueOf(category) }.getOrDefault(PostCategory.HELP)
        return Post(
            id = id,
            title = title,
            description = description,
            category = category,
            area = area,
            contact = contact,
            price = price,
            timeAgo = timeAgo,
            owner = owner,
            ownerEmail = ownerEmail,
            mode = mode,
            imagePath = imagePath,
            mine = ownerEmail.isNotBlank() && ownerEmail == SessionManager.currentEmail()
        )
    }
}
