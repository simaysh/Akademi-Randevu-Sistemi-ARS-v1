package com.ankara.bote.randevu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ankara.bote.randevu.data.dao.*
import com.ankara.bote.randevu.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Student::class, Academician::class, TimeSlot::class, Appointment::class, Group::class, GroupMember::class],
    version = 7, // Versiyon artırıldı
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun academicianDao(): AcademicianDao
    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bote_randevu.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateData(db: AppDatabase) {
            // Eğer akademisyen yoksa geri getir
            if (db.academicianDao().count() == 0) {
                val academicians = listOf(
                    Academician(id = 1, name = "Mehmet Tekerek", title = "Prof. Dr.", email = "tekerek@ankara.edu.tr", password = "123"),
                    Academician(id = 2, name = "Dilek Doğan", title = "Doç. Dr.", email = "dilek@ankara.edu.tr", password = "123"),
                    Academician(id = 3, name = "H. Tuğba Öztürk", title = "Prof. Dr.", email = "tugba@ankara.edu.tr", password = "123"),
                    Academician(id = 4, name = "Özlem Çakır", title = "Prof. Dr.", email = "ozlem@ankara.edu.tr", password = "123"),
                    Academician(id = 5, name = "Mehmet Kurt", title = "Doç. Dr.", email = "mkurt@ankara.edu.tr", password = "123")
                )
                db.academicianDao().insertAll(academicians)

                // Müsait çalışma saatlerini de ekle
                val today = java.time.LocalDate.now()
                val times = listOf("09:00" to "09:30", "10:00" to "10:30", "11:00" to "11:30", "13:30" to "14:00", "14:30" to "15:00")
                val slots = mutableListOf<TimeSlot>()
                for (acId in 1..5) {
                    for (dayOff in 0..10L) {
                        val date = today.plusDays(dayOff).toString()
                        times.forEach { (s, e) ->
                            slots.add(TimeSlot(academicianId = acId, date = date, startTime = s, endTime = e))
                        }
                    }
                }
                db.timeSlotDao().insertAll(slots)
            }
        }
    }
}
