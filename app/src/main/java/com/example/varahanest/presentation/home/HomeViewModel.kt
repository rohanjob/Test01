package com.example.varahanest.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.usecase.GetPropertiesUseCase
import com.example.varahanest.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val listings: List<Property>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPropertiesUseCase: GetPropertiesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProperties(forceRefresh = false)
    }

    fun loadProperties(forceRefresh: Boolean) {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                getPropertiesUseCase(forceRefresh = forceRefresh).collect { list ->
                    _uiState.value = HomeUiState.Success(list)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load properties")
            }
        }
    }

    fun toggleFavorite(propertyId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(propertyId).collect {
                // Local cache flow will automatically update the UI state
            }
        }
    }
}
