package com.example.leknaczas.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.leknaczas.MainActivity
import com.example.leknaczas.R
import com.example.leknaczas.model.Lek

class NotificationService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "medication_reminders"
        const val NOTIFICATION_ID_PREFIX = 100
        
        fun createNotificationChannel(context: Context) {
            // Create the notification channel - only needed on API 26+ (Android 8.0 and higher)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun showMedicationReminder(lek: Lek) {
        val notificationId = NOTIFICATION_ID_PREFIX + lek.id.hashCode()

        // Create an intent that will open the app when the notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("LEK_ID", lek.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val title = context.getString(R.string.medication_reminder_title)
        val message = context.getString(R.string.medication_reminder_message, lek.nazwa, lek.ilosc, lek.jednostka)
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medicine_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Add a "take medication" action
        val takeMedicationIntent = Intent(context, MedicationActionReceiver::class.java).apply {
            action = MedicationActionReceiver.ACTION_TAKE_MEDICATION
            putExtra("LEK_ID", lek.id)
        }
        
        val takePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1,
            takeMedicationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        builder.addAction(
            R.drawable.ic_check,
            context.getString(R.string.take_medication),
            takePendingIntent
        )

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                // Handle missing notification permission
                e.printStackTrace()
            }
        }
    }
}