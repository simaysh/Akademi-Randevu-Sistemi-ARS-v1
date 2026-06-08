package com.ankara.bote.randevu.data.dao

import androidx.room.*
import com.ankara.bote.randevu.data.model.Student

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student): Long

    @Query("SELECT * FROM students WHERE studentNumber = :number LIMIT 1")
    suspend fun findByStudentNumber(number: String): Student?

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): Student?

    @Query("SELECT * FROM students WHERE studentNumber != :excludeNumber")
    suspend fun getAllExcept(excludeNumber: String): List<Student>
}
