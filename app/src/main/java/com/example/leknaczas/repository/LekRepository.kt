package com.example.leknaczas.repository

import android.util.Log
import com.example.leknaczas.model.Lek
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

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

        val lek = Lek(id = "", nazwa = nazwa, _przyjety = false)
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
            _przyjety = false
        )
        
        return try {
            userLekiCollection?.add(lek)?.await()?.id ?: ""
        } catch (e: Exception) {
            Log.e("LekRepository", "Error adding lek", e)
            ""
        }
    }

    override suspend fun addLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String): String {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return ""
        }

        val iloscNaDawke = try {
            when (ilosc) {
                "1/2" -> 0.5f
                "1/4" -> 0.25f
                else -> ilosc.toFloatOrNull() ?: 1.0f
            }
        } catch (e: Exception) {
            1.0f
        }

        val lek = Lek(
            id = "", 
            nazwa = nazwa,
            czestotliwosc = czestotliwosc, 
            ilosc = ilosc,
            jednostka = jednostka,
            _przyjety = false,
            dostepneIlosc = 0,
            iloscNaDawke = iloscNaDawke
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
            val nowyStatus = !lek.przyjety
            val aktualnePrzyjecia = lek.przyjecia.toMutableMap()
            
            if (dataWziecia.isNotEmpty() && nowyStatus) {
                // Jeśli oznaczamy jako wzięty z konkretną datą
                aktualnePrzyjecia[dataWziecia] = true
            } else if (dataWziecia.isEmpty()) {
                // Jeśli przełączamy status dzisiaj
                val dzisiaj = java.time.LocalDate.now().toString()
                aktualnePrzyjecia[dzisiaj] = nowyStatus
            }
            
            val updates = hashMapOf<String, Any>(
                "przyjecia" to aktualnePrzyjecia,
                "_przyjety" to nowyStatus,
                "_dataWziecia" to (if (nowyStatus && dataWziecia.isNotEmpty()) dataWziecia else "")
            )
            
            userLekiCollection?.document(lek.id)?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error updating lek status", e)
        }
    }

    override suspend fun deleteLek(lekId: String) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            userLekiCollection?.document(lekId)?.delete()?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error deleting lek", e)
        }
    }

    suspend fun markLekAsTaken(lekId: String, dataWziecia: String) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            val docRef = userLekiCollection?.document(lekId)
            val snapshot = docRef?.get()?.await()
            val lek = snapshot?.toObject(Lek::class.java)
            val aktualnePrzyjecia = lek?.przyjecia?.toMutableMap() ?: mutableMapOf()
            
            // Oznacz jako wzięty dla wskazanej daty
            aktualnePrzyjecia[dataWziecia] = true
            
            val updates = hashMapOf<String, Any>(
                "przyjecia" to aktualnePrzyjecia
            )
            
            // Aktualizuj pola kompatybilności wstecznej tylko dla dzisiejszej daty
            val today = LocalDate.now().toString()
            if (dataWziecia == today) {
                updates["_przyjety"] = true
                updates["_dataWziecia"] = dataWziecia
            }
            
            Log.d("LekRepository", "Oznaczenie leku $lekId jako wzięty dla daty: $dataWziecia")
            docRef?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error marking lek as taken", e)
        }
    }

    suspend fun markLekAsNotTaken(lekId: String, dataWziecia: String) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            val docRef = userLekiCollection?.document(lekId)
            val snapshot = docRef?.get()?.await()
            val lek = snapshot?.toObject(Lek::class.java)
            val aktualnePrzyjecia = lek?.przyjecia?.toMutableMap() ?: mutableMapOf()
            
            if (dataWziecia.isNotEmpty()) {
                // Oznacz jako niewzięty dla wskazanej daty
                aktualnePrzyjecia[dataWziecia] = false
            }
            
            val updates = hashMapOf<String, Any>("przyjecia" to aktualnePrzyjecia)
            
            // Aktualizuj pola kompatybilności tylko dla dzisiaj
            val today = java.time.LocalDate.now().toString()
            if (dataWziecia == today || dataWziecia.isEmpty()) {
                updates["_przyjety"] = false
                updates["_dataWziecia"] = ""
            }
            
            docRef?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error marking lek as not taken", e)
        }
    }

    suspend fun updateLekSupply(lekId: String, nowaIlosc: Int) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            val updates = hashMapOf<String, Any>(
                "dostepneIlosc" to nowaIlosc
            )
            
            userLekiCollection?.document(lekId)?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error updating medication supply", e)
        }    }

    override suspend fun updateLekSupplyAndStatus(lekId: String, nowaIlosc: Int, dataWziecia: String) {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return
        }

        try {
            val updates = hashMapOf<String, Any>(
                "dostepneIlosc" to nowaIlosc
            )
            
            if (dataWziecia.isNotEmpty()) {
                updates["_przyjety"] = true
                updates["_dataWziecia"] = dataWziecia
                updates["przyjecia.$dataWziecia"] = true
            } else {
                updates["_przyjety"] = false
                updates["_dataWziecia"] = ""
            }
            
                    Log.d("LekRepository", "Aktualizacja leku $lekId: ilość=$nowaIlosc, data=$dataWziecia")
            userLekiCollection?.document(lekId)?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e("LekRepository", "Error updating medication supply and status", e)
        }
    }

    override suspend fun addLek(
        nazwa: String, 
        czestotliwosc: String, 
        ilosc: String, 
        jednostka: String,
        dataWaznosci: String,
        producent: String
    ): String {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return ""
        }

        val iloscNaDawke = try {
            when (ilosc) {
                "1/2" -> 0.5f
                "1/4" -> 0.25f
                else -> ilosc.toFloatOrNull() ?: 1.0f
            }
        } catch (e: Exception) {
            1.0f
        }

        val lek = Lek(
            id = "", 
            nazwa = nazwa,
            czestotliwosc = czestotliwosc, 
            ilosc = ilosc,
            jednostka = jednostka,
            _przyjety = false,
            dostepneIlosc = 0,
            iloscNaDawke = iloscNaDawke,
            dataWaznosci = dataWaznosci,
            producent = producent
        )
        
        return try {
            userLekiCollection?.add(lek)?.await()?.id ?: ""
        } catch (e: Exception) {
            Log.e("LekRepository", "Error adding lek", e)
            ""
        }
    }    override suspend fun addSupply(lekId: String, dodanaIlosc: Int) {
        try {
            if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
                return
            }

            userLekiCollection?.document(lekId)?.get()?.await()?.let { document ->
                val aktualnaIlosc = document.getLong("dostepneIlosc")?.toInt() ?: 0
                val nowaIlosc = aktualnaIlosc + dodanaIlosc
                
                userLekiCollection?.document(lekId)?.update("dostepneIlosc", nowaIlosc)?.await()
                Log.d("LekRepository", "Dodano zapas $dodanaIlosc, nowa ilość: $nowaIlosc")
            }
        } catch (e: Exception) {
            Log.e("LekRepository", "Error adding supply", e)
        }
    }
}
