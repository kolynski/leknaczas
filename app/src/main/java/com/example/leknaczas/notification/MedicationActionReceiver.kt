package com.example.leknaczas.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MedicationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TAKE_MEDICATION = "com.example.leknaczas.ACTION_TAKE_MEDICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TAKE_MEDICATION) {
            val lekId = intent.getStringExtra("LEK_ID") ?: return
            
            // Update medication status as taken
            CoroutineScope(Dispatchers.IO).launch {
                val repository = LekRepository()
                repository.markLekAsTaken(lekId, LocalDate.now().toString())
                
                // Cancel the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.cancel(NotificationService.NOTIFICATION_ID_PREFIX + lekId.hashCode())
            }
        }
    }
}