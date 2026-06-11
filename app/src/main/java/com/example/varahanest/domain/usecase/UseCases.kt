package com.example.varahanest.domain.usecase

import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.model.Lead
import com.example.varahanest.domain.model.SupportTicket
import com.example.varahanest.domain.repository.AuthRepository
import com.example.varahanest.domain.repository.PropertyRepository
import com.example.varahanest.domain.repository.SupportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPropertiesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(type: String? = null, forceRefresh: Boolean = false): Flow<List<Property>> {
        return if (type != null) {
            repository.getPropertiesByTransactionType(type, forceRefresh)
        } else {
            repository.getProperties(forceRefresh)
        }
    }
}

class GetPropertyDetailsUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(id: String): Flow<Property?> = repository.getPropertyDetails(id)
}

class SearchPropertiesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(query: String): Flow<List<Property>> = repository.searchProperties(query)
}

class SavePropertyDraftUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(property: Property, localImagePaths: List<String>): Long {
        return repository.saveDraft(property, localImagePaths)
    }
}

class SubmitPropertyUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(property: Property, localImagePaths: List<String>): Flow<Result<Property>> {
        return repository.submitProperty(property, localImagePaths)
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(propertyId: String): Flow<Unit> = repository.toggleFavorite(propertyId)
}

class IsFavoriteUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(propertyId: String): Flow<Boolean> = repository.isFavorite(propertyId)
}

class ContactOwnerUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(lead: Lead): Flow<Result<Unit>> = repository.submitLead(lead)
}

class CreateSupportTicketUseCase @Inject constructor(
    private val repository: SupportRepository
) {
    operator fun invoke(ticket: SupportTicket): Flow<Result<SupportTicket>> = repository.createSupportTicket(ticket)
}

class GetSupportTicketsUseCase @Inject constructor(
    private val repository: SupportRepository
) {
    operator fun invoke(): Flow<List<SupportTicket>> = repository.getSupportTickets()
}

// Auth use cases grouped
class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, psw: String) = authRepository.loginWithEmail(email, psw)
}

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, psw: String, name: String) = authRepository.signUpWithEmail(email, psw, name)
}

class SendOtpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(phone: String) = authRepository.sendOtp(phone)
}

class VerifyOtpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(phone: String, otp: String) = authRepository.verifyOtp(phone, otp)
}

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    fun asFlow(): Flow<UserProfile?> = authRepository.getCurrentUserFlow()
    suspend operator fun invoke(): UserProfile? = authRepository.getCurrentUser()
}

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.logout()
}
