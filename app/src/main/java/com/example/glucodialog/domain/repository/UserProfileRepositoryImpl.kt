package com.example.glucodialog.data.repository

import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toLocal
import com.example.glucodialog.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override suspend fun insertUserProfile(profile: UserProfile) {
        dao.insertUserProfile(profile.toLocal())
    }

    override fun getUserProfile(): Flow<UserProfile?> {
        return dao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        dao.updateUserProfile(profile.toLocal())
    }
}