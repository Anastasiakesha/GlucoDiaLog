package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.repository.ActivityRepository

class GetActivitiesBetweenUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(startTimestamp: Long, endTimestamp: Long) =
        repository.getActivitiesBetween(startTimestamp, endTimestamp)
}