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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VarahaDatabase {
        return Room.databaseBuilder(
            context,
            VarahaDatabase::class.java,
            "varaha_nest_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideLocalDao(db: VarahaDatabase): LocalDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient? {
        val supabaseUrl = "https://odbomxyjoqswryacwsel.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9kYm9teHlqb3Fzd3J5YWN3c2VsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODExNDM1OTUsImV4cCI6MjA5NjcxOTU5NX0.XEJRMvzu3mB3T2InFhMDAkn6TUgqWm98gGC3ZJLMbZw"
        
        return try {
            createSupabaseClient(supabaseUrl, supabaseKey) {
                install(Auth)
                install(Postgrest)
                install(Storage)
            }
        } catch (e: Exception) {
            null
        }
    }

    @Provides
    @Singleton
    fun provideAuthRepository(localDao: LocalDao, remoteService: SupabaseService): AuthRepository {
        return AuthRepositoryImpl(localDao, remoteService)
    }

    @Provides
    @Singleton
    fun providePropertyRepository(localDao: LocalDao, remoteService: SupabaseService): PropertyRepository {
        return PropertyRepositoryImpl(localDao, remoteService)
    }

    @Provides
    @Singleton
    fun provideSupportRepository(remoteService: SupabaseService): SupportRepository {
        return SupportRepositoryImpl(remoteService)
    }
}
