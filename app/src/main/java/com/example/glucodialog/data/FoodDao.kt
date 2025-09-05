package com.example.glucodialog.data

import androidx.room.*
import com.example.glucodialog.data.relations.FoodEntryWithItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM food_items")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFoodItems(items: List<FoodItem>)

    @Insert
    suspend fun insertFoodEntry(entry: FoodEntry)

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getFoodItemById(id: Int): FoodItem?

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    suspend fun getAllFoodEntriesOnce(): List<FoodEntry>

    @Transaction
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    suspend fun getAllFoodEntriesOnceWithItems(): List<FoodEntryWithItem>

    @Query("SELECT * FROM food_items WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getFoodItemByName(name: String): FoodItem?

    @Query("SELECT * FROM food_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getFoodEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<FoodEntry>

    @Transaction
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoodEntriesWithItemsFlow(): Flow<List<FoodEntryWithItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(type: FoodItem)

    @Update
    suspend fun updateFoodEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteFoodEntry(entry: FoodEntry)

}