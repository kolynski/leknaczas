package com.example.leknaczas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.Lek
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class LekViewModel : ViewModel() {
    private val lekRepository = LekRepository()
    
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
            lekRepository.getLekiFlow().collect {
                _leki.value = it
                _isLoading.value = false
            }
        }
    }
    
    fun dodajLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String) {
        viewModelScope.launch {
            _isLoading.value = true
            lekRepository.addLek(nazwa, czestotliwosc, ilosc, jednostka)
            _isLoading.value = false
        }
    }
    
    fun toggleLekStatus(lek: Lek) {
        viewModelScope.launch {
            // Aktualizujemy status oraz datę wzięcia leku
            val dataWziecia = if (!lek.przyjety) LocalDate.now().toString() else ""
            lekRepository.updateLekStatus(lek, dataWziecia)
        }
    }
    
    fun usunLek(lek: Lek) {
        viewModelScope.launch {
            lekRepository.deleteLek(lek.id)
        }
    }
}