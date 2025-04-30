package com.example.leknaczas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.model.Lek
import com.example.leknaczas.repository.LekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LekViewModel : ViewModel() {
    private val lekRepository = LekRepository()
    
    private val _leki = MutableStateFlow<List<Lek>>(emptyList())
    val leki: StateFlow<List<Lek>> = _leki
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadLeki()
    }
    
    private fun loadLeki() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                lekRepository.getLekiFlow().collectLatest { lekiList ->
                    _leki.value = lekiList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
    
    fun dodajLek(nazwa: String) {
        if (nazwa.isBlank()) return
        
        viewModelScope.launch {
            try {
                lekRepository.addLek(nazwa)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun toggleLekStatus(lek: Lek) {
        viewModelScope.launch {
            try {
                lekRepository.updateLekStatus(lek)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}