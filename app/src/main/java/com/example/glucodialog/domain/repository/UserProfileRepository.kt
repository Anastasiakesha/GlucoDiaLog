package com.example.glucodialog.domain.repository


import com.example.glucodialog.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun insertUserProfile(profile: UserProfile)
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)
}