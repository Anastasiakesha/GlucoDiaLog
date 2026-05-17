package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "therapy_plans",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)

data class TherapyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val startDate: Long,
    val endDate: Long? = null,
    val notes: String = ""
)