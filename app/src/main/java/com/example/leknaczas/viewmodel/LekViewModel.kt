package com.example.leknaczas.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.leknaczas.model.Lek

class LekViewModel : ViewModel() {
    private val _leki = mutableStateListOf<Lek>()
    val leki: SnapshotStateList<Lek> = _leki
    private var nextId = 0

    fun dodajLek(nazwa: String) {
        if (nazwa.isNotBlank()) {
            _leki.add(Lek(id = nextId++, nazwa = nazwa))
        }
    }

    fun toggleLekStatus(id: Int) {
        val index = _leki.indexOfFirst { it.id == id }
        if (index != -1) {
            val lek = _leki[index]
            _leki[index] = lek.copy(wziety = !lek.wziety)
        }
    }
}