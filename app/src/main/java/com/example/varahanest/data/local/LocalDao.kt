package com.example.varahanest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {

    // Properties Cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperties(properties: List<PropertyEntity>)

    @Query("SELECT * FROM cached_properties ORDER BY createdAt DESC")
    fun getAllPropertiesFlow(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM cached_properties WHERE id = :id LIMIT 1")
    fun getPropertyById(id: String): PropertyEntity?

    @Query("SELECT * FROM cached_properties WHERE transactionType = :type ORDER BY createdAt DESC")
    fun getPropertiesByTransactionTypeFlow(type: String): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM cached_properties WHERE title LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%'")
    fun searchPropertiesFlow(query: String): Flow<List<PropertyEntity>>

    @Query("DELETE FROM cached_properties")
    fun clearProperties()

    // Favorites Cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE propertyId = :propertyId AND userId = :userId")
    fun deleteFavorite(propertyId: String, userId: String)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesFlow(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE propertyId = :propertyId AND userId = :userId)")
    fun isFavoriteFlow(propertyId: String, userId: String): Flow<Boolean>

    // Search History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchQuery(search: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getSearchHistoryFlow(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE query = :query")
    fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    fun clearSearchHistory()

    // Recently Viewed
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentlyViewed(view: RecentlyViewedEntity)

    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT 20")
    fun getRecentlyViewedFlow(): Flow<List<RecentlyViewedEntity>>

    // Drafts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDraft(draft: PropertyDraftEntity): Long

    @Query("SELECT * FROM property_drafts ORDER BY lastUpdated DESC")
    fun getAllDraftsFlow(): Flow<List<PropertyDraftEntity>>

    @Query("SELECT * FROM property_drafts WHERE id = :id LIMIT 1")
    fun getDraftById(id: Int): PropertyDraftEntity?

    @Query("DELETE FROM property_drafts WHERE id = :id")
    fun deleteDraft(id: Int)

    // User Profile Cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getActiveUserProfile(): UserProfileEntity?

    @Query("DELETE FROM user_profiles")
    fun clearUserProfile()
}
