package com.example.varahanest.data.repository

import com.example.varahanest.data.remote.SupabaseService
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteService: SupabaseService
) : AuthRepository {

    override fun loginWithEmail(email: String, password: String): Flow<Result<UserProfile>> = flow {
        emit(remoteService.loginWithEmail(email, password))
    }

    override fun signUpWithEmail(email: String, password: String, fullName: String): Flow<Result<UserProfile>> = flow {
        emit(remoteService.signUpWithEmail(email, password, fullName))
    }

    override fun sendOtp(phone: String): Flow<Result<Unit>> = flow {
        emit(remoteService.sendOtp(phone))
    }

    override fun verifyOtp(phone: String, otp: String): Flow<Result<UserProfile>> = flow {
        emit(remoteService.verifyOtp(phone, otp))
    }

    override fun getCurrentUserFlow(): Flow<UserProfile?> = flow {
        emit(remoteService.getCurrentUser())
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return remoteService.getCurrentUser()
    }

    override fun logout(): Flow<Result<Unit>> = flow {
        emit(remoteService.logout())
    }
}
