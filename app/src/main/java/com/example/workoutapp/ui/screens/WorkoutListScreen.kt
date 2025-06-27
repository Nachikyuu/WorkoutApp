package com.example.workoutapp.ui.screens

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


//Anfangs-Screen, Auswahl des Workouts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    onWorkoutSelected: (String) -> Unit, // Callback mit der WorkoutSet ID
    onShowProgressClicked: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Wähle ein Workout") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Verfügbare Workouts:")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onWorkoutSelected("full_body_beginner") }) {
                Text("Anfänger Ganzkörper")
            }

            Button(onClick = { onWorkoutSelected("hiit_advanced") }) {
                Text("HIIT für Fortgeschrittene")
            }

            Button(onClick = onShowProgressClicked) {
                Text("Fortschritt anzeigen")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutListScreenPreview() {
    WorkoutListScreen(onWorkoutSelected = {}, onShowProgressClicked = {})
}