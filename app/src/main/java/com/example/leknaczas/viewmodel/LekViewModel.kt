package com.example.leknaczas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.Lek
import com.example.leknaczas.notification.MedicationScheduler
import com.example.leknaczas.notification.NotificationService
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.util.Log

class LekViewModel(application: Application) : AndroidViewModel(application) {
    private val lekRepository = LekRepository()
    private val medicationScheduler = MedicationScheduler(application)
    
    private val _leki = MutableStateFlow<List<Lek>>(emptyList())
    val leki: StateFlow<List<Lek>> = _leki.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadLeki()
    }
    
    // Dodajemy publiczną metodę do ponownego załadowania leków
    fun refreshLeki() {
        loadLeki()
    }
    
    private fun loadLeki() {
        viewModelScope.launch {
            _isLoading.value = true
            lekRepository.getLekiFlow().collect { lekiList -> 
                _leki.value = lekiList
                
                // Schedule notifications for all medications
                lekiList.forEach { lek -> 
                    medicationScheduler.scheduleMedicationReminders(lek)
                }
                
                _isLoading.value = false
            }
        }
    }
    
    fun dodajLek(
        nazwa: String, 
        czestotliwosc: String, 
        ilosc: String, 
        jednostka: String,
        dataWaznosci: String = "",
        producent: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("LekViewModel", "Dodaję lek: $nazwa, $czestotliwosc, $ilosc, $jednostka, ważność: $dataWaznosci, producent: $producent")
            val lekId = lekRepository.addLek(nazwa, czestotliwosc, ilosc, jednostka, dataWaznosci, producent)
            Log.d("LekViewModel", "Utworzono lek z ID: $lekId")
            
            // If the medication was added successfully, schedule a notification
            if (lekId.isNotEmpty()) {
                // Wait a moment for the medication to be available in the repository
                delay(500)
                
                // Reload to get the newly added medication
                val newMedication = lekRepository.getLekiFlow().firstOrNull()?.find { it.id == lekId }
                if (newMedication != null) {
                    medicationScheduler.scheduleMedicationReminders(newMedication)
                    Log.d("LekViewModel", "Zaplanowano powiadomienia dla nowego leku")
                }
            }
            
            // Odświeżamy listę leków, aby zobaczyć nowo dodany lek
            refreshLeki()
            _isLoading.value = false
        }
    }
    
    fun toggleLekStatus(lek: Lek) {
        viewModelScope.launch {
            val nowyStatus = !lek.przyjety
            val dataWziecia = if (nowyStatus) LocalDate.now().toString() else ""
            
            Log.d("LekViewModel", "Zmiana statusu leku ${lek.nazwa} na $nowyStatus, data: $dataWziecia")
            
            // Jeśli lek jest oznaczany jako wzięty, zmniejsz dostępną ilość
            if (nowyStatus && lek.dostepneIlosc > 0) {
                val iloscNaDawke = try {
                    when (lek.ilosc) {
                        "1/2" -> 0.5f
                        "1/4" -> 0.25f
                        else -> lek.ilosc.toFloatOrNull() ?: 1.0f
                    }
                } catch (e: Exception) {
                    1.0f
                }
                
                Log.d("LekViewModel", "Dawka: $iloscNaDawke, dostępna ilość przed: ${lek.dostepneIlosc}")
                
                // Zmniejsz dostępną ilość o dawkę
                val nowaDostepnaIlosc = (lek.dostepneIlosc - iloscNaDawke).coerceAtLeast(0f).toInt()
                Log.d("LekViewModel", "Nowa dostępna ilość: $nowaDostepnaIlosc")
                
                lekRepository.updateLekSupplyAndStatus(lek.id, nowaDostepnaIlosc, dataWziecia)
                
                // Jeśli zapas się kończy (mniej niż 5 sztuk), pokaż powiadomienie
                if (nowaDostepnaIlosc <= 5 && nowaDostepnaIlosc > 0) {
                    val context = getApplication<Application>().applicationContext
                    val notificationService = NotificationService(context)
                    notificationService.showLowSupplyNotification(lek, nowaDostepnaIlosc)
                    Log.d("LekViewModel", "Wysłano powiadomienie o niskim stanie leku")
                }
            } else {
                // Po prostu zaktualizuj status, bez zmiany ilości
                lekRepository.updateLekStatus(lek, dataWziecia)
            }
            
            // Odświeżamy listę leków, aby zobaczyć zmiany
            delay(500) // Krótkie opóźnienie, aby dać czas na zaktualizowanie bazy danych
            refreshLeki()
        }
    }
    
    fun usunLek(lek: Lek) {
        viewModelScope.launch {
            // Cancel any scheduled notifications for this medication
            medicationScheduler.cancelRemindersForMedication(lek.id)
            
            // Delete the medication
            lekRepository.deleteLek(lek.id)
            
            // Odświeżamy listę leków po usunięciu
            delay(500)
            refreshLeki()
        }
    }

    fun markAsTakenOnDate(lek: Lek, date: String) {
        viewModelScope.launch {
            Log.d("LekViewModel", "Oznaczanie leku ${lek.nazwa} jako wzięty na datę: $date")
            
            // Jeśli oznaczamy lek jako wzięty na dzisiaj, zmniejszamy też ilość
            if (date == LocalDate.now().toString() && lek.dostepneIlosc > 0) {
                val iloscNaDawke = try {
                    when (lek.ilosc) {
                        "1/2" -> 0.5f
                        "1/4" -> 0.25f
                        else -> lek.ilosc.toFloatOrNull() ?: 1.0f
                    }
                } catch (e: Exception) {
                    1.0f
                }
                
                val nowaDostepnaIlosc = (lek.dostepneIlosc - iloscNaDawke).coerceAtLeast(0f).toInt()
                lekRepository.updateLekSupplyAndStatus(lek.id, nowaDostepnaIlosc, date)
            } else {
                lekRepository.markLekAsTaken(lek.id, date)
            }
            
            // Odświeżamy listę leków po zmianie
            delay(500)
            refreshLeki()
        }
    }

    fun markAsNotTaken(lek: Lek, date: String) {
        viewModelScope.launch {
            Log.d("LekViewModel", "Oznaczanie leku ${lek.nazwa} jako NIE wzięty na datę: $date")
            lekRepository.markLekAsNotTaken(lek.id, date)
            
            // Odświeżamy listę leków po zmianie
            delay(500)
            refreshLeki()
        }
    }

    fun dodajZapas(lek: Lek, iloscDoRozenia: Int) {
        viewModelScope.launch {
            Log.d("LekViewModel", "Dodawanie zapasu dla ${lek.nazwa}: +$iloscDoRozenia, było: ${lek.dostepneIlosc}")
            lekRepository.updateLekSupply(lek.id, lek.dostepneIlosc + iloscDoRozenia)
            
            // Odświeżamy listę leków po dodaniu zapasu
            delay(500)
            refreshLeki()
        }
    }
}