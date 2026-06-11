package com.example.varahanest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val title: String,
    val description: String,
    val price: Double,
    val transactionType: String, // "BUY", "RENT", "COMMERCIAL"
    val propertyCategory: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val balconies: Int,
    val areaSqft: Double,
    val address: String,
    val city: String,
    val state: String,
    val latitude: Double?,
    val longitude: Double?,
    val furnishingStatus: String?,
    val parkingSpaces: Int,
    val ownershipType: String?,
    val postedBy: String,
    val verified: Boolean,
    val imageUrls: String, // Comma-separated image URLs
    val videoUrl: String?,
    val createdAt: Long
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val userId: String,
    val addedAt: Long
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val propertyId: String,
    val viewedAt: Long
)

@Entity(tableName = "property_drafts")
data class PropertyDraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val transactionType: String,
    val propertyCategory: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val balconies: Int,
    val areaSqft: Double,
    val address: String,
    val city: String,
    val state: String,
    val latitude: Double?,
    val longitude: Double?,
    val furnishingStatus: String?,
    val parkingSpaces: Int,
    val ownershipType: String?,
    val localImagePaths: String, // Comma separated local/selected images
    val lastUpdated: Long
)
