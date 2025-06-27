package com.example.workoutapp.ui.screens

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onNavigateBack: () -> Unit
    // Hier kommt später das ViewModel für den Fortschritt hinzu
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dein Fortschritt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hier siehst du bald deinen Workout-Fortschritt!")
            // Möglichkeit: Liste der abgeschlossenen Workouts, Diagramme etc.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    ProgressScreen(onNavigateBack = {})
}