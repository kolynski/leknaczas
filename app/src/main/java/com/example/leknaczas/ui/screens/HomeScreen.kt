package com.example.leknaczas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.leknaczas.R
import com.example.leknaczas.ui.components.LekItem
import com.example.leknaczas.ui.components.MedicineCalendar
import com.example.leknaczas.viewmodel.AuthViewModel
import com.example.leknaczas.viewmodel.LekViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    lekViewModel: LekViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val leki by lekViewModel.leki.collectAsStateWithLifecycle()
    val isLoading by lekViewModel.isLoading.collectAsStateWithLifecycle()
    
    var nowyLekNazwa by remember { mutableStateOf("") }
    var nowyLekDawka by remember { mutableStateOf("") }
    var nowyLekCzestotliwosc by remember { mutableStateOf("1 x dziennie") }
    var nowyLekIlosc by remember { mutableStateOf("1") }
    var nowyLekJednostka by remember { mutableStateOf("tabletka") }
    
    // Opcje dla wybieranych wartości
    val czestotliwosciOptions = listOf("1 x dziennie", "2 x dziennie", "3 x dziennie", "co drugi dzień", "raz w tygodniu")
    val iloscOptions = listOf("1", "2", "3", "1/2", "1/4")
    val jednostkaOptions = listOf("tabletka", "kapsułka", "opakowanie", "zastrzyk", "ampułka", "ml")
    
    var expandedCzestotliwosc by remember { mutableStateOf(false) }
    var expandedIlosc by remember { mutableStateOf(false) }
    var expandedJednostka by remember { mutableStateOf(false) }
    
    // Efekt uruchamiany przy pierwszym wyświetleniu tego ekranu
    LaunchedEffect(Unit) {
        lekViewModel.refreshLeki()
    }
    
    Scaffold(
        topBar = {
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Lewa strona - Formularz i lista leków
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Formularz dodawania leku
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.add_medicine),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = nowyLekNazwa,
                            onValueChange = { nowyLekNazwa = it },
                            label = { Text(stringResource(R.string.medicine_name)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = nowyLekDawka,
                            onValueChange = { nowyLekDawka = it },
                            label = { Text(stringResource(R.string.medicine_dosage)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        
                        // Dropdown dla częstotliwości
                        ExposedDropdownMenuBox(
                            expanded = expandedCzestotliwosc,
                            onExpandedChange = { expandedCzestotliwosc = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
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
                                    .menuAnchor()
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
                        
                        // Row dla ilości i jednostki
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Dropdown dla ilości
                            ExposedDropdownMenuBox(
                                expanded = expandedIlosc,
                                onExpandedChange = { expandedIlosc = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = nowyLekIlosc,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Ilość") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIlosc)
                                    },
                                    modifier = Modifier.menuAnchor()
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
                            
                            // Dropdown dla jednostki
                            ExposedDropdownMenuBox(
                                expanded = expandedJednostka,
                                onExpandedChange = { expandedJednostka = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = nowyLekJednostka,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Jednostka") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJednostka)
                                    },
                                    modifier = Modifier.menuAnchor()
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
                        
                        Button(
                            onClick = {
                                lekViewModel.dodajLek(
                                    nazwa = nowyLekNazwa,
                                    dawka = nowyLekDawka,
                                    czestotliwosc = nowyLekCzestotliwosc,
                                    ilosc = nowyLekIlosc,
                                    jednostka = nowyLekJednostka
                                )
                                nowyLekNazwa = ""
                                nowyLekDawka = ""
                                nowyLekCzestotliwosc = "1 x dziennie"
                                nowyLekIlosc = "1"
                                nowyLekJednostka = "tabletka"
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = nowyLekNazwa.isNotBlank()
                        ) {
                            Text(stringResource(R.string.add))
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
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (leki.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Brak leków. Dodaj swój pierwszy lek.")
                    }
                } else {
                    LazyColumn {
                        items(leki) { lek ->
                            LekItem(
                                lek = lek,
                                onStatusChanged = { lekViewModel.toggleLekStatus(lek) }
                            )
                        }
                    }
                }
            }
            
            // Prawa strona - Kalendarz
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kalendarz przyjmowania leków",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                MedicineCalendar(
                    medications = leki,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}