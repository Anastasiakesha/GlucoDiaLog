package com.example.glucodialog.domain.repository

import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import kotlinx.coroutines.flow.Flow

interface InsulinRepository {

    fun getAllInsulinTypes(): Flow<List<InsulinType>>

    suspend fun insertInsulinType(type: InsulinType): Long

    suspend fun insertAllInsulinTypes(types: List<InsulinType>)

    suspend fun insertInsulinEntry(entry: InsulinEntry)

    fun getAllInsulinEntries(): Flow<List<InsulinEntry>>

    suspend fun getAllInsulinEntriesOnce(): List<InsulinEntry>

    suspend fun getAllInsulinEntriesWithTypesOnce(): List<InsulinEntryWithTypeDomain>

    fun getAllInsulinEntriesWithTypesFlow(): Flow<List<InsulinEntryWithTypeDomain>>

    suspend fun getInsulinTypeById(id: Int): InsulinType?

    suspend fun getInsulinTypeByName(name: String): InsulinType?

    suspend fun getInsulinEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<InsulinEntry>

    suspend fun updateInsulinEntry(entry: InsulinEntry)

    suspend fun deleteInsulinEntry(entry: InsulinEntry)
}