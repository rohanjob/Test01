package com.example.varahanest.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.usecase.GetCurrentUserUseCase
import com.example.varahanest.domain.usecase.LoginUseCase
import com.example.varahanest.domain.usecase.LogoutUseCase
import com.example.varahanest.domain.usecase.RegisterUseCase
import com.example.varahanest.domain.usecase.SendOtpUseCase
import com.example.varahanest.domain.usecase.VerifyOtpUseCase
import com.example.varahanest.data.local.VarahaDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val db: VarahaDatabase,
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
            val user = getCurrentUserUseCase()
            if (user != null) {
                val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
                val isPremium = prefs.getBoolean("premium_status_${user.id}", false)
                _currentUser.value = user.copy(isPremium = isPremium)
            } else {
                _currentUser.value = null
            }
        }
    }

    fun login(email: String, psw: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            loginUseCase(email, psw).collect { result ->
                result.onSuccess {
                    val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
                    val isPremium = prefs.getBoolean("premium_status_${it.id}", false)
                    val profile = it.copy(isPremium = isPremium)
                    _currentUser.value = profile
                    _authState.value = AuthState.Success(profile)
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
                    val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
                    val isPremium = prefs.getBoolean("premium_status_${it.id}", false)
                    val profile = it.copy(isPremium = isPremium)
                    _currentUser.value = profile
                    _authState.value = AuthState.Success(profile)
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
                    val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
                    val isPremium = prefs.getBoolean("premium_status_${it.id}", false)
                    val profile = it.copy(isPremium = isPremium)
                    _currentUser.value = profile
                    _authState.value = AuthState.Success(profile)
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

    fun upgradeToPremium() {
        val user = _currentUser.value ?: return
        val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("premium_status_${user.id}", true).apply()
        val updated = user.copy(isPremium = true)
        _currentUser.value = updated
        _authState.value = AuthState.Success(updated)
    }

    fun downgradeFromPremium() {
        val user = _currentUser.value ?: return
        val prefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("premium_status_${user.id}", false).apply()
        val updated = user.copy(isPremium = false)
        _currentUser.value = updated
        _authState.value = AuthState.Success(updated)
    }

    fun clearError() {
        _authState.value = AuthState.Idle
    }

    fun updateUserProfile(fullName: String, phone: String) {
        val user = _currentUser.value ?: return
        val prefs = context.getSharedPreferences("varaha_nest_mock_session", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("full_name", fullName)
            putString("phone_number", phone)
            apply()
        }
        val updated = user.copy(fullName = fullName, phoneNumber = phone)
        _currentUser.value = updated
        _authState.value = AuthState.Success(updated)
    }

    fun deleteUserDataAndLogout() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                db.clearAllTables()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val mockPrefs = context.getSharedPreferences("varaha_nest_mock_session", Context.MODE_PRIVATE)
            mockPrefs.edit().clear().apply()
            val mainPrefs = context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE)
            mainPrefs.edit().clear().apply()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }
}
