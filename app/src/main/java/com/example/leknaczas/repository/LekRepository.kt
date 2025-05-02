package com.example.leknaczas.repository

import android.util.Log
import com.example.leknaczas.model.Lek
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LekRepository : ILekRepository {
    private val firestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.e("LekRepository", "Error initializing Firestore", e)
        null
    }

    private val auth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.e("LekRepository", "Error initializing FirebaseAuth", e)
        null
    }

    private val userLekiCollection
        get() = firestore?.collection("users")
            ?.document(auth?.currentUser?.uid ?: "")
            ?.collection("leki")

    override fun getLekiFlow(): Flow<List<Lek>> = callbackFlow {
        if (auth?.currentUser == null || firestore == null || userLekiCollection == null) {
            trySend(emptyList())
            awaitClose { /* nothing to clean up */ }
            return@callbackFlow
        }

        val listenerRegistration = userLekiCollection?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("LekRepository", "Error fetching leki", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            val leki = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Lek::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(leki)
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    override suspend fun addLek(nazwa: String): String {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return ""
        }

        val lek = Lek(id = "", nazwa = nazwa, przyjety = false)
        return try {
            userLekiCollection?.add(lek)?.await()?.id ?: ""
        } catch (e: Exception) {
            Log.e("LekRepository", "Error adding lek", e)
            ""
        }
    }

    override suspend fun updateLekStatus(lek: Lek) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            userLekiCollection?.document(lek.id)?.update("przyjety", !lek.przyjety)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error updating lek status", e)
        }
    }
}
