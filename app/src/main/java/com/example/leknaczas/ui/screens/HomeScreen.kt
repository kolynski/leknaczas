package com.example.leknaczas.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.leknaczas.R
import com.example.leknaczas.model.Lek
import com.example.leknaczas.ui.components.LekItem
import com.example.leknaczas.ui.components.MedicineCalendar
import com.example.leknaczas.ui.components.StreakCard
import com.example.leknaczas.viewmodel.AuthViewModel
import com.example.leknaczas.viewmodel.LekViewModel
import com.example.leknaczas.notification.NotificationService
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Funkcja pomocnicza do sprawdzenia, czy lek powinien być zaplanowany na określony dzień
fun isScheduledForDate(date: LocalDate, frequency: String): Boolean {
    return when (frequency) {
        "1 x dziennie", "2 x dziennie", "3 x dziennie" -> true
        "co drugi dzień" -> ChronoUnit.DAYS.between(LocalDate.of(date.year, 1, 1), date) % 2 == 0L
        "raz w tygodniu" -> date.dayOfWeek == DayOfWeek.MONDAY // Assume Monday is the day
        else -> true // Default to scheduled if frequency is unknown
    }
}

// Funkcja calculateStreakInfo musi być zdefiniowana przed HomeScreen, gdzie jest używana
fun calculateStreakInfo(leki: List<Lek>): Triple<Int, Int, Float> {
    if (leki.isEmpty()) {
        return Triple(0, 0, 0f)
    }
    
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    // Dla każdego dnia przechowujemy mapę: data -> (zaplanowane leki, wzięte leki)
    val dateToMedicationStatus = mutableMapOf<LocalDate, Pair<Int, Int>>()
    
    // Zbieramy informacje o wszystkich lekach dla wszystkich dat
    val allDates = mutableSetOf<LocalDate>()
    
    // Zbieramy informacje o wszystkich lekach dla wszystkich dat
    for (lek in leki) {
        // Sprawdzamy wszystkie daty od początku roku do dzisiaj
        val startDate = today.withDayOfYear(1)
        
        var checkDate = startDate
        
        while (!checkDate.isAfter(today)) {
            val dateStr = checkDate.toString()
            allDates.add(checkDate)
            
            // Czy lek powinien być zaplanowany na ten dzień
            val shouldBeScheduled = isScheduledForDate(checkDate, lek.czestotliwosc)
            
            if (shouldBeScheduled) {
                // Pobierz aktualny status dla tej daty
                val statusForDate = dateToMedicationStatus.getOrDefault(checkDate, Pair(0, 0))
                
                // Zwiększ liczbę zaplanowanych leków
                val scheduledCount = statusForDate.first + 1
                
                // Sprawdź, czy lek został wzięty w tym dniu
                val taken = lek.przyjecia[dateStr] == true
                val takenCount = statusForDate.second + (if (taken) 1 else 0)
                
                // Aktualizuj status dla tej daty
                dateToMedicationStatus[checkDate] = Pair(scheduledCount, takenCount)
            }
            
            checkDate = checkDate.plusDays(1)
        }
    }
    
    // Funkcja pomocnicza do sprawdzenia czy wszystkie zaplanowane leki na dany dzień zostały wzięte
    fun areAllMedicationsTaken(date: LocalDate): Boolean {
        val status = dateToMedicationStatus[date] ?: return false
        return status.first > 0 && status.first == status.second
    }
    
    // Oblicz aktualną serię
    var currentStreak = 0
    var checkDate = today
    
    while (true) {
        if (areAllMedicationsTaken(checkDate)) {
            currentStreak++ 
            checkDate = checkDate.minusDays(1)
        } else {
            break
        }
    }
    
    // Oblicz najdłuższą serię
    var longestStreak = 0
    var currentLongestStreak = 0
    var prevDate: LocalDate? = null
    
    for (date in allDates.sortedBy { it }) {
        if (areAllMedicationsTaken(date)) {
            if (prevDate == null || ChronoUnit.DAYS.between(prevDate, date) == 1L) {
                currentLongestStreak++
            } else {
                currentLongestStreak = 1
            }
            
            longestStreak = maxOf(longestStreak, currentLongestStreak)
            prevDate = date
        } else {
            currentLongestStreak = 0
            prevDate = null
        }
    }
    
    // Oblicz tygodniową skuteczność
    var takenDays = 0
    var totalDays = 0
    
    for (i in 0..6) {
        val date = today.minusDays(i.toLong())
        val status = dateToMedicationStatus[date]
        
        if (status != null && status.first > 0) {
            totalDays++
            if (status.first == status.second) {
                takenDays++
            }
        }
    }
    
    val lastWeekAdherence = if (totalDays > 0) takenDays.toFloat() / totalDays else 0f
    
    return Triple(currentStreak, longestStreak, lastWeekAdherence)
}

