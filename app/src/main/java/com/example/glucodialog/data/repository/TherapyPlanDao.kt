package com.example.glucodialog.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.glucodialog.data.local.InsulinTherapyPlan
import com.example.glucodialog.data.local.MedicationTherapyPlan
import com.example.glucodialog.data.local.TherapyPlan
import com.example.glucodialog.data.relations.TherapyPlanWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TherapyPlanDao {
    @Insert
    suspend fun insertTherapyPlan(plan: TherapyPlan): Long

    @Insert
    suspend fun insertInsulinPlan(plan: InsulinTherapyPlan)

    @Insert
    suspend fun insertMedicationPlan(plan: MedicationTherapyPlan)

    @Query("SELECT * FROM therapy_plans WHERE userId = :userId AND endDate IS NULL LIMIT 1")
    fun getActivePlanFlow(userId: Int): Flow<TherapyPlan?>

    @Query("SELECT * FROM insulin_therapy_plans WHERE therapyPlanId = :planId")
    fun getInsulinPlansForTherapy(planId: Int): Flow<List<InsulinTherapyPlan>>

    @Query("SELECT * FROM medication_therapy_plans WHERE therapyPlanId = :planId")
    fun getMedicationPlansForTherapy(planId: Int): Flow<List<MedicationTherapyPlan>>

    @Transaction
    @Query("SELECT * FROM therapy_plans WHERE userId = :userId AND endDate IS NULL LIMIT 1")
    fun getActivePlanWithDetailsFlow(userId: Int): Flow<TherapyPlanWithDetails?>

    @Query("UPDATE therapy_plans SET endDate = :endDate WHERE id = :planId")
    suspend fun finishPlan(planId: Int, endDate: Long)
}