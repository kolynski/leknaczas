package com.example.leknaczas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.leknaczas.model.Lek

@Composable
fun LekItem(
    lek: Lek,
    onStatusChanged: (Lek) -> Unit,
    onDelete: (Lek) -> Unit,
    onAddSupply: (Lek) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lek.nazwa,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (lek.konczySieZapas()) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Kończy się zapas",
                                tint = Color(0xFFFF9800), // Pomarańczowy
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    
                    // Pokazuj częstotliwość
                    if (lek.czestotliwosc.isNotEmpty()) {
                        Text(
                            text = "Częstotliwość: ${lek.czestotliwosc}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // Pokazuj ilość i jednostkę dawkowania
                    if (lek.ilosc.isNotEmpty() && lek.jednostka.isNotEmpty()) {
                        Text(
                            text = "Dawka: ${lek.ilosc} ${lek.jednostka}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // Pokazuj dostępną ilość
                    val dostepneText = if (lek.dostepneIlosc > 0)
                        "Dostępne: ${lek.dostepneIloscFormatted()}"
                    else
                        "Brak zapasów"
                    
                    Text(
                        text = dostepneText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (lek.konczySieZapas()) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Przycisk dodawania zapasu
                OutlinedButton(
                    onClick = { onAddSupply(lek) },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Dodaj zapas"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dodaj zapas")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Przycisk usuwania
                    IconButton(onClick = { onDelete(lek) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Usuń lek",
                            tint = Color.Red
                        )
                    }
                    
                    // Checkbox do oznaczania leku jako wzięty
                    Checkbox(
                        checked = lek.przyjety,
                        onCheckedChange = { 
                            if (lek.dostepneIlosc > 0 || !it) {
                                onStatusChanged(lek)
                            }
                        },
                        enabled = lek.dostepneIlosc > 0 || !lek.przyjety
                    )
                }
            }
        }
    }
}
