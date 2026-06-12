package com.example.varahanest.data.remote

import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.domain.model.Lead
import com.example.varahanest.domain.model.SupportTicket
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class SupabaseService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseClient: SupabaseClient?
) {
    data class ApprovedAgent(
        val email: String,
        val password: String,
        val id: String,
        val fullName: String,
        val phoneNumber: String
    )

    private val approvedAgents = listOf(
        ApprovedAgent("agent@rohith123.com", "agent123", "mock-agent-123", "Agent Rohith", "+919876543210"),
        ApprovedAgent("agent@suresh101.com", "agent123", "mock-agent-101", "Agent Suresh", "+919102938475")
    )

    // Falls back to mock data if supabase client is not configured/initialized
    private val isRealSupabase = supabaseClient != null

    private fun saveMockUser(user: UserProfile) {
        val prefs = context.getSharedPreferences("varaha_nest_mock_session", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("full_name", user.fullName)
            putString("phone_number", user.phoneNumber)
            putString("role", user.role)
            putBoolean("is_premium", user.isPremium)
            apply()
        }
    }

    private fun getMockUser(): UserProfile? {
        val prefs = context.getSharedPreferences("varaha_nest_mock_session", Context.MODE_PRIVATE)
        val id = prefs.getString("user_id", null) ?: return null
        val fullName = prefs.getString("full_name", "") ?: ""
        val phoneNumber = prefs.getString("phone_number", "") ?: ""
        val role = prefs.getString("role", "USER") ?: "USER"
        val isPremium = prefs.getBoolean("is_premium", false)
        return UserProfile(id, fullName, phoneNumber, role, isPremium)
    }

    private fun clearMockUser() {
        val prefs = context.getSharedPreferences("varaha_nest_mock_session", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    suspend fun loginWithEmail(email: String, psw: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        val isAgent = trimmedEmail.startsWith("agent", ignoreCase = true) || trimmedEmail.contains("agent", ignoreCase = true)
        if (isAgent) {
            val approved = approvedAgents.find { it.email.equals(trimmedEmail, ignoreCase = true) }
            if (approved == null) {
                return@withContext Result.failure(Exception("This Agent account is not approved by Admin."))
            }
            if (approved.password != psw) {
                return@withContext Result.failure(Exception("Incorrect password for Agent account."))
            }
            val agentProfile = UserProfile(
                id = approved.id,
                fullName = approved.fullName,
                phoneNumber = approved.phoneNumber,
                role = "AGENT",
                isPremium = true
            )
            if (!isRealSupabase) {
                saveMockUser(agentProfile)
            }
            return@withContext Result.success(agentProfile)
        }
        if (!isRealSupabase) {
            // Simulated login for previewing
            val defaultUser = UserProfile("mock-uuid-1", "Varaha User", "+919876543210", "OWNER")
            saveMockUser(defaultUser)
            return@withContext Result.success(defaultUser)
        }
        try {
            supabaseClient!!.auth.signInWith(Email) {
                this.email = email
                this.password = psw
            }
            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                Result.success(UserProfile(currentUser.id, "Authenticated User", currentUser.phone ?: "", "USER"))
            } else {
                Result.failure(Exception("Login succeeded but user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, psw: String, fullName: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.contains("agent", ignoreCase = true)) {
            return@withContext Result.failure(Exception("Agent accounts can only be created by Admin in the Admin Portal."))
        }
        if (!isRealSupabase) {
            return@withContext Result.success(UserProfile("mock-uuid-1", fullName, "+919876543210", "USER"))
        }
        try {
            supabaseClient!!.auth.signUpWith(Email) {
                this.email = email
                this.password = psw
                data = buildJsonObject {
                    put("full_name", fullName)
                }
            }
            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                Result.success(UserProfile(currentUser.id, fullName, currentUser.phone ?: "", "USER"))
            } else {
                Result.failure(Exception("Registration succeeded but user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendOtp(phone: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            return@withContext Result.success(Unit)
        }
        try {
            supabaseClient!!.auth.signInWith(OTP) {
                this.phone = phone
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(phone: String, token: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            val otpUser = UserProfile("mock-uuid-1", "OTP User", phone, "USER")
            saveMockUser(otpUser)
            return@withContext Result.success(otpUser)
        }
        try {
            supabaseClient!!.auth.verifyPhoneOtp(
                type = io.github.jan.supabase.auth.OtpType.Phone.SMS,
                phone = phone,
                token = token
            )

            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                Result.success(UserProfile(currentUser.id, "OTP User", phone, "USER"))
            } else {
                Result.failure(Exception("OTP verification succeeded but user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): UserProfile? = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            return@withContext getMockUser()
        }
        try {
            val user = supabaseClient!!.auth.currentUserOrNull() ?: return@withContext null
            // Fetch profile
            val profile = supabaseClient.postgrest["profiles"]
                .select(Columns.ALL) {
                    filter {
                        eq("id", user.id)
                    }
                }.decodeSingleOrNull<UserProfile>()
            profile ?: UserProfile(user.id, "Active User", user.phone ?: "", "USER")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            clearMockUser()
            return@withContext Result.success(Unit)
        }
        try {
            supabaseClient!!.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchProperties(): List<Property> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            return@withContext getMockProperties()
        }
        try {
            supabaseClient!!.postgrest["properties"]
                .select(Columns.ALL) {
                    filter {
                        eq("status", "ACTIVE")
                    }
                }.decodeList<Property>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun submitProperty(property: Property): Property = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            return@withContext property.copy(id = java.util.UUID.randomUUID().toString())
        }
        supabaseClient!!.postgrest["properties"].insert(property).decodeAs<Property>()
    }

    suspend fun uploadMedia(file: File, propertyId: String): String = withContext(Dispatchers.IO) {
        if (!isRealSupabase) {
            return@withContext "https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x"
        }
        val bucket = supabaseClient!!.storage.from("property-media")
        val path = "$propertyId/${file.name}"
        bucket.upload(path, file.readBytes())
        bucket.publicUrl(path)
    }

    suspend fun submitLead(lead: Lead): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) return@withContext Result.success(Unit)
        try {
            supabaseClient!!.postgrest["leads"].insert(lead)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitSupportTicket(ticket: SupportTicket): Result<SupportTicket> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) return@withContext Result.success(ticket.copy(id = java.util.UUID.randomUUID().toString()))
        try {
            val inserted = supabaseClient!!.postgrest["support_tickets"].insert(ticket).decodeAs<SupportTicket>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSupportTickets(userId: String): List<SupportTicket> = withContext(Dispatchers.IO) {
        if (!isRealSupabase) return@withContext emptyList()
        try {
            supabaseClient!!.postgrest["support_tickets"]
                .select(Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<SupportTicket>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Mock property generation
    private fun getMockProperties(): List<Property> {
        return listOf(
            Property(
                id = "mock-1",
                ownerId = "mock-uuid-2",
                title = "Emerald Estates Luxury Apartment",
                description = "Experience modern urban living in this pristine premium 3 BHK condominium situated right in the heart of Sector 62. Comes with top-tier amenities, smart home integrations, 24/7 power backup, high-end marble flooring, and access to private clubhouse and gym facilities.",
                price = 28000000.0, // 2.8 Cr
                transactionType = "BUY",
                propertyCategory = "RESIDENTIAL_APARTMENT",
                bedrooms = 3,
                bathrooms = 3,
                balconies = 2,
                areaSqft = 1850.0,
                address = "Tower A, Sector 62",
                city = "Gurugram",
                state = "Haryana",
                latitude = 28.412,
                longitude = 77.031,
                furnishingStatus = "FULLY_FURNISHED",
                parkingSpaces = 2,
                ownershipType = "FREEHOLD",
                postedBy = "OWNER",
                verified = false,
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuA_RFOE-fC8jbLm7roFS-7kf0iGFsgGxZYZgXLiyA8i8Bl45rTHkeYV092aepK4hx7E0nH_0xMc-bVBan4Cy4BI0vfa4Tz10WMRHrF4bwbqcnCKm3E1Moo5ySkcpdnTXAIPitusTxoiyJOVcJ8shNdYf6kUb6ymuucg3WLjCkw3i0MwBufqinPsug02uQKPmC4WcAABHD3Gitk7_xJ6b_rBUNsxi4jT2Zb9OgQqk8XT-IxTb07xDEgD3iPcNcVgRLo7lqc129ZyEoF3"
                )
            ),
            Property(
                id = "mock-2",
                ownerId = "mock-uuid-3",
                title = "Worli Sky Penthouses",
                description = "Magnificent duplex penthouse overlooking the Arabian Sea. Features direct elevator entry, double height ceilings, customized modern kitchen, sprawling sun deck, and premium bathroom fittings.",
                price = 120000000.0, // 12 Cr
                transactionType = "BUY",
                propertyCategory = "RESIDENTIAL_APARTMENT",
                bedrooms = 5,
                bathrooms = 6,
                balconies = 4,
                areaSqft = 4500.0,
                address = "Worli Sea Face Road, Worli",
                city = "Mumbai",
                state = "Maharashtra",
                latitude = 19.018,
                longitude = 72.815,
                furnishingStatus = "FULLY_FURNISHED",
                parkingSpaces = 3,
                ownershipType = "FREEHOLD",
                postedBy = "BUILDER",
                verified = true,
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBUVZayhVS78h9G25sfWEGIpzPEjlG5Xf-O1aAO_M5bCxdwcLxxW_yF4HG3XAm8C8FkuyGtVKSkxbIR7CoaZw6n-BJk8AyTwx0uPeK5sApVRFJEIwuu5ph7QOJ0kyJxkE5cYjNm0l7K2Nq-3hJhvMztYiubFaTzvbll3fprX6CUkOh-pGJb9VEd84S9MljCe2NIaUf-DZquYVQhMaU8yJ-ki4n7NnmqowdUOf0PilneKS0sSUjkzC0GPSo6OqEIUEOBHtCd_8OhY0ri"
                )
            ),
            Property(
                id = "mock-3",
                ownerId = "mock-uuid-4",
                title = "Modern Commercial Office Hub",
                description = "Excellent prime corporate space suitable for tech startups, finance firms or co-working setups. Positioned on high floor, offering high-speed elevators, centralized central air conditioning, and complete server backup infrastructure.",
                price = 120000.0, // 1.2L per month rent
                transactionType = "RENT",
                propertyCategory = "COMMERCIAL_OFFICE",
                bedrooms = 0,
                bathrooms = 2,
                balconies = 0,
                areaSqft = 2400.0,
                address = "DLF Cyber City, Phase 3",
                city = "Gurugram",
                state = "Haryana",
                latitude = 28.495,
                longitude = 77.089,
                furnishingStatus = "SEMI_FURNISHED",
                parkingSpaces = 4,
                ownershipType = "LEASEHOLD",
                postedBy = "AGENT",
                verified = false,
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x"
                )
            ),
            Property(
                id = "mock-4",
                ownerId = "mock-uuid-5",
                title = "Varaha Premium Heights",
                description = "Ultra premium high-rise residence with a panoramic city view, private deck, automated control systems, VRV air conditioning, customized Italian marble finishes, and standard premium membership amenities.",
                price = 85000000.0, // 8.5 Cr
                transactionType = "BUY",
                propertyCategory = "RESIDENTIAL_APARTMENT",
                bedrooms = 4,
                bathrooms = 4,
                balconies = 3,
                areaSqft = 3200.0,
                address = "Golf Course Extension Road",
                city = "Gurugram",
                state = "Haryana",
                latitude = 28.398,
                longitude = 77.098,
                furnishingStatus = "FULLY_FURNISHED",
                parkingSpaces = 3,
                ownershipType = "FREEHOLD",
                postedBy = "BUILDER",
                verified = true,
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBUVZayhVS78h9G25sfWEGIpzPEjlG5Xf-O1aAO_M5bCxdwcLxxW_yF4HG3XAm8C8FkuyGtVKSkxbIR7CoaZw6n-BJk8AyTwx0uPeK5sApVRFJEIwuu5ph7QOJ0kyJxkE5cYjNm0l7K2Nq-3hJhvMztYiubFaTzvbll3fprX6CUkOh-pGJb9VEd84S9MljCe2NIaUf-DZquYVQhMaU8yJ-ki4n7NnmqowdUOf0PilneKS0sSUjkzC0GPSo6OqEIUEOBHtCd_8OhY0ri"
                )
            )
        )
    }
}
