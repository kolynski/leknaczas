package com.example.leknaczas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.Lek
import com.example.leknaczas.notification.MedicationScheduler
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    
    fun dodajLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val lekId = lekRepository.addLek(nazwa, czestotliwosc, ilosc, jednostka)
            
            // If the medication was added successfully, schedule a notification
            if (lekId.isNotEmpty()) {
                // Wait a moment for the medication to be available in the repository
                delay(500)
                
                // Reload to get the newly added medication
                lekRepository.getLekiFlow().firstOrNull()?.find { it.id == lekId }?.let { lek ->
                    medicationScheduler.scheduleMedicationReminders(lek)
                }
            }
            
            _isLoading.value = false
        }
    }
    
    fun toggleLekStatus(lek: Lek) {
        viewModelScope.launch {
            // Aktualizujemy status oraz datę wzięcia leku
            val dataWziecia = if (!lek.przyjety) LocalDate.now().toString() else ""
            lekRepository.updateLekStatus(lek, dataWziecia)
            
            // If the medication is marked as taken, cancel today's notifications
            if (!lek.przyjety) {
                medicationScheduler.cancelRemindersForMedication(lek.id)
                // Reschedule for future doses
                delay(500) // Small delay to ensure database update completes
                lekRepository.getLekiFlow().firstOrNull()?.find { it.id == lek.id }?.let { updatedLek ->
                    medicationScheduler.scheduleMedicationReminders(updatedLek)
                }
            }
        }
    }
    
    fun usunLek(lek: Lek) {
        viewModelScope.launch {
            // Cancel any scheduled notifications for this medication
            medicationScheduler.cancelRemindersForMedication(lek.id)
            
            // Delete the medication
            lekRepository.deleteLek(lek.id)
        }
    }
}