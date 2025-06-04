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
}