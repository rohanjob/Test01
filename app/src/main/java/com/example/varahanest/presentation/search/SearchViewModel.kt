package com.example.varahanest.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.usecase.GetPropertiesUseCase
import com.example.varahanest.domain.usecase.SearchPropertiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val results: List<Property>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getPropertiesUseCase: GetPropertiesUseCase,
    private val searchPropertiesUseCase: SearchPropertiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Filter states
    val query = MutableStateFlow("")
    val transactionType = MutableStateFlow("BUY") // BUY, RENT, COMMERCIAL
    val minPrice = MutableStateFlow<Double?>(null)
    val maxPrice = MutableStateFlow<Double?>(null)
    val bedrooms = MutableStateFlow<Int?>(null)
    val furnishedStatus = MutableStateFlow<String?>(null)
    val postedBy = MutableStateFlow<String?>(null)
    val verifiedOnly = MutableStateFlow(false)

    private val _searchHistory = MutableStateFlow<List<String>>(
        listOf("3 BHK in Gurugram", "Apartment rent under 30k", "Office space Sector 62", "Commercial shop buy")
    )
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    init {
        performSearch()
    }

    fun performSearch() {
        _uiState.value = SearchUiState.Loading
        viewModelScope.launch {
            try {
                // If there's a search query, run the search usecase, else get properties by category
                val flow = if (query.value.isNotEmpty()) {
                    searchPropertiesUseCase(query.value)
                } else {
                    getPropertiesUseCase(transactionType.value, forceRefresh = false)
                }

                flow.collect { list ->
                    // Apply in-memory client-side filters
                    val filtered = list.filter { property ->
                        val matchesPriceMin = minPrice.value == null || property.price >= minPrice.value!!
                        val matchesPriceMax = maxPrice.value == null || property.price <= maxPrice.value!!
                        val matchesBedrooms = bedrooms.value == null || property.bedrooms == bedrooms.value
                        val matchesFurnishing = furnishedStatus.value == null || property.furnishingStatus == furnishedStatus.value
                        val matchesPostedBy = postedBy.value == null || property.postedBy == postedBy.value
                        val matchesVerified = !verifiedOnly.value || property.verified

                        matchesPriceMin && matchesPriceMax && matchesBedrooms && matchesFurnishing && matchesPostedBy && matchesVerified
                    }
                    _uiState.value = SearchUiState.Success(filtered)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "An error occurred during search")
            }
        }
    }

    fun onQueryChanged(q: String) {
        query.value = q
    }

    fun onTransactionTypeChanged(type: String) {
        transactionType.value = type
        performSearch()
    }

    fun addHistoryQuery(q: String) {
        if (q.isNotEmpty() && !_searchHistory.value.contains(q)) {
            _searchHistory.value = listOf(q) + _searchHistory.value
        }
    }

    fun clearFilters() {
        minPrice.value = null
        maxPrice.value = null
        bedrooms.value = null
        furnishedStatus.value = null
        postedBy.value = null
        verifiedOnly.value = false
        performSearch()
    }
}
