package com.example.glucodialog.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.glucodialog.R // Обязательно укажите ваш R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Напоминание"
        val message = intent.getStringExtra("MESSAGE") ?: "Пора принять лекарство/инсулин"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", System.currentTimeMillis().toInt())
        val timeMinutes = intent.getIntExtra("TIME_MINUTES", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "therapy_reminders",
                "План лечения",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминания о приеме лекарств и инсулина"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "therapy_reminders")
            .setSmallIcon(R.drawable.ic_health)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())

        if (timeMinutes != -1) {
            ReminderScheduler.scheduleReminder(context, notificationId, timeMinutes, title, message)
        }
    }
}