package com.example.varahanest.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.varahanest.domain.model.SupportTicket
import com.example.varahanest.domain.usecase.CreateSupportTicketUseCase
import com.example.varahanest.domain.usecase.GetSupportTicketsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TicketSubmissionState {
    object Idle : TicketSubmissionState
    object Loading : TicketSubmissionState
    object Success : TicketSubmissionState
    data class Error(val message: String) : TicketSubmissionState
}

@HiltViewModel
class SupportCenterViewModel @Inject constructor(
    private val createSupportTicketUseCase: CreateSupportTicketUseCase,
    private val getSupportTicketsUseCase: GetSupportTicketsUseCase
) : ViewModel() {

    private val _tickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    private val _submissionState = MutableStateFlow<TicketSubmissionState>(TicketSubmissionState.Idle)
    val submissionState: StateFlow<TicketSubmissionState> = _submissionState.asStateFlow()

    init {
        loadTickets()
    }

    fun loadTickets() {
        viewModelScope.launch {
            getSupportTicketsUseCase().collect { list ->
                _tickets.value = list
            }
        }
    }

    fun submitTicket(subject: String, description: String) {
        _submissionState.value = TicketSubmissionState.Loading
        viewModelScope.launch {
            val ticket = SupportTicket(
                userId = "mock-uuid-1",
                subject = subject,
                description = description
            )
            createSupportTicketUseCase(ticket).collect { result ->
                result.onSuccess {
                    _submissionState.value = TicketSubmissionState.Success
                    loadTickets() // Refresh listing
                }.onFailure {
                    _submissionState.value = TicketSubmissionState.Error(it.message ?: "Failed to submit support ticket")
                }
            }
        }
    }

    fun resetSubmissionState() {
        _submissionState.value = TicketSubmissionState.Idle
    }
}
