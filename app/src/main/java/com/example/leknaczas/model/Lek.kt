package com.example.leknaczas.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Lek(
    val id: Int,
    val nazwa: String,
    var wziety: Boolean = false
)