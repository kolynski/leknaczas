package com.example.leknaczas.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.leknaczas.model.Lek
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MedicationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule notifications for a medication
     */
    fun scheduleMedicationReminders(lek: Lek) {
        // Cancel existing reminders for this medication
        cancelRemindersForMedication(lek.id)
        
        // Schedule fixed reminders at 10:00 and 20:00
        scheduleFixedTimeReminders(lek)
    }
    
    /**
     * Cancel all reminders for a specific medication
     */
    fun cancelRemindersForMedication(lekId: String) {
        workManager.cancelAllWorkByTag("medication_reminder_$lekId")
    }
    
    /**
     * Schedule reminders at fixed times (10:00 and 20:00)
     */
    private fun scheduleFixedTimeReminders(lek: Lek) {
        // Morning reminder at 10:00
        scheduleReminder(lek, 10, 0, "morning")
        
        // Evening reminder at 20:00
        scheduleReminder(lek, 20, 0, "evening")
    }
    
    /**
     * Schedule a reminder for a specific time
     */
    private fun scheduleReminder(lek: Lek, hour: Int, minute: Int, timePeriod: String) {
        val initialDelay = calculateInitialDelay(hour, minute)
        
        val data = workDataOf(
            "LEK_ID" to lek.id,
            "CHECK_IF_TAKEN" to true
        )
        
        // Create a unique work name for this medication and time
        val workName = "medication_reminder_${lek.id}_${timePeriod}"
        
        // Set up daily repeat
        val dailyWorkRequest = PeriodicWorkRequestBuilder<MedicationReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("medication_reminder_${lek.id}")
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
        
        Log.d("MedicationScheduler", "Scheduled reminder for ${lek.nazwa} at $hour:$minute")
    }
    
    /**
     * Calculate the initial delay in milliseconds from now until the next specified time
     */
    private fun calculateInitialDelay(hour: Int, minute: Int): Duration {
        val now = ZonedDateTime.now()
        var nextTime = ZonedDateTime.of(
            now.toLocalDate(),
            LocalTime.of(hour, minute),
            now.zone
        )
        
        // If the time is already past for today, schedule for tomorrow
        if (now.isAfter(nextTime)) {
            nextTime = nextTime.plusDays(1)
        }
        
        return Duration.between(now, nextTime)
    }
}