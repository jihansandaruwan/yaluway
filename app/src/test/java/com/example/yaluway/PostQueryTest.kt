package com.example.yaluway

import com.example.yaluway.model.Post
import com.example.yaluway.model.PostCategory
import com.example.yaluway.util.PostQuery
import org.junit.Assert.assertEquals
import org.junit.Test

class PostQueryTest {

    private val posts = listOf(
        Post(1, "Need blood donor", "Urgent request", PostCategory.HELP, "Nugegoda", "071"),
        Post(2, "Volunteer: weekend cleanup", "Park cleanup", PostCategory.HELP, "Dehiwala", "077"),
        Post(3, "Electric kettle", "Borrow for 2 days", PostCategory.MARKETPLACE, "Hostel A", "070"),
        Post(4, "Plumber needed", "Tap leak", PostCategory.SERVICES, "Kotte", "076"),
        Post(5, "Need a boat ride", "Lagoon jetty", PostCategory.HELP, "Negombo", "077"),
        Post(6, "Kandy temple guide", "Weekend tour", PostCategory.SERVICES, "Kandy", "075")
    )

    @Test
    fun searchFindsArea() {
        val result = PostQuery.filter(posts, "nugegoda")
        assertEquals(1, result.size)
        assertEquals("Need blood donor", result[0].title)
    }

    @Test
    fun categoryFilterWorks() {
        val result = PostQuery.filter(posts, "", PostCategory.SERVICES)
        assertEquals(2, result.size)
    }

    @Test
    fun urgentChipKeepsBloodPost() {
        val result = PostQuery.filter(posts, "", PostCategory.HELP, "urgent")
        assertEquals(1, result.size)
        assertEquals("Need blood donor", result[0].title)
    }

    @Test
    fun volunteerChipKeepsCleanupPost() {
        val result = PostQuery.filter(posts, "", PostCategory.HELP, "volunteer")
        assertEquals(1, result.size)
        assertEquals("Volunteer: weekend cleanup", result[0].title)
    }

    @Test
    fun negomboUserSeesNearbyCoastPosts() {
        val result = PostQuery.filter(posts, "", userArea = "Negombo")
        assertEquals(1, result.size)
        assertEquals("Need a boat ride", result[0].title)
    }

    @Test
    fun nugegodaUserSeesColomboCluster() {
        val result = PostQuery.filter(posts, "", userArea = "Nugegoda")
        assertEquals(4, result.size)
    }

    @Test
    fun kandyUserDoesNotSeeNegomboPosts() {
        val result = PostQuery.filter(posts, "", userArea = "Kandy")
        assertEquals(1, result.size)
        assertEquals("Kandy temple guide", result[0].title)
    }
}
