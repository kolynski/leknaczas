package com.example.leknaczas

import android.app.Application
import com.google.firebase.FirebaseApp

class LekNaCzasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}