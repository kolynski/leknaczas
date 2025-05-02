package com.example.leknaczas

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.leknaczas.model.Lek

class LekViewModel : ViewModel() {
    private var _lekList = mutableStateOf<List<Lek>>(emptyList())
    val lekList: List<Lek> get() = _lekList.value
    
    fun dodajLek(nazwa: String, dawka: String, czestotliwosc: String) {
        val nowyLek = Lek(
            id = java.util.UUID.randomUUID().toString(),
            nazwa = nazwa,
            dawka = dawka,
            czestotliwosc = czestotliwosc
        )
        _lekList.value = _lekList.value + nowyLek
    }
}