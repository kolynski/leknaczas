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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.leknaczas.model.Lek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import java.time.DayOfWeek
import java.util.*

data class MedicineCalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val medicationsForDay: List<MedicationScheduleInfo> = emptyList()
)

data class MedicationScheduleInfo(
    val lek: Lek,
    val status: MedicineStatus,
    val isScheduledForDay: Boolean, // Indicates if the med should be taken this day based on frequency
    val dayInCycle: Int? = null // Optional: Day number in a medication cycle
)

enum class MedicineStatus {
    TAKEN,       // Wzięty (zielony)
    NOT_TAKEN,   // Nie wzięty (czerwony)
    SCHEDULED,   // Zaplanowany do wzięcia (pomarańczowy)
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
                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = "Previous month"
                    )
                }
                
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
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
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Calendar days grid
            val calendarDays = generateCalendarDays(currentMonth, medications)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(min = 240.dp),
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
            
            // Medications legend
            if (medications.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Leki w kalendarzu:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    medications.forEach { lek ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val color = getMedicationColor(lek)
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${lek.nazwa} - ${lek.ilosc} ${lek.jednostka}, ${lek.czestotliwosc}",
                                style = MaterialTheme.typography.bodySmall,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            // Status legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color.Green, text = "Wzięty")
                LegendItem(color = Color.Red, text = "Pominięty")
                LegendItem(color = Color(0xFFFFA500), text = "Zaplanowany") // Orange
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
    val borderColor = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (day.isCurrentMonth) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    
    // Custom border widths
    val strokeWidthPx = with(LocalDensity.current) { 1.dp.toPx() }
    
    Box(
        modifier = modifier
            .padding(1.dp)
            .border(1.dp, borderColor)
            .drawMedicationIndicators(day.medicationsForDay, strokeWidthPx),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Day number
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            // If we have medications for this day, show them
            if (day.medicationsForDay.isNotEmpty()) {
                val takenMeds = day.medicationsForDay.count { it.status == MedicineStatus.TAKEN }
                val totalScheduled = day.medicationsForDay.count { it.isScheduledForDay }
                
                if (totalScheduled > 0) {
                    Text(
                        text = "$takenMeds/$totalScheduled",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = androidx.compose.ui.unit.sp(9),
                        color = if (takenMeds >= totalScheduled) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

private fun Modifier.drawMedicationIndicators(
    medicationsInfo: List<MedicationScheduleInfo>,
    strokeWidth: Float
): Modifier {
    return this.drawBehind {
        if (medicationsInfo.isEmpty()) {
            // Draw light gray background if no medications
            drawRect(Color.LightGray.copy(alpha = 0.1f))
            return@drawBehind
        }
        
        // Group by medication to draw indicators
        val medGroups = medicationsInfo.groupBy { it.lek.id }
        
        // Calculate section height
        val sectionHeight = size.height / medGroups.size.coerceAtLeast(1)
        
        medGroups.values.forEachIndexed { index, medInfos ->
            if (medInfos.isEmpty()) return@forEachIndexed
            
            val info = medInfos.first()
            val backgroundColor = when (info.status) {
                MedicineStatus.TAKEN -> Color.Green.copy(alpha = 0.3f)
                MedicineStatus.NOT_TAKEN -> Color.Red.copy(alpha = 0.3f)
                MedicineStatus.SCHEDULED -> Color(0xFFFFA500).copy(alpha = 0.3f) // Orange
                MedicineStatus.NONE -> Color.LightGray.copy(alpha = 0.1f)
            }
            
            // Calculate the section for this medication
            val top = index * sectionHeight
            drawRect(
                color = backgroundColor,
                topLeft = Offset(0f, top),
                size = androidx.compose.ui.geometry.Size(size.width, sectionHeight)
            )
            
            // If this is a scheduled day in a cycle, draw a pattern
            if (info.isScheduledForDay) {
                drawSchedulePattern(
                    info.lek,
                    top,
                    top + sectionHeight,
                    strokeWidth
                )
            }
        }
    }
}

private fun DrawScope.drawSchedulePattern(
    lek: Lek,
    top: Float,
    bottom: Float,
    strokeWidth: Float
) {
    // Get cycle pattern based on frequency
    val pattern = when (lek.czestotliwosc) {
        "co drugi dzień" -> listOf(true, false)
        "raz w tygodniu" -> listOf(true, false, false, false, false, false, false)
        // For "2 x dziennie" or "3 x dziennie" we still mark the day
        else -> listOf(true)
    }
    
    if (pattern.size > 1) { // Only draw pattern for periodic schedules
        val patternWidth = size.width / pattern.size
        
        pattern.forEachIndexed { index, isActive ->
            if (isActive) {
                // Draw diagonal lines for scheduled days
                val startX = index * patternWidth
                val endX = startX + patternWidth
                
                // Draw diagonal lines
                for (i in 0..4) {
                    val offset = i * 4
                    drawLine(
                        color = Color.Black.copy(alpha = 0.2f),
                        start = Offset(startX + offset, top),
                        end = Offset(startX + offset + (bottom - top)/2, bottom),
                        strokeWidth = strokeWidth
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

private fun getMedicationColor(lek: Lek): Color {
    // Generate a unique color for each medication based on the name
    val hash = lek.nazwa.hashCode()
    return Color(
        red = ((hash and 0xFF0000) shr 16) / 255f,
        green = ((hash and 0x00FF00) shr 8) / 255f,
        blue = (hash and 0x0000FF) / 255f,
        alpha = 0.7f
    )
}

private fun generateCalendarDays(yearMonth: YearMonth, medications: List<Lek>): List<MedicineCalendarDay> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    // In the European calendar, Monday is the first day (value 1), Sunday is the last day (value 7)
    var dayOfWeek = firstDayOfMonth.dayOfWeek.value
    if (dayOfWeek == 7) dayOfWeek = 0 // Adjust Sunday from 7 to 0
    
    // Calculate how many days we need to show from the previous month
    val daysFromPreviousMonth = if (dayOfWeek == 0) 6 else dayOfWeek - 1
    
    val calendarStart = firstDayOfMonth.minusDays(daysFromPreviousMonth.toLong())
    
    // We need 6 rows of 7 days each to ensure we have enough space
    val daysToShow = 42 // 6 weeks × 7 days
    
    val today = LocalDate.now()
    
    return (0 until daysToShow).map { dayOffset ->
        val currentDate = calendarStart.plusDays(dayOffset.toLong())
        val isCurrentMonth = currentDate.month == yearMonth.month
        
        // Determine medications and status for this day
        val medicationsForDay = if (isCurrentMonth || currentDate == today) {
            determineMedicationsForDay(currentDate, medications)
        } else {
            emptyList()
        }
        
        MedicineCalendarDay(
            date = currentDate,
            isCurrentMonth = isCurrentMonth,
            medicationsForDay = medicationsForDay
        )
    }
}

private fun determineMedicationsForDay(date: LocalDate, medications: List<Lek>): List<MedicationScheduleInfo> {
    val today = LocalDate.now()
    
    return medications.map { lek ->
        // Determine if this medication should be taken on this day based on frequency
        val isScheduled = isScheduledForDate(date, lek.czestotliwosc)
        
        val status = when {
            date.isAfter(today) -> {
                if (isScheduled) MedicineStatus.SCHEDULED else MedicineStatus.NONE
            }
            date.isEqual(today) -> {
                if (lek.przyjety) {
                    MedicineStatus.TAKEN
                } else if (isScheduled) {
                    MedicineStatus.NOT_TAKEN
                } else {
                    MedicineStatus.NONE
                }
            }
            else -> { // Past days
                // For demo, alternate based on day of month and medication pattern
                if (isScheduled) {
                    if ((date.dayOfMonth + lek.nazwa.length) % 3 != 0) {
                        MedicineStatus.TAKEN
                    } else {
                        MedicineStatus.NOT_TAKEN
                    }
                } else {
                    MedicineStatus.NONE
                }
            }
        }
        
        // Calculate day in cycle if applicable
        val dayInCycle = when (lek.czestotliwosc) {
            "co drugi dzień" -> ChronoUnit.DAYS.between(LocalDate.of(date.year, 1, 1), date) % 2 + 1
            "raz w tygodniu" -> date.dayOfWeek.value.toLong()
            else -> null
        }?.toInt()
        
        MedicationScheduleInfo(
            lek = lek,
            status = status,
            isScheduledForDay = isScheduled,
            dayInCycle = dayInCycle
        )
    }
}

private fun isScheduledForDate(date: LocalDate, frequency: String): Boolean {
    return when (frequency) {
        "1 x dziennie", "2 x dziennie", "3 x dziennie" -> true
        "co drugi dzień" -> ChronoUnit.DAYS.between(LocalDate.of(date.year, 1, 1), date) % 2 == 0L
        "raz w tygodniu" -> date.dayOfWeek == DayOfWeek.MONDAY // Assume Monday is the day
        else -> true // Default to scheduled if frequency is unknown
    }
}