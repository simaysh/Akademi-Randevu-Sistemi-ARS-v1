package com.ankara.bote.randevu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academicians")
data class Academician(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val title: String,
    val email: String = "",
    val password: String = "123456", // Varsayılan şifre
    val isAcademician: Boolean = true
)