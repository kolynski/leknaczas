package com.example.leknaczas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.Lek
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LekViewModel : ViewModel() {
    private val lekRepository = LekRepository()
    
    private val _leki = MutableStateFlow<List<Lek>>(emptyList())
    val leki: StateFlow<List<Lek>> = _leki.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
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
    
    fun dodajLek(nazwa: String, dawka: String, czestotliwosc: String) {
        viewModelScope.launch {
            _isLoading.value = true
            lekRepository.addLek(nazwa, dawka, czestotliwosc)
            _isLoading.value = false
        }
    }
    
    fun toggleLekStatus(lek: Lek) {
        viewModelScope.launch {
            lekRepository.updateLekStatus(lek)
        }
    }
}