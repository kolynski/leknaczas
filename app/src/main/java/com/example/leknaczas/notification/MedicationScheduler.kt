package com.example.leknaczas.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.leknaczas.model.Lek
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MedicationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule notifications for a medication
     */
    fun scheduleMedicationReminders(lek: Lek) {
        // Cancel existing reminders for this medication
        cancelRemindersForMedication(lek.id)
        
        // Parse frequency and set up appropriate reminder times
        when (lek.czestotliwosc) {
            "1 x dziennie" -> scheduleDaily(lek, listOf(8)) // 8:00 AM
            "2 x dziennie" -> scheduleDaily(lek, listOf(8, 20)) // 8:00 AM and 8:00 PM
            "3 x dziennie" -> scheduleDaily(lek, listOf(8, 14, 20)) // 8:00 AM, 2:00 PM, and 8:00 PM
            "co drugi dzień" -> scheduleEveryOtherDay(lek, 8) // 8:00 AM every other day
            "raz w tygodniu" -> scheduleWeekly(lek, DayOfWeek.MONDAY, 8) // Monday at 8:00 AM
            else -> scheduleDaily(lek, listOf(8)) // Default to once daily at 8:00 AM
        }
    }
    
    /**
     * Cancel all reminders for a specific medication
     */
    fun cancelRemindersForMedication(lekId: String) {
        workManager.cancelAllWorkByTag("medication_reminder_$lekId")
    }
    
    /**
     * Schedule reminders for a medication that should be taken daily at specific hours
     */
    private fun scheduleDaily(lek: Lek, hoursOfDay: List<Int>) {
        hoursOfDay.forEach { hour ->
            scheduleReminder(lek, hour, 0, Duration.ofDays(1))
        }
    }
    
    /**
     * Schedule reminders for a medication that should be taken every other day
     */
    private fun scheduleEveryOtherDay(lek: Lek, hourOfDay: Int) {
        // Check if today is a day to take the medication
        val today = LocalDate.now()
        val startOfYear = LocalDate.of(today.year, 1, 1)
        val daysSinceStartOfYear = ChronoUnit.DAYS.between(startOfYear, today)
        
        // Calculate initial delay - if today is an "off" day, start tomorrow
        val initialDelayDays = if (daysSinceStartOfYear % 2 == 0L) 0L else 1L
        val initialDelay = calculateInitialDelay(hourOfDay, 0, initialDelayDays)
        
        schedulePeriodicReminder(lek, initialDelay, Duration.ofDays(2))
    }
    
    /**
     * Schedule reminders for a medication that should be taken weekly on a specific day
     */
    private fun scheduleWeekly(lek: Lek, dayOfWeek: DayOfWeek, hourOfDay: Int) {
        val today = LocalDate.now()
        val daysUntilNext = (7 + dayOfWeek.value - today.dayOfWeek.value) % 7
        val initialDelayDays = if (daysUntilNext == 0 && LocalTime.now().isAfter(LocalTime.of(hourOfDay, 0))) 7L else daysUntilNext.toLong()
        
        val initialDelay = calculateInitialDelay(hourOfDay, 0, initialDelayDays)
        
        schedulePeriodicReminder(lek, initialDelay, Duration.ofDays(7))
    }
    
    /**
     * Calculate the initial delay in milliseconds from now until the next specified time
     */
    private fun calculateInitialDelay(hour: Int, minute: Int, additionalDays: Long = 0): Duration {
        val now = ZonedDateTime.now()
        var nextTime = ZonedDateTime.of(
            now.toLocalDate().plusDays(additionalDays),
            LocalTime.of(hour, minute),
            now.zone
        )
        
        // If the time is already past for today, schedule for tomorrow
        if (additionalDays == 0L && now.isAfter(nextTime)) {
            nextTime = nextTime.plusDays(1)
        }
        
        return Duration.between(now, nextTime)
    }
    
    /**
     * Schedule a one-time reminder for a medication
     */
    private fun scheduleReminder(lek: Lek, hour: Int, minute: Int, repeatInterval: Duration) {
        val initialDelay = calculateInitialDelay(hour, minute)
        
        val data = workDataOf("LEK_ID" to lek.id)
        
        val reminderRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("medication_reminder_${lek.id}")
            .build()
            
        workManager.enqueueUniqueWork(
            "medication_reminder_${lek.id}_${hour}_${minute}",
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
        
        Log.d("MedicationScheduler", "Scheduled reminder for ${lek.nazwa} at $hour:$minute in ${initialDelay.toMinutes()} minutes")
    }
    
    /**
     * Schedule a periodic reminder for a medication
     */
    private fun schedulePeriodicReminder(lek: Lek, initialDelay: Duration, repeatInterval: Duration) {
        val data = workDataOf("LEK_ID" to lek.id)
        
        val reminderRequest = PeriodicWorkRequestBuilder<MedicationReminderWorker>(
            repeatInterval.toHours(), TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("medication_reminder_${lek.id}")
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            "periodic_reminder_${lek.id}",
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
        
        Log.d("MedicationScheduler", "Scheduled periodic reminder for ${lek.nazwa} starting in ${initialDelay.toMinutes()} minutes, repeating every ${repeatInterval.toHours()} hours")
    }
}