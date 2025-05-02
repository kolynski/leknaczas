package com.example.leknaczas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.leknaczas.R
import com.example.leknaczas.ui.components.LekItem
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
    var nowyLekCzestotliwosc by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    
                    OutlinedTextField(
                        value = nowyLekCzestotliwosc,
                        onValueChange = { nowyLekCzestotliwosc = it },
                        label = { Text(stringResource(R.string.medicine_frequency)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    Button(
                        onClick = {
                            lekViewModel.dodajLek(
                                nazwa = nowyLekNazwa,
                                dawka = nowyLekDawka,
                                czestotliwosc = nowyLekCzestotliwosc
                            )
                            nowyLekNazwa = ""
                            nowyLekDawka = ""
                            nowyLekCzestotliwosc = ""
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
    }
}