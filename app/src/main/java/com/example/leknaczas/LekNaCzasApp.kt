package com.example.leknaczas

import android.app.Application
import com.example.leknaczas.notification.NotificationService
import com.example.leknaczas.repository.AuthRepository
import com.example.leknaczas.repository.IAuthRepository
import com.example.leknaczas.repository.ILekRepository
import com.example.leknaczas.repository.LekRepository
import com.google.firebase.FirebaseApp

class LekNaCzasApp : Application() {
    companion object {
        lateinit var authRepository: IAuthRepository
        lateinit var lekRepository: ILekRepository
    }

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            authRepository = AuthRepository()
            lekRepository = LekRepository()
            
            // Create the notification channel
            NotificationService.createNotificationChannel(this)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Błąd inicjalizacji Firebase: ${e.message}")
        }
    }
}