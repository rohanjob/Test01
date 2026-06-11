package com.example.varahanest.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.usecase.GetCurrentUserUseCase
import com.example.varahanest.domain.usecase.LoginUseCase
import com.example.varahanest.domain.usecase.LogoutUseCase
import com.example.varahanest.domain.usecase.RegisterUseCase
import com.example.varahanest.domain.usecase.SendOtpUseCase
import com.example.varahanest.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val profile: UserProfile) : AuthState
    data class Error(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            _currentUser.value = getCurrentUserUseCase()
        }
    }

    fun login(email: String, psw: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            loginUseCase(email, psw).collect { result ->
                result.onSuccess {
                    _currentUser.value = it
                    _authState.value = AuthState.Success(it)
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Authentication failed")
                }
            }
        }
    }

    fun signUp(email: String, psw: String, fullName: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            registerUseCase(email, psw, fullName).collect { result ->
                result.onSuccess {
                    _currentUser.value = it
                    _authState.value = AuthState.Success(it)
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Registration failed")
                }
            }
        }
    }

    fun sendOtp(phone: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            sendOtpUseCase(phone).collect { result ->
                result.onSuccess {
                    _authState.value = AuthState.Idle // Ready for verification
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Failed to send OTP")
                }
            }
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            verifyOtpUseCase(phone, otp).collect { result ->
                result.onSuccess {
                    _currentUser.value = it
                    _authState.value = AuthState.Success(it)
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Invalid OTP code")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { result ->
                result.onSuccess {
                    _currentUser.value = null
                    _authState.value = AuthState.Idle
                }
            }
        }
    }
    
    fun clearError() {
        _authState.value = AuthState.Idle
    }
}
