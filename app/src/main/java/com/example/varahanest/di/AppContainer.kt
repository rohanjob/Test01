package com.example.varahanest.di

import android.content.Context
import androidx.room.Room
import com.example.varahanest.data.local.LocalDao
import com.example.varahanest.data.local.VarahaDatabase
import com.example.varahanest.data.remote.SupabaseService
import com.example.varahanest.data.repository.AuthRepositoryImpl
import com.example.varahanest.data.repository.PropertyRepositoryImpl
import com.example.varahanest.data.repository.SupportRepositoryImpl
import com.example.varahanest.domain.repository.AuthRepository
import com.example.varahanest.domain.repository.PropertyRepository
import com.example.varahanest.domain.repository.SupportRepository

class AppContainer(private val context: Context) {

    private val database: VarahaDatabase by lazy {
        Room.databaseBuilder(
            context,
            VarahaDatabase::class.java,
            "varaha_nest_database"
        ).fallbackToDestructiveMigration().build()
    }

    val localDao: LocalDao by lazy {
        database.dao
    }

    val supabaseClient: io.github.jan.supabase.SupabaseClient? by lazy {
        val supabaseUrl = "https://odbomxyjoqswryacwsel.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9kYm9teHlqb3Fzd3J5YWN3c2VsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODExNDM1OTUsImV4cCI6MjA5NjcxOTU5NX0.XEJRMvzu3mB3T2InFhMDAkn6TUgqWm98gGC3ZJLMbZw"
        try {
            io.github.jan.supabase.createSupabaseClient(supabaseUrl, supabaseKey) {
                install(io.github.jan.supabase.auth.Auth)
                install(io.github.jan.supabase.postgrest.Postgrest)
                install(io.github.jan.supabase.storage.Storage)
            }
        } catch (e: Exception) {
            null
        }
    }

    val remoteService: SupabaseService by lazy {
        SupabaseService(supabaseClient)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(localDao, remoteService)
    }

    val propertyRepository: PropertyRepository by lazy {
        PropertyRepositoryImpl(localDao, remoteService)
    }

    val supportRepository: SupportRepository by lazy {
        SupportRepositoryImpl(remoteService)
    }
}
