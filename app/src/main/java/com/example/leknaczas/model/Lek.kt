package com.example.leknaczas.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
    val iloscNaDawke: Float = 1.0f,
    // Data ważności aktualnego zapasu
    val dataWaznosci: String = ""
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
    
    // Sprawdzanie, czy lek się przeterminował
    fun czyPrzeterminowany(): Boolean {
        if (dataWaznosci.isEmpty()) return false
        return try {
            val dataWaznosciParsed = LocalDate.parse(dataWaznosci)
            LocalDate.now().isAfter(dataWaznosciParsed)
        } catch (e: Exception) {
            false
        }
    }
    
    // Sprawdzanie, czy lek kończy ważność (mniej niż 30 dni)
    fun konczyWaznosc(): Boolean {
        if (dataWaznosci.isEmpty()) return false
        return try {
            val dataWaznosciParsed = LocalDate.parse(dataWaznosci)
            val dniDoWygasniecia = ChronoUnit.DAYS.between(LocalDate.now(), dataWaznosciParsed)
            dniDoWygasniecia in 1..30
        } catch (e: Exception) {
            false
        }
    }
    
    // Formatowanie daty ważności
    fun dataWaznosciFormatted(): String {
        if (dataWaznosci.isEmpty()) return ""
        return try {
            val date = LocalDate.parse(dataWaznosci)
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            dataWaznosci
        }
    }
    
    // Dni do wygaśnięcia
    fun dniDoWygasniecia(): Long {
        if (dataWaznosci.isEmpty()) return Long.MAX_VALUE
        return try {
            val dataWaznosciParsed = LocalDate.parse(dataWaznosci)
            ChronoUnit.DAYS.between(LocalDate.now(), dataWaznosciParsed)
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }
}