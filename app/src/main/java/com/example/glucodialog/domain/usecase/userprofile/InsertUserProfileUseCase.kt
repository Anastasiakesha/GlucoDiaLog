package com.example.glucodialog.domain.usecase.userprofile

import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.domain.repository.UserProfileRepository

class InsertUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        repository.insertUserProfile(profile)
    }
}