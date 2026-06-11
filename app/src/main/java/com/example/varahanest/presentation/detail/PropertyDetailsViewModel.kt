package com.example.varahanest.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.model.Lead
import com.example.varahanest.domain.usecase.GetPropertyDetailsUseCase
import com.example.varahanest.domain.usecase.IsFavoriteUseCase
import com.example.varahanest.domain.usecase.ToggleFavoriteUseCase
import com.example.varahanest.domain.usecase.ContactOwnerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LeadState {
    object Idle : LeadState
    object Loading : LeadState
    object Success : LeadState
    data class Error(val message: String) : LeadState
}

@HiltViewModel
class PropertyDetailsViewModel @Inject constructor(
    private val getPropertyDetailsUseCase: GetPropertyDetailsUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val contactOwnerUseCase: ContactOwnerUseCase
) : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _leadState = MutableStateFlow<LeadState>(LeadState.Idle)
    val leadState: StateFlow<LeadState> = _leadState.asStateFlow()

    fun loadPropertyDetails(id: String) {
        viewModelScope.launch {
            getPropertyDetailsUseCase(id).collect { prop ->
                _property.value = prop
                if (prop != null) {
                    observeFavoriteState(prop.id)
                }
            }
        }
    }

    private fun observeFavoriteState(propertyId: String) {
        viewModelScope.launch {
            isFavoriteUseCase(propertyId).collect {
                _isFavorite.value = it
            }
        }
    }

    fun toggleFavorite() {
        val prop = _property.value ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(prop.id).collect {
                // UI automatically updates through observed favorite state Flow
            }
        }
    }

    fun submitLead(message: String) {
        val prop = _property.value ?: return
        _leadState.value = LeadState.Loading
        viewModelScope.launch {
            val lead = Lead(
                propertyId = prop.id,
                buyerId = "mock-uuid-1",
                message = message
            )
            contactOwnerUseCase(lead).collect { result ->
                result.onSuccess {
                    _leadState.value = LeadState.Success
                }.onFailure {
                    _leadState.value = LeadState.Error(it.message ?: "Failed to submit inquiry")
                }
            }
        }
    }

    fun resetLeadState() {
        _leadState.value = LeadState.Idle
    }
}
