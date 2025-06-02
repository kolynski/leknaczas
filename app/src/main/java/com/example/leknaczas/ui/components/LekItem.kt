package com.example.leknaczas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.leknaczas.model.Lek

@Composable
fun LekItem(
    lek: Lek,
    onStatusChanged: ((Lek) -> Unit)?,
    onDelete: (Lek) -> Unit,
    onAddSupply: (Lek) -> Unit
) {
    // Gradient colors based on medication status
    val gradientColors = when {
        lek.czyPrzeterminowany() -> listOf(
            Color(0xFFFFEBEE), Color(0xFFFFCDD2)
        )
        lek.konczyWaznosc() -> listOf(
            Color(0xFFFFF3E0), Color(0xFFFFE0B2)
        )
        lek.konczySieZapas() -> listOf(
            Color(0xFFFFF8E1), Color(0xFFFFF59D)
        )
        else -> listOf(
            Color(0xFFF3E5F5), Color(0xFFE1BEE7)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with name and status indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Medicine name
                        Text(
                            text = lek.nazwa,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Status indicators
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if (lek.czyPrzeterminowany()) {
                            StatusChip(
                                text = "PRZETERMINOWANY",
                                backgroundColor = Color(0xFFF44336),
                                textColor = Color.White
                            )
                        } else if (lek.konczyWaznosc()) {
                            StatusChip(
                                text = "KOŃCZY WAŻNOŚĆ",
                                backgroundColor = Color(0xFFFF9800),
                                textColor = Color.White
                            )
                        }
                        
                        if (lek.konczySieZapas()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusChip(
                                text = "MAŁO ZAPASÓW",
                                backgroundColor = Color(0xFFFFC107),
                                textColor = Color.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Medicine details in cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dosage info
                    DetailCard(
                        title = "Dawka",
                        value = "${lek.ilosc} ${lek.jednostka}",
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Frequency info
                    DetailCard(
                        title = "Częstotliwość",
                        value = lek.czestotliwosc,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Available amount
                    DetailCard(
                        title = "Dostępne",
                        value = if (lek.dostepneIlosc > 0) lek.dostepneIloscFormatted() else "Brak zapasów",
                        modifier = Modifier.weight(1f),
                        valueColor = if (lek.konczySieZapas()) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Expiry date
                    if (lek.dataWaznosci.isNotEmpty()) {
                        DetailCard(
                            title = "Ważność",
                            value = lek.dataWaznosciFormatted(),
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.DateRange,
                            valueColor = when {
                                lek.czyPrzeterminowany() -> Color(0xFFF44336)
                                lek.konczyWaznosc() -> Color(0xFFFF9800)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add supply button
                    ElevatedButton(
                        onClick = { onAddSupply(lek) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Dodaj zapas",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dodaj zapas")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete button
                        IconButton(
                            onClick = { onDelete(lek) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń lek",
                                tint = Color(0xFFD32F2F)
                            )
                        }
                        
                        // Checkbox
                        if (onStatusChanged != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Checkbox(
                                checked = lek.przyjety,
                                onCheckedChange = { 
                                    if (lek.dostepneIlosc > 0 || !it) {
                                        onStatusChanged(lek)
                                    }
                                },
                                enabled = lek.dostepneIlosc > 0 || !lek.przyjety,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = title,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = valueColor
            )
        }
    }
}