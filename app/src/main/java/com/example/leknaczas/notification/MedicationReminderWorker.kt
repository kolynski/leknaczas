package com.example.leknaczas.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.leknaczas.model.Lek
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "MedicationReminderWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val lekId = inputData.getString("LEK_ID")
            if (lekId == null) {
                Log.e(TAG, "No medication ID provided")
                return@withContext Result.failure()
            }

            // Get the repository
            val repository = LekRepository()
            
            // Get the medication list
            val leki = repository.getLekiFlow().firstOrNull() ?: emptyList()
            
            // Find the specific medication
            val lek = leki.find { it.id == lekId }
            if (lek == null) {
                Log.e(TAG, "Medication not found: $lekId")
                return@withContext Result.failure()
            }
            
            // Current date as string
            val today = LocalDate.now().toString()
            
            // Check if the medication is already taken today
            val takenToday = lek.przyjecia[today] == true
            
            // Only send notification if medication has not been taken today
            if (!takenToday) {
                // Show notification
                val notificationService = NotificationService(applicationContext)
                notificationService.showMedicationReminder(lek)
                Log.d(TAG, "Sending notification for ${lek.nazwa} - not taken today")
            } else {
                Log.d(TAG, "Medication ${lek.nazwa} already taken today - skipping notification")
            }
            
            return@withContext Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in medication reminder worker", e)
            return@withContext Result.failure()
        }
    }
}