package com.example.leknaczas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leknaczas.model.Lek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*

data class MedicineCalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val medicationsForDay: List<MedicationScheduleInfo> = emptyList()
)

data class MedicationScheduleInfo(
    val lek: Lek,
    val status: MedicineStatus
)

enum class MedicineStatus {
    TAKEN,       // Wzięty (zielony)
    NOT_TAKEN,   // Nie wzięty (czerwony)
    SCHEDULED,   // Zaplanowany do wzięcia (niebieski)
    NONE         // Nie było leku tego dnia (szary)
}

@Composable
fun MedicineCalendar(
    medications: List<Lek>,
    onMedicationStatusChange: (Lek, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    
    // Dialog state
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedMedication by remember { mutableStateOf<MedicationScheduleInfo?>(null) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Calendar header with month name and navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayOfWeek in DayOfWeek.values()) {
                    val dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar days grid
            val calendarDays = generateCalendarDays(currentMonth, medications)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp),
                content = {
                    items(calendarDays) { day ->
                        CalendarDay(
                            day = day,
                            isToday = day.date == today,
                            isSelected = day.date == selectedDate,
                            onDateClick = { selectedDate = day.date },
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Detail view for selected date
            val medsForSelectedDate = calendarDays.find { it.date == selectedDate }?.medicationsForDay ?: emptyList()
            val isPastOrPresent = !selectedDate.isAfter(today)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leki na ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault()))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (isPastOrPresent && selectedDate != today) {
                    // Add a small edit button hint for past dates
                    Text(
                        text = "Można edytować",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (medsForSelectedDate.isEmpty()) {
                // Check if we should show potential medications that could be added
                val potentialMeds = if (isPastOrPresent) {
                    medications.filter { lek -> 
                        isScheduledForDate(selectedDate, lek.czestotliwosc) 
                    }.map { lek ->
                        MedicationScheduleInfo(lek, MedicineStatus.NOT_TAKEN)
                    }
                } else emptyList()
                
                if (potentialMeds.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak leków na ten dzień",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Zaplanowane leki na ten dzień:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        potentialMeds.forEach { medInfo ->
                            MedicationStatusItem(
                                medInfo = medInfo,
                                isPast = isPastOrPresent,
                                onEditStatus = {
                                    selectedMedication = medInfo
                                    showStatusDialog = true
                                }
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    medsForSelectedDate.forEach { medInfo ->
                        MedicationStatusItem(
                            medInfo = medInfo,
                            isPast = isPastOrPresent,
                            onEditStatus = {
                                selectedMedication = medInfo
                                showStatusDialog = true
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status legend
            Text(
                text = "Legenda",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF4CAF50), text = "Wzięty") // Green
                LegendItem(color = Color(0xFFF44336), text = "Pominięty") // Red
                LegendItem(color = Color(0xFF2196F3), text = "Zaplanowany") // Blue
            }
        }
    }
    
    // Dialog to change medication status
    if (showStatusDialog && selectedMedication != null) {
        val med = selectedMedication!!
        
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Status leku") },
            text = {
                Column {
                    Text("Czy przyjąłeś ${med.lek.nazwa} dnia ${
                        selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()))
                    }?")
                    
                    Text(
                        text = "Dawka: ${med.lek.ilosc} ${med.lek.jednostka}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        onMedicationStatusChange(med.lek, selectedDate.toString())
                        showStatusDialog = false
                    }
                ) {
                    Text("Tak, wziąłem/-ęłam")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { 
                        // For NOT_TAKEN, we set the date to empty
                        onMedicationStatusChange(med.lek, "")
                        showStatusDialog = false
                    }
                ) {
                    Text("Nie wziąłem/-ęłam")
                }
            }
        )
    }
}

@Composable
private fun CalendarDay(
    day: MedicineCalendarDay,
    isToday: Boolean,
    isSelected: Boolean,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayTextColor = when {
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }
    
    // Calculate medication status indicators
    val takenCount = day.medicationsForDay.count { it.status == MedicineStatus.TAKEN }
    val notTakenCount = day.medicationsForDay.count { it.status == MedicineStatus.NOT_TAKEN }
    val scheduledCount = day.medicationsForDay.count { it.status == MedicineStatus.SCHEDULED }
    
    val hasMedications = takenCount + notTakenCount + scheduledCount > 0
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = day.isCurrentMonth) { onDateClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Day number
            Text(
                text = day.date.dayOfMonth.toString(),
                color = dayTextColor,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Medication indicators
            if (hasMedications) {
                Row(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (takenCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)) // Green
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    if (notTakenCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336)) // Red
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    if (scheduledCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3)) // Blue
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationStatusItem(
    medInfo: MedicationScheduleInfo,
    isPast: Boolean = false,
    onEditStatus: () -> Unit = {}
) {
    val backgroundColor = when (medInfo.status) {
        MedicineStatus.TAKEN -> Color(0xFF4CAF50).copy(alpha = 0.1f) // Green with opacity
        MedicineStatus.NOT_TAKEN -> Color(0xFFF44336).copy(alpha = 0.1f) // Red with opacity
        MedicineStatus.SCHEDULED -> Color(0xFF2196F3).copy(alpha = 0.1f) // Blue with opacity
        MedicineStatus.NONE -> Color.Transparent
    }
    
    val iconTint = when (medInfo.status) {
        MedicineStatus.TAKEN -> Color(0xFF4CAF50) // Green
        MedicineStatus.NOT_TAKEN -> Color(0xFFF44336) // Red
        MedicineStatus.SCHEDULED -> Color(0xFF2196F3) // Blue
        MedicineStatus.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val statusText = when (medInfo.status) {
        MedicineStatus.TAKEN -> "Wzięty"
        MedicineStatus.NOT_TAKEN -> "Pominięty"
        MedicineStatus.SCHEDULED -> "Zaplanowany"
        MedicineStatus.NONE -> "Brak"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (medInfo.status) {
                    MedicineStatus.TAKEN -> Icons.Default.CheckCircle
                    MedicineStatus.NOT_TAKEN -> Icons.Default.Close
                    MedicineStatus.SCHEDULED -> Icons.Default.CheckCircle
                    MedicineStatus.NONE -> Icons.Default.CheckCircle
                },
                contentDescription = statusText,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medInfo.lek.nazwa,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${medInfo.lek.ilosc} ${medInfo.lek.jednostka}, ${medInfo.lek.czestotliwosc}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = iconTint
            )
            
            // Show edit button only for past dates or NOT_TAKEN current date
            if (isPast) {
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onEditStatus,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edytuj status",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp
        )
    }
}

private fun generateCalendarDays(yearMonth: YearMonth, medications: List<Lek>): List<MedicineCalendarDay> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    // Get day of week adjusted for locale (Monday as 1, Sunday as 7)
    var dayOfWeek = firstDayOfMonth.dayOfWeek.value
    
    // Calculate how many days we need to show from the previous month (to start with Monday)
    val daysFromPreviousMonth = if (dayOfWeek == 7) 6 else dayOfWeek - 1
    
    val calendarStart = firstDayOfMonth.minusDays(daysFromPreviousMonth.toLong())
    
    // We need 6 rows of 7 days to ensure we have enough space
    val daysToShow = 42 // 6 weeks × 7 days
    
    val today = LocalDate.now()
    
    return (0 until daysToShow).map { dayOffset ->
        val currentDate = calendarStart.plusDays(dayOffset.toLong())
        val isCurrentMonth = currentDate.month == yearMonth.month
        
        // Determine medications and status for this day
        val medicationsForDay = determineMedicationsForDay(currentDate, medications)
        
        MedicineCalendarDay(
            date = currentDate,
            isCurrentMonth = isCurrentMonth,
            medicationsForDay = medicationsForDay
        )
    }
}

private fun determineMedicationsForDay(date: LocalDate, medications: List<Lek>): List<MedicationScheduleInfo> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val resultList = mutableListOf<MedicationScheduleInfo>()
    
    medications.forEach { lek ->
        // Check if this medication should be scheduled for this date based on frequency
        val shouldBeScheduled = isScheduledForDate(date, lek.czestotliwosc)
        
        if (shouldBeScheduled) {
            // First check if the medication has a specific status for this date
            if (lek.dataWziecia.isNotEmpty()) {
                try {
                    val takenDate = LocalDate.parse(lek.dataWziecia, formatter)
                    if (takenDate == date && lek.przyjety) {
                        // Medication was taken on this date
                        resultList.add(MedicationScheduleInfo(lek, MedicineStatus.TAKEN))
                        return@forEach
                    }
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            
            // If we reach here, the medication wasn't marked as taken on this date
            
            // For today - show as NOT_TAKEN
            if (date == today) {
                resultList.add(MedicationScheduleInfo(lek, MedicineStatus.NOT_TAKEN))
            } 
            // For past dates - show as NOT_TAKEN (missed)
            else if (date.isBefore(today)) {
                resultList.add(MedicationScheduleInfo(lek, MedicineStatus.NOT_TAKEN))
            }
            // For future dates - show as scheduled
            else {
                resultList.add(MedicationScheduleInfo(lek, MedicineStatus.SCHEDULED))
            }
        }
    }
    
    return resultList
}

private fun isScheduledForDate(date: LocalDate, frequency: String): Boolean {
    return when (frequency) {
        "1 x dziennie", "2 x dziennie", "3 x dziennie" -> true
        "co drugi dzień" -> ChronoUnit.DAYS.between(LocalDate.of(date.year, 1, 1), date) % 2 == 0L
        "raz w tygodniu" -> date.dayOfWeek == DayOfWeek.MONDAY // Assume Monday is the day
        else -> true // Default to scheduled if frequency is unknown
    }
}