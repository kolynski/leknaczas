package com.example.leknaczas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.leknaczas.R
import com.example.leknaczas.model.Lek

@Composable
fun LekItem(
    lek: Lek,
    onStatusChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = lek.nazwa,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Button(
                onClick = { onStatusChanged() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (lek.wziety) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = if (lek.wziety) 
                        stringResource(R.string.taken) 
                    else 
                        stringResource(R.string.not_taken)
                )
            }
        }
    }
}