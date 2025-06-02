package com.example.leknaczas.repository

import com.example.leknaczas.model.Lek
import kotlinx.coroutines.flow.Flow

interface ILekRepository {
    fun getLekiFlow(): Flow<List<Lek>>
    suspend fun addLek(nazwa: String): String
    suspend fun addLek(nazwa: String, dawka: String, czestotliwosc: String): String
    suspend fun addLek(nazwa: String, dawka: String, czestotliwosc: String, ilosc: String, jednostka: String): String
    suspend fun addLek(nazwa: String, czestotliwosc: String, ilosc: String, jednostka: String): String
    suspend fun updateLekStatus(lek: Lek)
    suspend fun updateLekStatus(lek: Lek, dataWziecia: String)
    suspend fun deleteLek(lekId: String)
    suspend fun updateLekSupplyAndStatus(lekId: String, nowaIlosc: Int, data: String)
    suspend fun addSupply(lekId: String, dodanaIlosc: Int)
    suspend fun addSupplyWithExpiryDate(lekId: String, dodanaIlosc: Int, dataWaznosci: String)
}
