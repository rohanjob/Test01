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
        val supabaseUrl = "https://your-supabase-url.supabase.co"
        val supabaseKey = "your-supabase-anon-key"
        
        if (supabaseUrl.contains("your-supabase-url")) {
            // Return null if credentials are not configured, SupabaseService handles fallback
            return null
        }
        
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
    fun provideAuthRepository(remoteService: SupabaseService): AuthRepository {
        return AuthRepositoryImpl(remoteService)
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
