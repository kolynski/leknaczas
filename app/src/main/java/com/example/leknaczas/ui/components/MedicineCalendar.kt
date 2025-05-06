package com.example.leknaczas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.leknaczas.model.Lek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext

data class MedicineCalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val status: MedicineStatus = MedicineStatus.NONE
)

enum class MedicineStatus {
    TAKEN,       // Wzięty (zielony)
    NOT_TAKEN,   // Nie wzięty (czerwony)
    NONE         // Nie było leku tego dnia (szary)
}

@Composable
fun MedicineCalendar(
    medications: List<Lek>,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Calendar header with month name and navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) }
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.NavigateBefore,
                        contentDescription = "Previous month"
                    )
                }
                
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.titleMedium
                )
                
                androidx.compose.material3.IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) }
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.NavigateNext,
                        contentDescription = "Next month"
                    )
                }
            }
            
            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("Pon", "Wt", "Śr", "Czw", "Pt", "Sb", "Nd")
                daysOfWeek.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Calendar days grid
            val calendarDays = generateCalendarDays(currentMonth, medications)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(min = 200.dp),
                content = {
                    items(calendarDays) { day ->
                        CalendarDay(
                            day = day,
                            isToday = day.date == today,
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            )
            
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color.Green, text = "Wzięty")
                LegendItem(color = Color.Red, text = "Pominięty")
                LegendItem(color = Color.LightGray, text = "Brak leku")
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: MedicineCalendarDay,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (day.status) {
        MedicineStatus.TAKEN -> Color.Green.copy(alpha = 0.3f)
        MedicineStatus.NOT_TAKEN -> Color.Red.copy(alpha = 0.3f)
        MedicineStatus.NONE -> Color.LightGray.copy(alpha = 0.1f)
    }
    
    val textColor = if (day.isCurrentMonth) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .background(backgroundColor)
            .then(
                if (isToday) {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primary)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun generateCalendarDays(yearMonth: YearMonth, medications: List<Lek>): List<MedicineCalendarDay> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    // In the European calendar, Monday is the first day (value 1), Sunday is the last day (value 7)
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value
    
    // Calculate how many days we need to show from the previous month
    val daysFromPreviousMonth = dayOfWeek - 1
    
    val calendarStart = firstDayOfMonth.minusDays(daysFromPreviousMonth.toLong())
    
    // We need 6 rows of 7 days each to ensure we have enough space
    val daysToShow = 42 // 6 weeks × 7 days
    
    return (0 until daysToShow).map { dayOffset ->
        val currentDate = calendarStart.plusDays(dayOffset.toLong())
        val isCurrentMonth = currentDate.month == yearMonth.month
        
        // Determine status based on medication data
        val status = if (isCurrentMonth) {
            determineMedicineStatus(currentDate, medications)
        } else {
            MedicineStatus.NONE
        }
        
        MedicineCalendarDay(
            date = currentDate,
            isCurrentMonth = isCurrentMonth,
            status = status
        )
    }
}

private fun determineMedicineStatus(date: LocalDate, medications: List<Lek>): MedicineStatus {
    // In a real app, you would check your database for medication intake records
    // For this example, we'll use the existing medicines and determine based on currentDate
    
    // For simplicity, let's assume:
    // - If today, check if medicine.przyjety is true
    // - If in the past, randomly mark as taken/not taken
    // - If in the future, mark as NONE
    
    val today = LocalDate.now()
    
    return when {
        date.isAfter(today) -> MedicineStatus.NONE
        date.isEqual(today) -> {
            if (medications.any { it.przyjety }) {
                MedicineStatus.TAKEN
            } else if (medications.isNotEmpty()) {
                MedicineStatus.NOT_TAKEN
            } else {
                MedicineStatus.NONE
            }
        }
        else -> {
            // For past days, alternate between taken and not taken for demo purposes
            if (medications.isNotEmpty() && date.dayOfMonth % 3 != 0) {
                MedicineStatus.TAKEN
            } else if (medications.isNotEmpty()) {
                MedicineStatus.NOT_TAKEN
            } else {
                MedicineStatus.NONE
            }
        }
    }
}