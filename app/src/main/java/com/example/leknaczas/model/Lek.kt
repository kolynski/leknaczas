package com.example.leknaczas.model

data class Lek(
    val id: String = "",
    val nazwa: String = "",
    val dawka: String = "",
    val czestotliwosc: String = "",
    val ilosc: String = "", // Nowe pole - ilość leku (np. 1 tabletka)
    val jednostka: String = "", // Nowe pole - jednostka (np. tabletka, opakowanie)
    val przyjecia: Map<String, Boolean> = emptyMap() // <--- dodaj to pole
)