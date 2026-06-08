package com.ankara.bote.randevu.ui.auth

import android.app.Application
import androidx.lifecycle.*
import com.ankara.bote.randevu.data.AppDatabase
import com.ankara.bote.randevu.data.SessionManager
import com.ankara.bote.randevu.data.repository.AppRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(AppDatabase.getInstance(application))
    private val session = SessionManager(application)

    private val _state = MutableLiveData<AuthState>(AuthState.Idle)
    val state: LiveData<AuthState> = _state

    val isAlreadyLoggedIn: Boolean get() = session.isLoggedIn
    val isAcademician: Boolean get() = session.isAcademician

    fun loginStudent(number: String, firstName: String, lastName: String) {
        if (number.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _state.value = AuthState.Error("Lütfen tüm alanları doldurun")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val student = repository.loginOrRegister(number, firstName, lastName)
                session.userId = student.id
                session.userRole = "STUDENT"
                session.userNumber = student.studentNumber
                session.userName = student.fullName
                _state.value = AuthState.Success
            } catch (e: Exception) {
                _state.value = AuthState.Error("Giriş hatası: ${e.message}")
            }
        }
    }

    fun loginAcademician(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = AuthState.Error("E-posta ve şifre girin")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val ac = repository.getAcademicianByEmail(email)
                if (ac != null && ac.password == pass) {
                    session.userId = ac.id
                    session.userRole = "ACADEMICIAN"
                    session.userName = ac.name
                    session.userNumber = ac.email
                    _state.value = AuthState.Success
                } else {
                    _state.value = AuthState.Error("Hatalı e-posta veya şifre")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Sistem hatası")
            }
        }
    }
}