@OptIn(
    ExperimentalMaterial3Api::class, 
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    lekViewModel: LekViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val leki by lekViewModel.leki.collectAsStateWithLifecycle()
    val isLoading by lekViewModel.isLoading.collectAsStateWithLifecycle()
    
    // Dodaj te zmienne stanu na początku funkcji HomeScreen
    var showSupplyDialog by remember { mutableStateOf(false) }
    var selectedLek by remember { mutableStateOf<Lek?>(null) }
    var noweOpakowanie by remember { mutableStateOf("") }
    var iloscWSztukach by remember { mutableStateOf("") }
    
    // Zmienne dla dodawania leku
    var nowyLekNazwa by remember { mutableStateOf("") }
    var nowyLekCzestotliwosc by remember { mutableStateOf("1 x dziennie") }
    var nowyLekIlosc by remember { mutableStateOf("1") }
    var nowyLekJednostka by remember { mutableStateOf("tabletka") }
    
    // Nowe zmienne stanu dla daty ważności i producenta
    var nowyLekDataWaznosci by remember { mutableStateOf("") }
    var nowyLekProducent by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Opcje dla wybieranych wartości
    val czestotliwosciOptions = listOf("1 x dziennie", "2 x dziennie", "3 x dziennie", "co drugi dzień", "raz w tygodniu")
    val iloscOptions = listOf("1", "2", "3", "1/2", "1/4")
    val jednostkaOptions = listOf("tabletka", "kapsułka", "opakowanie", "zastrzyk", "ampułka", "ml")
    
    var expandedCzestotliwosc by remember { mutableStateOf(false) }
    var expandedIlosc by remember { mutableStateOf(false) }
    var expandedJednostka by remember { mutableStateOf(false) }
    
    // Inicjalizacja pagerState dla przesuwania ekranów w poziomie
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    // Tworzenie zakresu korutyny dla operacji asynchronicznych
    val coroutineScope = rememberCoroutineScope()
    
    // Nazwy zakładek
    val tabTitles = listOf("Leki", "Kalendarz", "Statystyki")
    
    // Efekt uruchamiany przy pierwszym wyświetleniu tego ekranu
    LaunchedEffect(Unit) {
        lekViewModel.refreshLeki()
    }

    // Calculate streak information
    val streakInfo = calculateStreakInfo(leki)
    val currentStreak = streakInfo.first
    val longestStreak = streakInfo.second
    val lastWeekAdherence = streakInfo.third

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    actions = {
                        // Dodajemy przycisk odświeżania
                        IconButton(
                            onClick = { lekViewModel.refreshLeki() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Odśwież"
                            )
                        }
                        IconButton(
                            onClick = {
                                authViewModel.logout()
                                onLogout()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = stringResource(id = R.string.logout)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                
                // Dodanie zakładek na górze ekranu
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { 
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    // Page 0: Medicine List
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Formularz dodawania leku
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // W sekcji dodawania leku (Page 0), zamień istniejący formularz na:
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Header with gradient background
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        listOf(
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                                        )
                                                    )
                                                )
                                                .padding(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.add_medicine),
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color.White
                                            )
                                        }
                                    }
                                    
                                    // Medicine name
                                    OutlinedTextField(
                                        value = nowyLekNazwa,
                                        onValueChange = { nowyLekNazwa = it },
                                        label = { Text(stringResource(R.string.medicine_name)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                        )
                                    )
                                    
                                    // Producer field
                                    OutlinedTextField(
                                        value = nowyLekProducent,
                                        onValueChange = { nowyLekProducent = it },
                                        label = { Text("Producent/Marka (opcjonalne)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                        )
                                    )
                                    
                                    // Frequency dropdown
                                    ExposedDropdownMenuBox(
                                        expanded = expandedCzestotliwosc,
                                        onExpandedChange = { expandedCzestotliwosc = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = nowyLekCzestotliwosc,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text(stringResource(R.string.medicine_frequency)) },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCzestotliwosc)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                            )
                                        )
                                        
                                        ExposedDropdownMenu(
                                            expanded = expandedCzestotliwosc,
                                            onDismissRequest = { expandedCzestotliwosc = false }
                                        ) {
                                            czestotliwosciOptions.forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option) },
                                                    onClick = {
                                                        nowyLekCzestotliwosc = option
                                                        expandedCzestotliwosc = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    
                                    // Amount and unit in a row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Amount dropdown
                                        ExposedDropdownMenuBox(
                                            expanded = expandedIlosc,
                                            onExpandedChange = { expandedIlosc = it },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = nowyLekIlosc,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text(stringResource(R.string.medicine_amount)) },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIlosc)
                                                },
                                                modifier = Modifier.menuAnchor(),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            
                                            ExposedDropdownMenu(
                                                expanded = expandedIlosc,
                                                onDismissRequest = { expandedIlosc = false }
                                            ) {
                                                iloscOptions.forEach { option ->
                                                    DropdownMenuItem(
                                                        text = { Text(option) },
                                                        onClick = {
                                                            nowyLekIlosc = option
                                                            expandedIlosc = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Unit dropdown
                                        ExposedDropdownMenuBox(
                                            expanded = expandedJednostka,
                                            onExpandedChange = { expandedJednostka = it },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = nowyLekJednostka,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text(stringResource(R.string.medicine_unit)) },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJednostka)
                                                },
                                                modifier = Modifier.menuAnchor(),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            
                                            ExposedDropdownMenu(
                                                expanded = expandedJednostka,
                                                onDismissRequest = { expandedJednostka = false }
                                            ) {
                                                jednostkaOptions.forEach { option ->
                                                    DropdownMenuItem(
                                                        text = { Text(option) },
                                                        onClick = {
                                                            nowyLekJednostka = option
                                                            expandedJednostka = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    // Expiry date picker
                                    OutlinedTextField(
                                        value = if (nowyLekDataWaznosci.isNotEmpty()) {
                                            try {
                                                LocalDate.parse(nowyLekDataWaznosci).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                            } catch (e: Exception) {
                                                nowyLekDataWaznosci
                                            }
                                        } else "",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Data ważności (opcjonalne)") },
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePicker = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.DateRange,
                                                    contentDescription = "Wybierz datę"
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                        )
                                    )
                                    
                                    // Add button with gradient
                                    ElevatedButton(
                                        onClick = {
                                            lekViewModel.dodajLek(
                                                nazwa = nowyLekNazwa,
                                                czestotliwosc = nowyLekCzestotliwosc,
                                                ilosc = nowyLekIlosc,
                                                jednostka = nowyLekJednostka,
                                                dataWaznosci = nowyLekDataWaznosci,
                                                producent = nowyLekProducent
                                            )
                                            // Reset form
                                            nowyLekNazwa = ""
                                            nowyLekCzestotliwosc = "1 x dziennie"
                                            nowyLekIlosc = "1"
                                            nowyLekJednostka = "tabletka"
                                            nowyLekDataWaznosci = ""
                                            nowyLekProducent = ""
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        enabled = nowyLekNazwa.isNotBlank(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = Color.White
                                        ),
                                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.add),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Lista leków
                            Text(
                                text = stringResource(R.string.medicine_list),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (leki.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Brak leków. Dodaj swój pierwszy lek.")
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    leki.forEach { lek ->
                                        LekItem(
                                            lek = lek,
                                            onStatusChanged = null, // Usunięto checkbox
                                            onDelete = { lekViewModel.usunLek(lek) },
                                            onAddSupply = { 
                                                selectedLek = it
                                                showSupplyDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Page 1: Calendar
                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.medicine_calendar),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            MedicineCalendar(
                                medications = leki,
                                onMedicationStatusChange = { lek, dataWziecia ->
                                    if (dataWziecia.contains(":not_taken")) {
                                        // Jeśli zawiera marker "not_taken", oznaczamy jako niewzięty
                                        val actualDate = dataWziecia.split(":")[0]
                                        lekViewModel.markAsNotTaken(lek, actualDate)
                                    } else {
                                        // Inaczej oznaczamy jako wzięty
                                        lekViewModel.markAsTakenOnDate(lek, dataWziecia)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            if (leki.isEmpty()) {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = stringResource(R.string.no_medicines_calendar),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(vertical = 24.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                    
                    // Page 2: Streak Stats
                    2 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Twoje osiągnięcia",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            // Current streak card
                            StreakCard(
                                title = "Aktualna seria",
                                value = currentStreak,
                                description = "dni pod rząd",
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = if (currentStreak >= 3) Color(0xFFFF9800) else Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )
                            
                            // Longest streak card
                            StreakCard(
                                title = "Najdłuższa seria",
                                value = longestStreak,
                                description = "dni pod rząd",
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = if (longestStreak >= 7) Color(0xFFFF5722) else Color(0xFFFF9800),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )
                            
                            // Last week adherence card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Przyjmowanie leków w ostatnim tygodniu",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val percentage = (lastWeekAdherence * 100).toInt()
                                        Text(
                                            text = "$percentage%",
                                            style = MaterialTheme.typography.displayMedium,
                                            color = when {
                                                percentage >= 90 -> Color(0xFF4CAF50)
                                                percentage >= 75 -> Color(0xFFFFC107)
                                                else -> Color(0xFFF44336)
                                            }
                                        )
                                        
                                        LinearProgressIndicator(
                                            progress = lastWeekAdherence,
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .height(12.dp)
                                                .width(200.dp),
                                            color = when {
                                                lastWeekAdherence >= 0.9f -> Color(0xFF4CAF50)
                                                lastWeekAdherence >= 0.75f -> Color(0xFFFFC107)
                                                else -> Color(0xFFF44336)
                                            }
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Staraj się przyjmować leki regularnie dla lepszych efektów leczenia",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Motivational message based on streak
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val message = when {
                                        currentStreak == 0 -> "Zacznij swoją serię już dzisiaj!"
                                        currentStreak < 3 -> "Dobry początek! Kontynuuj serię!"
                                        currentStreak < 7 -> "Świetnie! Utrzymaj serię przez tydzień!"
                                        currentStreak < 14 -> "Imponujące! Jesteś konsekwentny w dbaniu o swoje zdrowie!"
                                        currentStreak < 30 -> "Niesamowite! Twoja wytrwałość jest godna podziwu!"
                                        else -> "Mistrz! Twoja dyscyplina jest niezrównana!"
                                    }
                                    
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Dialog do dodawania zapasu leku
    if (showSupplyDialog && selectedLek != null) {
        AlertDialog(
            onDismissRequest = { 
                showSupplyDialog = false
                selectedLek = null
                noweOpakowanie = ""
                iloscWSztukach = ""
            },
            title = { Text("Dodaj zapas leku") },
            text = {
                Column {
                    Text(
                        text = "Dodaj nowe opakowanie leku ${selectedLek?.nazwa}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = noweOpakowanie,
                        onValueChange = { noweOpakowanie = it },
                        label = { Text("Liczba opakowań") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = iloscWSztukach,
                        onValueChange = { iloscWSztukach = it },
                        label = { Text("Liczba sztuk w opakowaniu") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        val opakowanieCount = noweOpakowanie.toIntOrNull() ?: 0
                        val sztukiCount = iloscWSztukach.toIntOrNull() ?: 0
                        if (opakowanieCount > 0 && sztukiCount > 0 && selectedLek != null) {
                            lekViewModel.dodajZapas(selectedLek!!, opakowanieCount * sztukiCount)
                        }
                        showSupplyDialog = false
                        selectedLek = null
                        noweOpakowanie = ""
                        iloscWSztukach = ""
                    },
                    enabled = noweOpakowanie.isNotEmpty() && iloscWSztukach.isNotEmpty() &&
                            (noweOpakowanie.toIntOrNull() ?: 0) > 0 &&
                            (iloscWSztukach.toIntOrNull() ?: 0) > 0
                ) {
                    Text("Dodaj")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showSupplyDialog = false
                        selectedLek = null
                        noweOpakowanie = ""
                        iloscWSztukach = ""
                    }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            nowyLekDataWaznosci = selectedDate.toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Anuluj")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}