package com.example.leknaczas.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository : IAuthRepository {
    private val firebaseAuth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.e("AuthRepository", "Error initializing FirebaseAuth", e)
        null
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth?.currentUser

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        if (firebaseAuth == null) {
            return Result.failure(Exception("Firebase Auth is not initialized"))
        }

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Login failed: user is null"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            Result.failure(e)
        }
    }

    override suspend fun signup(email: String, password: String): Result<FirebaseUser> {
        if (firebaseAuth == null) {
            return Result.failure(Exception("Firebase Auth is not initialized"))
        }

        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign up failed: user is null"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Signup error", e)
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuth?.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth?.currentUser != null
    }
}