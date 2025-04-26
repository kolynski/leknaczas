package com.example.leknaczas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leknaczas.ui.components.LekItem
import com.example.leknaczas.ui.theme.LeknaczasTheme
import com.example.leknaczas.viewmodel.LekViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeknaczasTheme {
                LekNaCzasApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LekNaCzasApp(lekViewModel: LekViewModel = viewModel()) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            var nowyLekNazwa by remember { mutableStateOf("") }
            
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
                    
                    Button(
                        onClick = {
                            lekViewModel.dodajLek(nowyLekNazwa)
                            nowyLekNazwa = ""
                        },
                        modifier = Modifier.align(Alignment.End)
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
            
            LazyColumn {
                items(lekViewModel.leki) { lek ->
                    LekItem(
                        lek = lek,
                        onStatusChanged = { lekViewModel.toggleLekStatus(it) }
                    )
                }
            }
        }
    }

}}