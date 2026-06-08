package com.ankara.bote.randevu.data.repository

import com.ankara.bote.randevu.data.AppDatabase
import com.ankara.bote.randevu.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    fun getAcademiciansFlow() = db.academicianDao().getAllFlow()

    fun getAvailableSlots(academicianId: Int, date: String) =
        db.timeSlotDao().getAvailableSlots(academicianId, date)

    fun getMyAppointments(userId: Int): Flow<List<Appointment>> =
        db.appointmentDao().getMyAppointments(userId)

    fun getPastAppointments(userId: Int): Flow<List<Appointment>> =
        db.appointmentDao().getPastAppointments(userId)

    fun getAppointmentsForAcademician(academicianId: Int): Flow<List<Appointment>> =
        db.appointmentDao().getAppointmentsForAcademician(academicianId)

    suspend fun createAppointment(
        slot: TimeSlot,
        academician: Academician,
        studentId: Int,
        studentName: String,
        groupId: Int? = null
    ): Appointment {
        val appointment = Appointment(
            timeSlotId = slot.id,
            studentId = studentId,
            studentName = studentName,
            academicianId = academician.id,
            academicianName = academician.name,
            date = slot.date,
            startTime = slot.startTime,
            endTime = slot.endTime,
            groupId = groupId
        )
        val id = db.appointmentDao().insert(appointment)
        db.timeSlotDao().updateAvailability(slot.id, false)
        return appointment.copy(id = id.toInt())
    }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String) {
        db.appointmentDao().updateStatus(appointmentId, status)
    }

    suspend fun cancelAppointment(appointment: Appointment) {
        db.appointmentDao().cancel(appointment.id)
        db.timeSlotDao().updateAvailability(appointment.timeSlotId, true)
    }

    fun getMyGroups(studentId: Int): Flow<List<Group>> =
        db.groupDao().getGroupsForStudent(studentId)

    suspend fun createGroup(name: String, ownerId: Int, memberIds: List<Int>) {
        val groupId = db.groupDao().insertGroup(Group(name = name, ownerId = ownerId))
        memberIds.forEach { studentId ->
            db.groupDao().insertMember(GroupMember(groupId = groupId.toInt(), studentId = studentId))
        }
    }

    suspend fun deleteGroup(groupId: Int) {
        db.groupDao().deleteGroup(groupId)
    }

    suspend fun getAcademicianByEmail(email: String): Academician? {
        return db.academicianDao().findByEmail(email)
    }

    suspend fun loginOrRegister(number: String, firstName: String, lastName: String): Student {
        val existing = db.studentDao().findByStudentNumber(number)
        if (existing != null) return existing
        val newId = db.studentDao().insert(
            Student(studentNumber = number, firstName = firstName, lastName = lastName)
        )
        return Student(id = newId.toInt(), studentNumber = number, firstName = firstName, lastName = lastName)
    }

    suspend fun ensureStudentExists(studentNumber: String): Int {
        val existing = db.studentDao().findByStudentNumber(studentNumber)
        if (existing != null) return existing.id
        val newId = db.studentDao().insert(
            Student(studentNumber = studentNumber, firstName = studentNumber, lastName = "")
        )
        return newId.toInt()
    }
}