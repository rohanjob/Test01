package com.example.varahanest.data.repository

import com.example.varahanest.data.local.LocalDao
import com.example.varahanest.data.local.UserProfileEntity
import com.example.varahanest.data.remote.SupabaseService
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val localDao: LocalDao,
    private val remoteService: SupabaseService
) : AuthRepository {

    override fun loginWithEmail(email: String, password: String): Flow<Result<UserProfile>> = flow {
        val result = remoteService.loginWithEmail(email, password)
        result.onSuccess { profile ->
            localDao.clearUserProfile()
            localDao.insertUserProfile(profile.toEntity())
        }
        emit(result)
    }

    override fun signUpWithEmail(email: String, password: String, fullName: String): Flow<Result<UserProfile>> = flow {
        val result = remoteService.signUpWithEmail(email, password, fullName)
        result.onSuccess { profile ->
            localDao.clearUserProfile()
            localDao.insertUserProfile(profile.toEntity())
        }
        emit(result)
    }

    override fun sendOtp(phone: String): Flow<Result<Unit>> = flow {
        emit(remoteService.sendOtp(phone))
    }

    override fun verifyOtp(phone: String, otp: String): Flow<Result<UserProfile>> = flow {
        val result = remoteService.verifyOtp(phone, otp)
        result.onSuccess { profile ->
            localDao.clearUserProfile()
            localDao.insertUserProfile(profile.toEntity())
        }
        emit(result)
    }

    override fun getCurrentUserFlow(): Flow<UserProfile?> = flow {
        val cached = localDao.getActiveUserProfile()?.toDomain()
        emit(cached)
        try {
            val remote = remoteService.getCurrentUser()
            if (remote != null) {
                localDao.clearUserProfile()
                localDao.insertUserProfile(remote.toEntity())
                emit(remote)
            }
        } catch (e: Exception) {
            // Ignore and fallback to cached
        }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return try {
            val remote = remoteService.getCurrentUser()
            if (remote != null) {
                localDao.clearUserProfile()
                localDao.insertUserProfile(remote.toEntity())
                remote
            } else {
                localDao.getActiveUserProfile()?.toDomain()
            }
        } catch (e: Exception) {
            localDao.getActiveUserProfile()?.toDomain()
        }
    }

    override fun logout(): Flow<Result<Unit>> = flow {
        val result = remoteService.logout()
        result.onSuccess {
            localDao.clearUserProfile()
        }
        emit(result)
    }
}

// Mapper extension functions
fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    fullName = fullName,
    phoneNumber = phoneNumber,
    role = role,
    createdAt = createdAt
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    fullName = fullName,
    phoneNumber = phoneNumber,
    role = role,
    createdAt = createdAt
)
