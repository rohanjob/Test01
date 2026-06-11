package com.example.varahanest.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LocalDaoTest {

    private lateinit var db: VarahaDatabase
    private lateinit var dao: LocalDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VarahaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetUserProfile() {
        val profile = UserProfileEntity(
            id = "user-123",
            fullName = "John Doe",
            phoneNumber = "+1234567890",
            role = "OWNER",
            createdAt = 1000L
        )
        dao.insertUserProfile(profile)

        val retrieved = dao.getActiveUserProfile()
        assertNotNull(retrieved)
        assertEquals("user-123", retrieved?.id)
        assertEquals("John Doe", retrieved?.fullName)
        assertEquals("+1234567890", retrieved?.phoneNumber)
        assertEquals("OWNER", retrieved?.role)

        dao.clearUserProfile()
        assertNull(dao.getActiveUserProfile())
    }

    @Test
    fun testPropertyCaching() = runBlocking {
        val properties = listOf(
            PropertyEntity(
                id = "prop-1",
                ownerId = "user-123",
                title = "Beautiful Villa",
                description = "Spacious villa with sea view",
                price = 5000000.0,
                transactionType = "BUY",
                propertyCategory = "RESIDENTIAL_HOUSE",
                bedrooms = 4,
                bathrooms = 4,
                balconies = 2,
                areaSqft = 3500.0,
                address = "123 Beach Road",
                city = "Goa",
                state = "Goa",
                latitude = 15.2993,
                longitude = 74.124,
                furnishingStatus = "FULLY_FURNISHED",
                parkingSpaces = 2,
                ownershipType = "FREEHOLD",
                postedBy = "OWNER",
                verified = true,
                imageUrls = "url1,url2",
                videoUrl = "video_url",
                createdAt = 2000L
            )
        )
        dao.insertProperties(properties)

        val retrievedFlow = dao.getAllPropertiesFlow().first()
        assertEquals(1, retrievedFlow.size)
        assertEquals("prop-1", retrievedFlow[0].id)
        assertEquals("Beautiful Villa", retrievedFlow[0].title)

        val byId = dao.getPropertyById("prop-1")
        assertNotNull(byId)
        assertEquals("Beautiful Villa", byId?.title)

        val searchFlow = dao.searchPropertiesFlow("Villa").first()
        assertEquals(1, searchFlow.size)

        dao.clearProperties()
        assertTrue(dao.getAllPropertiesFlow().first().isEmpty())
    }

    @Test
    fun testFavorites() = runBlocking {
        val favorite = FavoriteEntity(
            id = "fav-1",
            propertyId = "prop-1",
            userId = "user-123",
            addedAt = 3000L
        )
        dao.insertFavorite(favorite)

        val isFav = dao.isFavoriteFlow("prop-1", "user-123").first()
        assertTrue(isFav)

        val list = dao.getFavoritesFlow("user-123").first()
        assertEquals(1, list.size)

        dao.deleteFavorite("prop-1", "user-123")
        assertFalse(dao.isFavoriteFlow("prop-1", "user-123").first())
    }

    @Test
    fun testSearchHistory() = runBlocking {
        val search = SearchHistoryEntity("Apartment", 4000L)
        dao.insertSearchQuery(search)

        val history = dao.getSearchHistoryFlow().first()
        assertEquals(1, history.size)
        assertEquals("Apartment", history[0].query)

        dao.deleteSearchQuery("Apartment")
        assertTrue(dao.getSearchHistoryFlow().first().isEmpty())
    }

    @Test
    fun testRecentlyViewed() = runBlocking {
        val viewed = RecentlyViewedEntity("prop-1", 5000L)
        dao.insertRecentlyViewed(viewed)

        val recent = dao.getRecentlyViewedFlow().first()
        assertEquals(1, recent.size)
        assertEquals("prop-1", recent[0].propertyId)
    }

    @Test
    fun testPropertyDrafts() = runBlocking {
        val draft = PropertyDraftEntity(
            id = 0,
            title = "Draft Villa",
            description = "Some description",
            price = 2000000.0,
            transactionType = "RENT",
            propertyCategory = "RESIDENTIAL_HOUSE",
            bedrooms = 3,
            bathrooms = 3,
            balconies = 1,
            areaSqft = 2000.0,
            address = "Draft Road",
            city = "Pune",
            state = "Maharashtra",
            latitude = null,
            longitude = null,
            furnishingStatus = null,
            parkingSpaces = 1,
            ownershipType = null,
            localImagePaths = "local_img1",
            localVideoPath = "local_video_path",
            lastUpdated = 6000L
        )
        val id = dao.saveDraft(draft)
        assertTrue(id > 0)

        val allDrafts = dao.getAllDraftsFlow().first()
        assertEquals(1, allDrafts.size)
        assertEquals("Draft Villa", allDrafts[0].title)
        assertEquals("local_video_path", allDrafts[0].localVideoPath)

        val fetched = dao.getDraftById(id.toInt())
        assertNotNull(fetched)
        assertEquals("local_video_path", fetched?.localVideoPath)

        dao.deleteDraft(id.toInt())
        assertNull(dao.getDraftById(id.toInt()))
    }
}
