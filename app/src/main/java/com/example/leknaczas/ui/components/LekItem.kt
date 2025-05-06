package com.example.leknaczas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.leknaczas.model.Lek

@Composable
fun LekItem(
    lek: Lek, 
    onStatusChanged: (Lek) -> Unit,
    onDelete: (Lek) -> Unit
) {
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
                
                // Pokazuj częstotliwość
                if (lek.czestotliwosc.isNotEmpty()) {
                    Text(text = "Częstotliwość: ${lek.czestotliwosc}")
                }
                
                // Pokazuj ilość i jednostkę
                if (lek.ilosc.isNotEmpty() && lek.jednostka.isNotEmpty()) {
                    Text(text = "Ilość: ${lek.ilosc} ${lek.jednostka}")
                }
            }
            
            // Przycisk usuwania
            IconButton(onClick = { onDelete(lek) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń lek",
                    tint = androidx.compose.ui.graphics.Color.Red
                )
            }
            
            // Checkbox do oznaczania leku jako wzięty
            Checkbox(
                checked = lek.przyjety,
                onCheckedChange = { onStatusChanged(lek) }
            )
        }
    }
}
