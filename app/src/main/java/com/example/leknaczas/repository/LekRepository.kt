package com.example.leknaczas.viewmodely

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.LekirebaseFirestore
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFloww
import kotlinx.coroutines.launchawait
import java.time.LocalDate
class LekRepository : ILekRepository {
class LekViewModel : ViewModel() {
    private val lekRepository = LekRepository()
    } catch (e: Exception) {
    private val _leki = MutableStateFlow<List<Lek>>(emptyList()))
    val leki: StateFlow<List<Lek>> = _leki.asStateFlow()
    }
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
        FirebaseAuth.getInstance()
    init {h (e: Exception) {
        loadLeki()Repository", "Error initializing FirebaseAuth", e)
    }   null
    }
    // Dodajemy publiczną metodę do ponownego załadowania leków
    fun refreshLeki() {iCollection
        loadLeki()th?.currentUser?.uid?.let { userId ->
    }       firestore?.collection("users")?.document(userId)?.collection("leki")
        }
    private fun loadLeki() {
        viewModelScope.launch { Flow<List<Lek>> = callbackFlow {
            _isLoading.value = truell || firestore == null) {
            lekRepository.getLekiFlow().collect {
                _leki.value = iting to clean up */ }
                _isLoading.value = false
            }
        }
    }   val userId = auth?.currentUser?.uid
        if (userId == null) {
    fun dodajLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String) {
        viewModelScope.launch {hing to clean up */ }
            _isLoading.value = true
            lekRepository.addLek(nazwa, czestotliwosc, ilosc, jednostka)
            _isLoading.value = false
        }al collection = firestore?.collection("users")?.document(userId)?.collection("leki")
    }   val listenerRegistration = collection?.addSnapshotListener { snapshot, error ->
            if (error != null) {
    fun toggleLekStatus(lek: Lek) {y", "Error fetching leki", error)
        viewModelScope.launch {st())
            // Aktualizujemy status oraz datę wzięcia leku
            val dataWziecia = if (!lek.przyjety) LocalDate.now().toString() else ""
            lekRepository.updateLekStatus(lek, dataWziecia)
        }   val leki = snapshot?.documents?.mapNotNull { doc ->
    }           try {
                       doc.toObject(Lek::class.java)?.copy(id = doc.id)






}    }        }            lekRepository.deleteLek(lek.id)        viewModelScope.launch {    fun usunLek(lek: Lek) {                } catch (e: Exception) {
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

    override suspend fun addLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String): String {
        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {
            return ""
        }

        val lek = Lek(
            id = "", 
            nazwa = nazwa,
            dawka = "", // Empty since we don't need it anymore
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























}    }        }            Log.e("LekRepository", "Error deleting lek", e)        } catch (e: Exception) {            userLekiCollection?.document(lekId)?.delete()?.await()        try {        }            return        if (firestore == null || auth?.currentUser == null || userLekiCollection == null) {    override suspend fun deleteLek(lekId: String) {    }        }            Log.e("LekRepository", "Error updating lek status", e)        } catch (e: Exception) {            userLekiCollection?.document(lek.id)?.update(updates)?.await()                        }                updates["dataWziecia"] = dataWziecia            if (dataWziecia.isNotEmpty()) {
            
            )
                "przyjety" to !lek.przyjety
        try {
            val updates = hashMapOf<String, Any>(