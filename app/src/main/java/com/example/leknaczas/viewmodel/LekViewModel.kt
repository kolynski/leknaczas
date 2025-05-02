package com.example.leknaczas

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.leknaczas.model.Lek

class LekViewModel : ViewModel() {
    private val _lekList = mutableStateListOf<Lek>()
    val lekList: List<Lek> = _lekList

    fun dodajLek(nazwa: String, dawka: String, czestotliwosc: String) {
        val nowyLek = Lek(
            id = java.util.UUID.randomUUID().toString(),
            nazwa = nazwa,
            dawka = dawka, 
            czestotliwosc = czestotliwosc,
            przyjety = false
        )
        _lekList.add(nowyLek)
    }
    
    fun toggleLekStatus(lek: Lek) {
        val index = _lekList.indexOfFirst { it.id == lek.id }
        if (index >= 0) {
            _lekList[index] = _lekList[index].copy(przyjety = !_lekList[index].przyjety)
        }
    }
}
