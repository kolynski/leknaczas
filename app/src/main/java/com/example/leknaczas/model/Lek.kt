package com.example.leknaczas.model

data class Lek(
    val id: String = "",
    val nazwa: String = "",
    val dawka: String = "",
    val czestotliwosc: String = "",
    val ilosc: String = "",
    val jednostka: String = "",
    val przyjecia: Map<String, Boolean> = emptyMap(),
    // Zachowujemy dla kompatybilności wstecznej, ale będą ukryte
    private var _przyjety: Boolean = false,
    private var _dataWziecia: String = ""
) {
    // Właściwości dostępowe zapewniające kompatybilność wsteczną
    val przyjety: Boolean
        get() = _przyjety || przyjecia.values.any { it }
    
    val dataWziecia: String
        get() = _dataWziecia.takeIf { it.isNotEmpty() } 
                ?: przyjecia.entries.firstOrNull { it.value }?.key 
                ?: ""
    
    // Getter dla sprawdzenia, czy lek został wzięty danego dnia
    fun isPrzyjetyNaDzien(data: String): Boolean {
        return przyjecia[data] == true
    }
}