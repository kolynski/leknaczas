package com.example.leknaczas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.leknaczas.model.Lek

@Composable
fun LekItem(lek: Lek, onStatusChanged: (Lek) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lek.nazwa)
                Text(text = "Dawka: ${lek.dawka}")
                Text(text = "Częstotliwość: ${lek.czestotliwosc}")
            }
            Checkbox(
                checked = lek.przyjety,
                onCheckedChange = { onStatusChanged(lek) }
            )
        }
    }
}
