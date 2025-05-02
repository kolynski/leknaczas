package com.example.leknaczas.repository

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun signup(email: String, password: String): Result<FirebaseUser>
    fun logout()
    fun isUserLoggedIn(): Boolean
}
