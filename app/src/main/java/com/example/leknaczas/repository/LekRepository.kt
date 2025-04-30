package com.example.leknaczas.repository

import com.example.leknaczas.model.Lek
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LekRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val userLekiCollection
        get() = firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("leki")
    
    fun getLekiFlow(): Flow<List<Lek>> = callbackFlow {
        if (auth.currentUser == null) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val listenerRegistration = userLekiCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            val leki = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Lek::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            
            trySend(leki)
        }
        
        awaitClose { listenerRegistration.remove() }
    }
    
    suspend fun addLek(nazwa: String): String {
        val lek = Lek(id = "", nazwa = nazwa, wziety = false)
        return userLekiCollection.add(lek).await().id
    }
    
    suspend fun updateLekStatus(lek: Lek) {
        userLekiCollection.document(lek.id).update("wziety", !lek.wziety).await()
    }
}