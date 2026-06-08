package com.ankara.bote.randevu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_slots")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val academicianId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val isAvailable: Boolean = true,
    val maxGroupSize: Int = 5
)