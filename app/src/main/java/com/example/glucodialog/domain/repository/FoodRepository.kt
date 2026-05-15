package com.example.glucodialog.domain.repository

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun getAllFoodTypes(): Flow<List<FoodType>>

    suspend fun getFoodEntryById(id: Int): FoodEntry?

    suspend fun insertFoodType(type: FoodType)

    suspend fun insertAllFoodTypes(types: List<FoodType>)

    suspend fun insertFoodEntry(entry: FoodEntry)

    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    suspend fun getAllFoodEntriesOnce(): List<FoodEntry>

    suspend fun getAllFoodEntriesWithTypesOnce(): List<FoodEntryWithTypeDomain>

    fun getAllFoodEntriesWithTypesFlow(): Flow<List<FoodEntryWithTypeDomain>>

    suspend fun getFoodTypeById(id: Int): FoodType?

    suspend fun getFoodTypeByName(name: String): FoodType?

    suspend fun getFoodEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<FoodEntry>

    suspend fun updateFoodEntry(entry: FoodEntry)

    suspend fun deleteFoodEntry(entry: FoodEntry)
}