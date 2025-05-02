package com.example.leknaczas.repository

import com.example.leknaczas.model.Lek
import kotlinx.coroutines.flow.Flow

interface ILekRepository {
    fun getLekiFlow(): Flow<List<Lek>>
    suspend fun addLek(nazwa: String): String
    suspend fun updateLekStatus(lek: Lek)
}
