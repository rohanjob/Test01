package com.example.varahanest.data.repository

import com.example.varahanest.data.local.LocalDao
import com.example.varahanest.data.local.PropertyEntity
import com.example.varahanest.data.local.FavoriteEntity
import com.example.varahanest.data.local.PropertyDraftEntity
import com.example.varahanest.data.remote.SupabaseService
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.model.Lead
import com.example.varahanest.domain.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val localDao: LocalDao,
    private val remoteService: SupabaseService
) : PropertyRepository {

    override fun getProperties(forceRefresh: Boolean): Flow<List<Property>> = flow {
        // Emit cached data first
        val cached = localDao.getAllPropertiesFlow().map { list -> list.map { it.toDomain() } }.first()
        emit(cached)
        
        // Fetch from remote if force refresh or cache is empty
        if (forceRefresh || cached.isEmpty()) {
            try {
                val remote = remoteService.fetchProperties()
                if (remote.isNotEmpty()) {
                    localDao.clearProperties()
                    localDao.insertProperties(remote.map { it.toEntity() })
                    emit(remote)
                }
            } catch (e: Exception) {
                // If remote fails, fallback is already emitted cached data
            }
        } else {
            // Keep observing local cache
            localDao.getAllPropertiesFlow().map { list -> list.map { it.toDomain() } }.collect {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getPropertiesByTransactionType(type: String, forceRefresh: Boolean): Flow<List<Property>> = flow {
        val cached = localDao.getPropertiesByTransactionTypeFlow(type).map { list -> list.map { it.toDomain() } }.first()
        emit(cached)

        if (forceRefresh || cached.isEmpty()) {
            try {
                val remote = remoteService.fetchProperties()
                if (remote.isNotEmpty()) {
                    localDao.clearProperties()
                    localDao.insertProperties(remote.map { it.toEntity() })
                    emit(remote.filter { it.transactionType == type })
                }
            } catch (e: Exception) {
                // Fallback to cached
            }
        } else {
            localDao.getPropertiesByTransactionTypeFlow(type).map { list -> list.map { it.toDomain() } }.collect {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getPropertyDetails(id: String): Flow<Property?> = flow {
        val local = localDao.getPropertyById(id)?.toDomain()
        emit(local)
        
        // Fetch fresh details from remote and update local cache
        try {
            val remoteList = remoteService.fetchProperties()
            val remote = remoteList.find { it.id == id }
            if (remote != null) {
                localDao.insertProperties(listOf(remote.toEntity()))
                emit(remote)
            }
        } catch (e: Exception) {
            // Ignore failure
        }
    }.flowOn(Dispatchers.IO)

    override fun searchProperties(query: String): Flow<List<Property>> {
        return localDao.searchPropertiesFlow(query).map { list -> list.map { it.toDomain() } }.flowOn(Dispatchers.IO)
    }

    override fun getFavorites(): Flow<List<Property>> {
        // Mock favorites list joined with properties
        return localDao.getAllPropertiesFlow().map { properties ->
            val favIds = localDao.getFavoritesFlow("mock-uuid-1").first().map { it.propertyId }.toSet()
            properties.filter { favIds.contains(it.id) }.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun toggleFavorite(propertyId: String): Flow<Unit> = flow {
        val userId = "mock-uuid-1"
        val exists = localDao.isFavoriteFlow(propertyId, userId).first()
        if (exists) {
            localDao.deleteFavorite(propertyId, userId)
        } else {
            localDao.insertFavorite(FavoriteEntity(
                id = java.util.UUID.randomUUID().toString(),
                propertyId = propertyId,
                userId = userId,
                addedAt = System.currentTimeMillis()
            ))
        }
        emit(Unit)
    }.flowOn(Dispatchers.IO)

    override fun isFavorite(propertyId: String): Flow<Boolean> {
        return localDao.isFavoriteFlow(propertyId, "mock-uuid-1").flowOn(Dispatchers.IO)
    }

    override fun submitLead(lead: Lead): Flow<Result<Unit>> = flow {
        val result = remoteService.submitLead(lead)
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getDrafts(): Flow<List<Property>> {
        return localDao.getAllDraftsFlow().map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveDraft(property: Property, localImagePaths: List<String>): Long = withContext(Dispatchers.IO) {
        return@withContext localDao.saveDraft(property.toDraftEntity(localImagePaths))
    }

    override suspend fun deleteDraft(draftId: Int) = withContext(Dispatchers.IO) {
        localDao.deleteDraft(draftId)
    }

    override fun submitProperty(property: Property, localImagePaths: List<String>): Flow<Result<Property>> = flow {
        try {
            val uploadedUrls = mutableListOf<String>()
            // Upload local images to Supabase storage
            localImagePaths.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    val url = remoteService.uploadMedia(file, property.id.ifEmpty { "temp" })
                    uploadedUrls.add(url)
                } else {
                    // Fallback URL if local file is mock/non-existent
                    uploadedUrls.add("https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x")
                }
            }
            
            val finalProperty = property.copy(
                imageUrls = if (uploadedUrls.isNotEmpty()) uploadedUrls else listOf("https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x")
            )
            
            val submitted = remoteService.submitProperty(finalProperty)
            localDao.insertProperties(listOf(submitted.toEntity()))
            emit(Result.success(submitted))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}

// Mapper extension functions
fun PropertyEntity.toDomain() = Property(
    id = id,
    ownerId = ownerId,
    title = title,
    description = description,
    price = price,
    transactionType = transactionType,
    propertyCategory = propertyCategory,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    balconies = balconies,
    areaSqft = areaSqft,
    address = address,
    city = city,
    state = state,
    latitude = latitude,
    longitude = longitude,
    furnishingStatus = furnishingStatus,
    parkingSpaces = parkingSpaces,
    ownershipType = ownershipType,
    postedBy = postedBy,
    verified = verified,
    imageUrls = if (imageUrls.isEmpty()) emptyList() else imageUrls.split(","),
    videoUrl = videoUrl,
    createdAt = createdAt
)

fun Property.toEntity() = PropertyEntity(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    ownerId = ownerId.ifEmpty { "mock-uuid-1" },
    title = title,
    description = description,
    price = price,
    transactionType = transactionType,
    propertyCategory = propertyCategory,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    balconies = balconies,
    areaSqft = areaSqft,
    address = address,
    city = city,
    state = state,
    latitude = latitude,
    longitude = longitude,
    furnishingStatus = furnishingStatus,
    parkingSpaces = parkingSpaces,
    ownershipType = ownershipType,
    postedBy = postedBy,
    verified = verified,
    imageUrls = imageUrls.joinToString(","),
    videoUrl = videoUrl,
    createdAt = createdAt
)

fun PropertyDraftEntity.toDomain() = Property(
    id = "draft-$id",
    title = title,
    description = description,
    price = price,
    transactionType = transactionType,
    propertyCategory = propertyCategory,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    balconies = balconies,
    areaSqft = areaSqft,
    address = address,
    city = city,
    state = state,
    latitude = latitude,
    longitude = longitude,
    furnishingStatus = furnishingStatus,
    parkingSpaces = parkingSpaces,
    ownershipType = ownershipType,
    imageUrls = if (localImagePaths.isEmpty()) emptyList() else localImagePaths.split(","),
    videoUrl = localVideoPath,
    createdAt = lastUpdated
)

fun Property.toDraftEntity(localImagePaths: List<String>) = PropertyDraftEntity(
    id = if (id.startsWith("draft-")) id.substringAfter("draft-").toInt() else 0,
    title = title,
    description = description,
    price = price,
    transactionType = transactionType,
    propertyCategory = propertyCategory,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    balconies = balconies,
    areaSqft = areaSqft,
    address = address,
    city = city,
    state = state,
    latitude = latitude,
    longitude = longitude,
    furnishingStatus = furnishingStatus,
    parkingSpaces = parkingSpaces,
    ownershipType = ownershipType,
    localImagePaths = localImagePaths.joinToString(","),
    localVideoPath = videoUrl,
    lastUpdated = System.currentTimeMillis()
)
