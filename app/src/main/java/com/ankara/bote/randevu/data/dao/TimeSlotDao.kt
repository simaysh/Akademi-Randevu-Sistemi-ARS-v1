package com.ankara.bote.randevu.data.dao

import androidx.room.*
import com.ankara.bote.randevu.data.model.TimeSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeSlotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TimeSlot>)

    @Query("""
        SELECT * FROM time_slots 
        WHERE academicianId = :academicianId 
        AND date = :date 
        AND isAvailable = 1
        ORDER BY startTime ASC
    """)
    fun getAvailableSlots(academicianId: Int, date: String): Flow<List<TimeSlot>>

    @Query("UPDATE time_slots SET isAvailable = :available WHERE id = :id")
    suspend fun updateAvailability(id: Int, available: Boolean)

    @Query("SELECT * FROM time_slots WHERE id = :id")
    suspend fun findById(id: Int): TimeSlot?
}
