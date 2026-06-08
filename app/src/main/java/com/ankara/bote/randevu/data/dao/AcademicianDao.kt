package com.ankara.bote.randevu.data.dao

import androidx.room.*
import com.ankara.bote.randevu.data.model.Academician
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicianDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Academician>)

    @Query("SELECT * FROM academicians ORDER BY name ASC")
    fun getAllFlow(): Flow<List<Academician>>

    @Query("SELECT * FROM academicians WHERE id = :id")
    suspend fun findById(id: Int): Academician?

    @Query("SELECT * FROM academicians WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): Academician?

    @Query("SELECT COUNT(*) FROM academicians")
    suspend fun count(): Int
}
