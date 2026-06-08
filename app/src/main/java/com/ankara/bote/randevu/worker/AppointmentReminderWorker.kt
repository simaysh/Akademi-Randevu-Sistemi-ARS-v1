package com.ankara.bote.randevu.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.ankara.bote.randevu.R
import java.util.concurrent.TimeUnit

class AppointmentReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val academicianName = inputData.getString("academicianName") ?: return Result.failure()
        val date = inputData.getString("date") ?: return Result.failure()
        val time = inputData.getString("time") ?: return Result.failure()

        showNotification(
            title = "⏰ Randevu Hatırlatması",
            body = "$academicianName ile $date tarihinde saat $time'de randevunuz var."
        )
        return Result.success()
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "bote_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Randevu Hatırlatmaları",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        fun schedule(
            context: Context,
            appointmentId: Int,
            academicianName: String,
            date: String,
            time: String,
            delayMillis: Long
        ) {
            val data = workDataOf(
                "academicianName" to academicianName,
                "date" to date,
                "time" to time
            )
            val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addTag("appointment_$appointmentId")
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }

        fun cancel(context: Context, appointmentId: Int) {
            WorkManager.getInstance(context)
                .cancelAllWorkByTag("appointment_$appointmentId")
        }
    }
}