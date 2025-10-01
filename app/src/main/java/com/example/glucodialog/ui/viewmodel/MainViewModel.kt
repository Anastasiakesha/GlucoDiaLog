package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.data.repository.UserProfileDao
import com.example.glucodialog.data.repository.UserProfileRepositoryImpl
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.domain.usecase.userprofile.GetUserProfileUseCase
import com.example.glucodialog.domain.usecase.userprofile.InsertUserProfileUseCase
import com.example.glucodialog.domain.usecase.userprofile.UpdateUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val insertUserProfileUseCase: InsertUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        getUserProfileUseCase().onEach { profile ->
            _userProfile.value = profile
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    fun insertUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            insertUserProfileUseCase(profile)
            _userProfile.value = profile
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            updateUserProfileUseCase(profile)
            _userProfile.value = profile
        }
    }
    class MainViewModelFactory(
        private val userProfileDao: UserProfileDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = UserProfileRepositoryImpl(userProfileDao)
            return MainViewModel(
                getUserProfileUseCase = GetUserProfileUseCase(repository),
                insertUserProfileUseCase = InsertUserProfileUseCase(repository),
                updateUserProfileUseCase = UpdateUserProfileUseCase(repository)
            ) as T
        }
    }
}