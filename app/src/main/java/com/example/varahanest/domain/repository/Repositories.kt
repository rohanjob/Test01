package com.example.varahanest.domain.repository

import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.model.Lead
import com.example.varahanest.domain.model.SupportTicket
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginWithEmail(email: String, password: String): Flow<Result<UserProfile>>
    fun signUpWithEmail(email: String, password: String, fullName: String): Flow<Result<UserProfile>>
    fun sendOtp(phone: String): Flow<Result<Unit>>
    fun verifyOtp(phone: String, otp: String): Flow<Result<UserProfile>>
    fun getCurrentUserFlow(): Flow<UserProfile?>
    suspend fun getCurrentUser(): UserProfile?
    fun logout(): Flow<Result<Unit>>
}

interface PropertyRepository {
    fun getProperties(forceRefresh: Boolean): Flow<List<Property>>
    fun getPropertiesByTransactionType(type: String, forceRefresh: Boolean): Flow<List<Property>>
    fun getPropertyDetails(id: String): Flow<Property?>
    fun searchProperties(query: String): Flow<List<Property>>
    fun getFavorites(): Flow<List<Property>>
    fun toggleFavorite(propertyId: String): Flow<Unit>
    fun isFavorite(propertyId: String): Flow<Boolean>
    fun submitLead(lead: Lead): Flow<Result<Unit>>
    
    // Posting & Drafting
    fun getDrafts(): Flow<List<Property>>
    suspend fun saveDraft(property: Property, localImagePaths: List<String>): Long
    suspend fun deleteDraft(draftId: Int)
    fun submitProperty(property: Property, localImagePaths: List<String>): Flow<Result<Property>>
}

interface SupportRepository {
    fun createSupportTicket(ticket: SupportTicket): Flow<Result<SupportTicket>>
    fun getSupportTickets(): Flow<List<SupportTicket>>
}
