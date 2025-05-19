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
    private var _dataWziecia: String = "",
    // Nowe pola do zarządzania ilością leku
    val dostepneIlosc: Int = 0,
    val iloscNaDawke: Float = 1.0f
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
    
    // Formatowanie dostępnej ilości z uwzględnieniem różnych jednostek
    fun dostepneIloscFormatted(): String {
        return when (jednostka) {
            "ml" -> "$dostepneIlosc ml"
            else -> "$dostepneIlosc szt."
        }
    }
    
    // Sprawdzanie, czy kończy się zapas leku (mniej niż 5 sztuk lub dawek)
    fun konczySieZapas(): Boolean {
        return dostepneIlosc <= 5
    }
}