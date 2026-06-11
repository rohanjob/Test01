package com.example.varahanest.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val transactionType: String = "BUY", // BUY, RENT, COMMERCIAL
    val propertyCategory: String = "", // RESIDENTIAL_APARTMENT, RESIDENTIAL_HOUSE, RENT_PG_ROOM, COMMERCIAL_OFFICE, COMMERCIAL_SHOP
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val balconies: Int = 0,
    val areaSqft: Double = 0.0,
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val furnishingStatus: String? = null, // FULLY_FURNISHED, SEMI_FURNISHED, UNFURNISHED
    val parkingSpaces: Int = 0,
    val ownershipType: String? = null, // FREEHOLD, LEASEHOLD, CO_OPERATIVE
    val postedBy: String = "OWNER", // OWNER, AGENT, BUILDER
    val verified: Boolean = false,
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val role: String = "USER", // USER, OWNER, AGENT, BUILDER
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class Lead(
    val id: String = "",
    val propertyId: String = "",
    val buyerId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val description: String = "",
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class NotificationItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
