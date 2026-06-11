package com.example.varahanest.presentation.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.usecase.SavePropertyDraftUseCase
import com.example.varahanest.domain.usecase.SubmitPropertyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SubmissionState {
    object Idle : SubmissionState
    object Loading : SubmissionState
    data class Success(val property: Property) : SubmissionState
    data class Error(val message: String) : SubmissionState
}

@HiltViewModel
class PostPropertyViewModel @Inject constructor(
    private val savePropertyDraftUseCase: SavePropertyDraftUseCase,
    private val submitPropertyUseCase: SubmitPropertyUseCase
) : ViewModel() {

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // Property state being built
    private val _propertyDraft = MutableStateFlow(Property(ownerId = "mock-uuid-1"))
    val propertyDraft: StateFlow<Property> = _propertyDraft.asStateFlow()

    private val _localImagePaths = MutableStateFlow<List<String>>(emptyList())
    val localImagePaths: StateFlow<List<String>> = _localImagePaths.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    fun updateBasicDetails(
        transactionType: String,
        category: String,
        address: String,
        city: String,
        state: String,
        lat: Double?,
        lng: Double?
    ) {
        _propertyDraft.value = _propertyDraft.value.copy(
            transactionType = transactionType,
            propertyCategory = category,
            address = address,
            city = city,
            state = state,
            latitude = lat,
            longitude = lng
        )
    }

    fun updatePropertyDetails(
        bedrooms: Int,
        bathrooms: Int,
        balconies: Int,
        areaSqft: Double,
        furnishingStatus: String,
        parkingSpaces: Int,
        ownershipType: String,
        price: Double
    ) {
        _propertyDraft.value = _propertyDraft.value.copy(
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            balconies = balconies,
            areaSqft = areaSqft,
            furnishingStatus = furnishingStatus,
            parkingSpaces = parkingSpaces,
            ownershipType = ownershipType,
            price = price
        )
    }

    fun updateAdditionalDetails(
        title: String,
        description: String,
        videoUrl: String?
    ) {
        _propertyDraft.value = _propertyDraft.value.copy(
            title = title,
            description = description,
            videoUrl = videoUrl
        )
    }

    fun addLocalImage(path: String) {
        _localImagePaths.value = _localImagePaths.value + path
    }

    fun removeLocalImage(path: String) {
        _localImagePaths.value = _localImagePaths.value - path
    }

    fun saveDraft() {
        viewModelScope.launch {
            savePropertyDraftUseCase(_propertyDraft.value, _localImagePaths.value)
        }
    }

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value += 1
            saveDraft()
        }
    }

    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    fun submitListing() {
        _submissionState.value = SubmissionState.Loading
        viewModelScope.launch {
            submitPropertyUseCase(_propertyDraft.value, _localImagePaths.value).collect { result ->
                result.onSuccess {
                    _submissionState.value = SubmissionState.Success(it)
                    // Reset draft after success
                    _propertyDraft.value = Property(ownerId = "mock-uuid-1")
                    _localImagePaths.value = emptyList()
                    _currentStep.value = 1
                }.onFailure {
                    _submissionState.value = SubmissionState.Error(it.message ?: "Failed to publish listing")
                }
            }
        }
    }

    fun resetSubmissionState() {
        _submissionState.value = SubmissionState.Idle
    }
}
