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
        get() = auth?.currentUser?.uid?.let { userId ->
            firestore?.collection("users")?.document(userId)?.collection("leki")
        }

    override fun getLekiFlow(): Flow<List<Lek>> = callbackFlow {
        if (auth?.currentUser == null || firestore == null) {
            trySend(emptyList())
            awaitClose { /* nothing to clean up */ }
            return@callbackFlow
        }

        val userId = auth?.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose { /* nothing to clean up */ }
            return@callbackFlow
        }

        val collection = firestore?.collection("users")?.document(userId)?.collection("leki")
        val listenerRegistration = collection?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("LekRepository", "Error fetching leki", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            val leki = snapshot?.documents?.mapNotNull { doc ->
                try {
                    doc.toObject(Lek::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("LekRepository", "Error converting document to Lek", e)
                    null
                }
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

    override suspend fun addLek(nazwa: String, dawka: String, czestotliwosc: String): String {
        return addLek(nazwa, dawka, czestotliwosc, "", "")
    }

    override suspend fun addLek(nazwa: String, dawka: String, czestotliwosc: String, ilosc: String, jednostka: String): String {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return ""
        }

        val lek = Lek(
            id = "", 
            nazwa = nazwa, 
            dawka = dawka, 
            czestotliwosc = czestotliwosc, 
            ilosc = ilosc,
            jednostka = jednostka,
            przyjety = false
        )
        
        return try {
            userLekiCollection?.add(lek)?.await()?.id ?: ""
        } catch (e: Exception) {
            Log.e("LekRepository", "Error adding lek", e)
            ""
        }
    }

    override suspend fun updateLekStatus(lek: Lek) {
        updateLekStatus(lek, "")
    }
    
    override suspend fun updateLekStatus(lek: Lek, dataWziecia: String) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            val updates = hashMapOf<String, Any>(
                "przyjety" to !lek.przyjety
            )
            
            if (dataWziecia.isNotEmpty()) {
                updates["dataWziecia"] = dataWziecia
            }
            
            userLekiCollection?.document(lek.id)?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error updating lek status", e)
        }
    }
}
