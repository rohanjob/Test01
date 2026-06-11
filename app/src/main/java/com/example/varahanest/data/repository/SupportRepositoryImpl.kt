package com.example.varahanest.data.repository

import com.example.varahanest.data.remote.SupabaseService
import com.example.varahanest.domain.model.SupportTicket
import com.example.varahanest.domain.repository.SupportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepositoryImpl @Inject constructor(
    private val remoteService: SupabaseService
) : SupportRepository {

    override fun createSupportTicket(ticket: SupportTicket): Flow<Result<SupportTicket>> = flow {
        emit(remoteService.submitSupportTicket(ticket))
    }

    override fun getSupportTickets(): Flow<List<SupportTicket>> = flow {
        emit(remoteService.fetchSupportTickets("mock-uuid-1"))
    }
}
