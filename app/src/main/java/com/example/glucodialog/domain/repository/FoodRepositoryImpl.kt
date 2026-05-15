package com.example.glucodialog.domain.repository


import com.example.glucodialog.data.repository.FoodDao
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toEntity
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(
    private val dao: FoodDao
) : FoodRepository {

    override fun getAllFoodTypes(): Flow<List<FoodType>> =
        dao.getAllFoodItems().map { list -> list.map { it.toDomain() } }

    override suspend fun getFoodEntryById(id: Int): FoodEntry? =
        dao.getFoodEntryById(id)?.toDomain()

    override suspend fun insertFoodType(type: FoodType) {
        dao.insertFoodItem(type.toEntity())
    }

    override suspend fun insertAllFoodTypes(types: List<FoodType>) {
        dao.insertAllFoodItems(types.map { it.toEntity() })
    }

    override suspend fun insertFoodEntry(entry: FoodEntry) {
        dao.insertFoodEntry(entry.toEntity())
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> =
        dao.getAllFoodEntries().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllFoodEntriesOnce(): List<FoodEntry> =
        dao.getAllFoodEntriesOnce().map { it.toDomain() }

    override suspend fun getAllFoodEntriesWithTypesOnce(): List<FoodEntryWithTypeDomain> =
        dao.getAllFoodEntriesOnceWithItems().map { it.toDomain() }

    override fun getAllFoodEntriesWithTypesFlow(): Flow<List<FoodEntryWithTypeDomain>> =
        dao.getAllFoodEntriesWithItemsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getFoodTypeById(id: Int): FoodType? =
        dao.getFoodItemById(id)?.toDomain()

    override suspend fun getFoodTypeByName(name: String): FoodType? =
        dao.getFoodItemByName(name)?.toDomain()

    override suspend fun getFoodEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<FoodEntry> =
        dao.getFoodEntriesBetween(startTimestamp, endTimestamp).map { it.toDomain() }

    override suspend fun updateFoodEntry(entry: FoodEntry) {
        dao.updateFoodEntry(entry.toEntity())
    }

    override suspend fun deleteFoodEntry(entry: FoodEntry) {
        dao.deleteFoodEntry(entry.toEntity())
    }
}