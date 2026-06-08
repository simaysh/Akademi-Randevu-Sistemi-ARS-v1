package com.ankara.bote.randevu.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.ankara.bote.randevu.data.AppDatabase
import com.ankara.bote.randevu.data.SessionManager
import com.ankara.bote.randevu.data.model.*
import com.ankara.bote.randevu.data.repository.AppRepository
import com.ankara.bote.randevu.worker.AppointmentReminderWorker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = AppRepository(AppDatabase.getInstance(application))
    val session = SessionManager(application)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    val academicians: LiveData<List<Academician>> =
        repository.getAcademiciansFlow().asLiveData()

    private val _selectedAcademicianId = MutableLiveData<Int>()

    private val _openTimeSlotsFor = MutableLiveData<Academician?>()
    val openTimeSlotsFor: LiveData<Academician?> = _openTimeSlotsFor

    private val timeSlotTrigger = MediatorLiveData<Pair<Int, String>>().apply {
        addSource(_selectedAcademicianId) { acId ->
            _selectedDate.value?.format(dateFormatter)?.let { date -> value = acId to date }
        }
        addSource(_selectedDate) { date ->
            _selectedAcademicianId.value?.let { acId -> value = acId to date.format(dateFormatter) }
        }
    }

    val timeSlots: LiveData<List<TimeSlot>> = timeSlotTrigger.switchMap { (acId, date) ->
        repository.getAvailableSlots(acId, date).asLiveData()
    }

    val myAppointments: LiveData<List<Appointment>> =
        repository.getMyAppointments(session.userId).asLiveData()

    val myPastAppointments: LiveData<List<Appointment>> =
        repository.getPastAppointments(session.userId).asLiveData()

    val academicianRequests: LiveData<List<Appointment>> =
        repository.getAppointmentsForAcademician(session.userId).asLiveData()

    val myGroups: LiveData<List<Group>> =
        repository.getMyGroups(session.userId).asLiveData()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun selectDate(date: LocalDate) { _selectedDate.value = date }

    fun selectAcademician(academicianId: Int) {
        _selectedAcademicianId.value = academicianId
        academicians.value?.find { it.id == academicianId }?.let {
            _openTimeSlotsFor.value = it
        }
    }

    fun onTimeSlotsOpened() { _openTimeSlotsFor.value = null }

    fun bookAppointment(slot: TimeSlot, academician: Academician, groupId: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val appointment = repository.createAppointment(
                    slot, academician, session.userId, session.userName, groupId
                )
                scheduleReminder(appointment)
            } catch (e: Exception) {
                _error.value = "Randevu alınamadı: ${e.message}"
            }
            _loading.value = false
        }
    }

    fun updateAppointmentStatus(appointmentId: Int, status: String) {
        viewModelScope.launch {
            try {
                repository.updateAppointmentStatus(appointmentId, status)
            } catch (e: Exception) {
                _error.value = "İşlem başarısız"
            }
        }
    }

    fun cancelAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.cancelAppointment(appointment)
                AppointmentReminderWorker.cancel(getApplication(), appointment.id)
            } catch (e: Exception) {
                _error.value = "İptal edilemedi"
            }
        }
    }

    // Grup oluşturma işlemini beklemeli yaparak Fragment senkronizasyonunu sağlıyoruz
    suspend fun createGroupSync(name: String, memberIds: List<Int>) {
        repository.createGroup(name, session.userId, memberIds)
    }

    private fun scheduleReminder(appointment: Appointment) {
        try {
            val appointmentDateTime = LocalDateTime.parse(
                "${appointment.date} ${appointment.startTime}",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            )
            val now = LocalDateTime.now()
            val delayMs = java.time.Duration.between(now, appointmentDateTime).toMillis() - 3_600_000L
            if (delayMs > 0) {
                AppointmentReminderWorker.schedule(getApplication(), appointment.id, appointment.academicianName, appointment.date, appointment.startTime, delayMs)
            }
        } catch (_: Exception) {}
    }

    fun clearError() { _error.value = null }
}