package com.ankara.bote.randevu.data.dao

import androidx.room.*
import com.ankara.bote.randevu.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: Appointment): Long

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): Appointment?

    @Query("""
        SELECT DISTINCT a.* FROM appointments a
        LEFT JOIN group_members gm ON a.groupId = gm.groupId
        WHERE (a.studentId = :studentId OR gm.studentId = :studentId)
        AND a.date >= date('now')
        ORDER BY a.date ASC, a.startTime ASC
    """)
    fun getMyAppointments(studentId: Int): Flow<List<Appointment>>

    @Query("""
        SELECT DISTINCT a.* FROM appointments a
        LEFT JOIN group_members gm ON a.groupId = gm.groupId
        WHERE (a.studentId = :studentId OR gm.studentId = :studentId)
        AND a.date < date('now')
        ORDER BY a.date DESC, a.startTime DESC
    """)
    fun getPastAppointments(studentId: Int): Flow<List<Appointment>>

    @Query("""
        SELECT * FROM appointments 
        WHERE academicianId = :academicianId 
        ORDER BY date ASC, startTime ASC
    """)
    fun getAppointmentsForAcademician(academicianId: Int): Flow<List<Appointment>>

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE appointments SET status = 'CANCELLED' WHERE id = :id")
    suspend fun cancel(id: Int)

    @Query("SELECT * FROM appointments WHERE date = :date AND studentId = :studentId")
    fun getAppointmentsOnDate(date: String, studentId: Int): Flow<List<Appointment>>
}
