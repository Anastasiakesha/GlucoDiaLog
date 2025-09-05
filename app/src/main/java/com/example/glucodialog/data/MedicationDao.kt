package com.example.glucodialog.data

import androidx.room.*
import com.example.glucodialog.data.relations.MedicationEntryWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medication_types")
    fun getAllMedicationTypes(): Flow<List<MedicationType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMedicationTypes(types: List<MedicationType>)

    @Insert
    suspend fun insertMedicationEntry(entry: MedicationEntry)

    @Query("SELECT * FROM medication_entries ORDER BY timestamp DESC")
    fun getAllMedicationEntries(): Flow<List<MedicationEntry>>

    @Query("SELECT * FROM medication_types WHERE id = :id LIMIT 1")
    suspend fun getMedicationById(id: Int): MedicationType?

    @Query("SELECT * FROM medication_entries ORDER BY timestamp DESC")
    suspend fun getAllMedicationEntriesOnce(): List<MedicationEntry>

    @Transaction
    @Query("SELECT * FROM medication_entries ORDER BY timestamp DESC")
    suspend fun getAllMedicationEntriesOnceWithTypes(): List<MedicationEntryWithType>

    @Query("SELECT * FROM medication_types WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getMedicationTypeByName(name: String): MedicationType?

    @Query("SELECT * FROM medication_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getMedicationEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<MedicationEntry>

    @Transaction
    @Query("SELECT * FROM medication_entries ORDER BY timestamp DESC")
    fun getAllMedicationEntriesWithTypesFlow(): Flow<List<MedicationEntryWithType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationType(type: MedicationType)

    @Update
    suspend fun updateMedicationEntry(entry: MedicationEntry)

    @Delete
    suspend fun deleteMedicationEntry(entry: MedicationEntry)

}