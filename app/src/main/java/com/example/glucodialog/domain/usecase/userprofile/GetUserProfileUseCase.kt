package com.example.glucodialog.domain.usecase.userprofile


import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return repository.getUserProfile()
    }
}