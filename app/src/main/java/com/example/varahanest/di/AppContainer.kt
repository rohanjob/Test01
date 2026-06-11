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

    val remoteService: SupabaseService by lazy {
        SupabaseService(null) // Mock client fallback
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
