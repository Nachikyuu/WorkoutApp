package com.example.workoutapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workoutapp.viewmodel.WorkoutState
import com.example.workoutapp.viewmodel.WorkoutViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlayerScreen(
    workoutSetId: String,
    workoutViewModel: WorkoutViewModel = viewModel(), // ViewModel Instanz holen
    onNavigateBack: () -> Unit
) {
    // ViewModel initialisieren, wenn der Screen zum ersten Mal aufgerufen wird
    // oder wenn die workoutSetId sich ändert (sollte hier nicht passieren, da ganze Screen neu)
    // workoutViewModel.loadWorkoutSet(workoutSetId)

    LaunchedEffect(key1 = workoutSetId) {
        workoutViewModel.loadWorkoutSet(workoutSetId)
    }
    val currentExerciseName by workoutViewModel.currentExerciseName.observeAsState("Übung wird geladen...")
    val currentExerciseDescription by workoutViewModel.currentExerciseDescription.observeAsState("")
    val currentExerciseImageResName by workoutViewModel.currentExerciseImageResName.observeAsState(null)
    val timeLeftInSeconds by workoutViewModel.timeLeftInSeconds.observeAsState(0)
    val workoutState by workoutViewModel.workoutState.observeAsState(WorkoutState.INITIAL) // INITIAL, EXERCISE, BREAK, FINISHED
    val progressPercentage by workoutViewModel.progressPercentage.observeAsState(0f)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (workoutState == WorkoutState.BREAK) "Pause" else "Übung") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentExerciseName,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (currentExerciseImageResName != null) {
                    // Finde die Resource ID anhand des Namens.
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val imageResId = try {
                        context.resources.getIdentifier(currentExerciseImageResName, "drawable", context.packageName)
                    } catch (e: Exception) {
                        0 // Fallback, wenn nicht gefunden
                    }
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = currentExerciseName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp), // Höhe anpassen
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("Bild nicht gefunden: $currentExerciseImageResName") // Für Debugging
                        // Image(painter = painterResource(id = R.drawable.placeholder_image), ...) // Fallback
                    }
                } else if (workoutState != WorkoutState.BREAK && workoutState != WorkoutState.INITIAL && workoutState != WorkoutState.FINISHED) {
                    //Platzhalter
                    Box(modifier = Modifier.height(200.dp)) { Text("Kein Bild für diese Übung") }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentExerciseDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$timeLeftInSeconds s",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progressPercentage }, // Compose 1.6.0+
                    // progress = progressPercentage, // Ältere Compose Versionen
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        when (workoutState) {
                            WorkoutState.INITIAL, WorkoutState.FINISHED -> {
                                workoutViewModel.startWorkout(workoutSetId)
                            }
                            WorkoutState.EXERCISE, WorkoutState.BREAK -> {
                                // workoutViewModel.togglePauseResume() // Für Pausenknopf - später
                               workoutViewModel.skipForward()
                            }
                            WorkoutState.PAUSED -> {
                                // workoutViewModel.togglePauseResume() // Für Fortsetzen - später
                            }
                        }
                    },
                    // 'enabled' Logik kann feiner werden, wenn Pause implementiert ist
                    enabled = workoutState == WorkoutState.INITIAL ||
                            workoutState == WorkoutState.FINISHED ||
                            workoutState == WorkoutState.EXERCISE || // Erlaube Skip/Pause während Übung
                            workoutState == WorkoutState.BREAK      // Erlaube Skip/Pause während Pause
                ) {
                    Text( //sich ändernder Text des Buttons
                        when (workoutState) {
                            WorkoutState.INITIAL -> "Workout starten"
                            WorkoutState.EXERCISE -> "Übung läuft... (Überspringen?)" // Beispiel für Skip
                            WorkoutState.BREAK -> "Pause läuft... (Überspringen?)"   // Beispiel für Skip
                            WorkoutState.PAUSED -> "Fortsetzen"
                            WorkoutState.FINISHED -> "Erneut starten"
                        }
                    )
                }
            }
        }
        if (workoutState == WorkoutState.FINISHED) {
            "Glückwunsch!!"
        }
    }
}

//dieser Basis-ViewModel-Typ erbt direkt von androidx.lifecycle.ViewModel
@Preview(showBackground = true)
@Composable
fun WorkoutPlayerScreenPreview() {
    // Diese Preview-Version erbt von ViewModel, nicht von deinem WorkoutViewModel (das AndroidViewModel ist)
    class PreviewWorkoutViewModel : ViewModel() {
        val currentExerciseName = MutableLiveData("Vorschau: Liegestütze")
        val currentExerciseDescription = MutableLiveData("Vorschau: Klassische Liegestütze.")
        val currentExerciseImageResName =
            MutableLiveData<String?>("push_up_image")
        val timeLeftInSeconds = MutableLiveData(25)
        val workoutState = MutableLiveData(WorkoutState.EXERCISE)
        val progressPercentage = MutableLiveData(0.3f)


        fun loadWorkoutSet(workoutSetId: String) {
            // Logik für die Preview, z.B. direkt Werte setzen
            currentExerciseName.value = "Preview: Geladenes Set - Übung 1"
            timeLeftInSeconds.value = 30
            workoutState.value = WorkoutState.INITIAL
        }
        fun startWorkout(workoutSetId: String) {
            currentExerciseName.value = "Preview: Workout gestartet"
            workoutState.value = WorkoutState.EXERCISE
            timeLeftInSeconds.value = 20
        }
        fun pauseWorkout() { /* No-op für Preview oder einfache Logik */ }
        fun resumeWorkout() { /* No-op für Preview oder einfache Logik */ }
        fun skipForward() {
            currentExerciseName.value = "Preview: Nächste Übung"
            workoutState.value = WorkoutState.BREAK
            timeLeftInSeconds.value = 10
        }
        fun togglePauseResume() {} // Implementiere nach Bedarf für die Preview
    }

    WorkoutPlayerScreen(
        workoutSetId = "preview_set_id", // Eine Dummy-ID für die Preview
        workoutViewModel = viewModel(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PreviewWorkoutViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PreviewWorkoutViewModel() as T
                }
                // Für den Fall, dass das echte WorkoutViewModel hier angefordert wird
                throw IllegalArgumentException("Unknown ViewModel class in Preview factory: ${modelClass.name}. Expected PreviewWorkoutViewModel.")
            }
        }),
        onNavigateBack = {}
    )
}