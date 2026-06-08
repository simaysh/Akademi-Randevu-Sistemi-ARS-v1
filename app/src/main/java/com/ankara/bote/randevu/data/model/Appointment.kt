package com.ankara.bote.randevu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeSlotId: Int,
    val academicianId: Int,
    val academicianName: String,
    val studentId: Int,
    val studentName: String = "",
    val groupId: Int? = null,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)