package com.example.workoutapp.viewmodel

import android.app.Application
import android.media.MediaPlayer // Für Soundeffekte
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.workoutapp.R // Für Sound-Ressourcen
import com.example.workoutapp.model.Exercise
import com.example.workoutapp.model.WorkoutSet
import com.example.workoutapp.data.WorkoutDataRepository
// Für weitere Implementation: import kotlinx.coroutines.launch (für DB-Operationen)


open class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentWorkoutSet = MutableLiveData<WorkoutSet?>()
    // val currentWorkoutSet: LiveData<WorkoutSet?> = _currentWorkoutSet // Optional, wenn UI es braucht

    private var currentExercises: List<Exercise> = emptyList()
    private var currentExerciseIndex = 0
    private var exercisesCompletedInSet = 0


    private val _currentExerciseName = MutableLiveData("Initialisiere...")
    open val currentExerciseName: LiveData<String> = _currentExerciseName

    private val _currentExerciseDescription = MutableLiveData("")
    open val currentExerciseDescription: LiveData<String> = _currentExerciseDescription

    private val _currentExerciseImageResName = MutableLiveData<String?>(null)

    open val currentExerciseImageResName: LiveData<String?> = _currentExerciseImageResName


    private val _timeLeftInSeconds = MutableLiveData(0)
    open val timeLeftInSeconds: LiveData<Int> = _timeLeftInSeconds

    private val _workoutState = MutableLiveData(WorkoutState.INITIAL)
    open val workoutState: LiveData<WorkoutState> = _workoutState

    private val _progressPercentage = MutableLiveData(0f) // 0.0f bis 1.0f
    open val progressPercentage: LiveData<Float> = _progressPercentage

    private var timer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null


    open fun loadWorkoutSet(workoutSetId: String) {
        val loadedSet = WorkoutDataRepository.getWorkoutSetById(workoutSetId) // Lade das Set
        _currentWorkoutSet.value = loadedSet

        if (loadedSet == null) {
            _currentExerciseName.value = "Workout nicht gefunden!"
            _workoutState.value = WorkoutState.FINISHED // Oder ein Fehlerstatus
            return
        }

        currentExercises = loadedSet.exercises
        exercisesCompletedInSet = 0
        currentExerciseIndex = 0
        _workoutState.value = WorkoutState.INITIAL
        updateUIForCurrentPhase(false) // Initiales Setup der UI für die erste Übung
    }

    private fun updateUIForCurrentPhase(isBreak: Boolean) {
        if (currentExerciseIndex >= currentExercises.size && !isBreak) { // Kein !isBreak bei finish
            finishWorkout()
            return
        }

        if (isBreak) {
            val breakDuration = _currentWorkoutSet.value?.breakDurationInSeconds ?: 15
            _currentExerciseName.value = "Pause"
            _currentExerciseDescription.value = "Nächste Übung: ${currentExercises.getOrNull(currentExerciseIndex)?.name ?: ""}"
            _currentExerciseImageResName.value = null
            _timeLeftInSeconds.value = breakDuration
            _workoutState.value = WorkoutState.BREAK
        } else {
            if (currentExercises.isEmpty()) {
                finishWorkout()
                return
            }
            val exercise = currentExercises[currentExerciseIndex]
            _currentExerciseName.value = exercise.name
            _currentExerciseDescription.value = exercise.description
            _currentExerciseImageResName.value = exercise.imageResName
            _timeLeftInSeconds.value = exercise.durationInSeconds
            _workoutState.value = WorkoutState.EXERCISE
        }
        // Fortschrittsbalken aktualisieren
        _progressPercentage.value = if (currentExercises.isNotEmpty()) {
            exercisesCompletedInSet.toFloat() / currentExercises.size.toFloat()
        } else {
            0f
        }
    }


    open fun startWorkout(workoutSetId: String) {
        // Wenn es ein Neustart ist oder das Set noch nicht geladen/geändert wurde
        if (_workoutState.value == WorkoutState.INITIAL ||
            _workoutState.value == WorkoutState.FINISHED ||
            _currentWorkoutSet.value?.id != workoutSetId) {
            loadWorkoutSet(workoutSetId)
        }

        if (_currentWorkoutSet.value == null || currentExercises.isEmpty()) {
            _currentExerciseName.value = "Workout kann nicht gestartet werden."
            _workoutState.value = WorkoutState.FINISHED
            return
        }
        // Stelle sicher, dass der Index und Zähler für den Start zurückgesetzt sind
        // currentExerciseIndex = 0 // Wird schon in loadWorkoutSet gemacht
        // exercisesCompletedInSet = 0 // Wird schon in loadWorkoutSet gemacht

        // Direkt mit der ersten Übung starten (oder der aktuellen, falls pausiert - später)
        startExercisePhase()
    }


    private fun startNextPhase() {
        // Wenn die vorherige Phase eine Übung war und es nicht die letzte Übung ist
        if (_workoutState.value == WorkoutState.EXERCISE && currentExerciseIndex < currentExercises.size) {
            startBreakPhase()
        } else { // Ansonsten (INITIAL, BREAK, oder nach der letzten Übung) starte die nächste Übung oder beende
            if (currentExerciseIndex >= currentExercises.size) {
                finishWorkout()
            } else {
                startExercisePhase()
            }
        }
    }

    private fun startExercisePhase() {
        if (currentExerciseIndex >= currentExercises.size) {
            finishWorkout()
            return
        }
        playSound(R.raw.exercise_start_sound) // Sound für Übungsstart
        updateUIForCurrentPhase(isBreak = false)
        val exercise = currentExercises[currentExerciseIndex]

        timer?.cancel()
        timer = object : CountDownTimer(exercise.durationInSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                playSound(R.raw.exercise_end_sound) // Sound für Übungsende
                exercisesCompletedInSet++
                currentExerciseIndex++
                startNextPhase()
            }
        }.start()
    }

    private fun startBreakPhase() {
        // Nur eine Pause starten, wenn es noch Übungen gibt
        if (currentExerciseIndex >= currentExercises.size) {
            finishWorkout() // Sollte nicht passieren, wenn Logik korrekt ist
            return
        }
        playSound(R.raw.break_start_sound) // Sound für Pausenstart
        updateUIForCurrentPhase(isBreak = true)
        val breakDuration = _currentWorkoutSet.value?.breakDurationInSeconds ?: 15

        timer?.cancel()
        timer = object : CountDownTimer(breakDuration * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                playSound(R.raw.break_end_sound) // Sound für Pausenende
                // Nicht currentExerciseIndex++ hier, das passiert vor dem nächsten startExercisePhase
                startNextPhase() // Starte die nächste Übung
            }
        }.start()
    }

    private fun finishWorkout() {
        _workoutState.value = WorkoutState.FINISHED
        _currentExerciseName.value = "Workout abgeschlossen!"
        _currentExerciseDescription.value = "Gut gemacht!"
        _progressPercentage.value = 1f
        _timeLeftInSeconds.value = 0
        timer?.cancel()
        // TODO: Fortschritt hier speichern (Phase 3)
        // viewModelScope.launch {
        //    repository.saveWorkoutProgress(...)
        // }
    }

    open fun skipForward() {
        timer?.cancel() // Aktuellen Timer immer stoppen

        when (_workoutState.value) {
            WorkoutState.EXERCISE -> {
                // Wenn wir eine Übung überspringen
                playSound(R.raw.exercise_end_sound) // Simuliere das normale Ende der Übung
                exercisesCompletedInSet++ // Zähle die übersprungene Übung als abgeschlossen
                currentExerciseIndex++    // Gehe zum Index der nächsten Phase (Pause oder nächste Übung)
                startNextPhase()          // Starte die nächste logische Phase (Pause oder nächste Übung/Finish)
            }
            WorkoutState.BREAK -> {
                // Wenn wir eine Pause überspringen
                playSound(R.raw.break_end_sound) // Simuliere das normale Ende der Pause
                // Der currentExerciseIndex wurde bereits nach der vorherigen Übung erhöht.
                // Direkt zur nächsten Übung.
                startNextPhase() // Starte die nächste Übung (oder beende, falls es die letzte war)
            }
            WorkoutState.INITIAL -> {
                // Wenn das Workout noch nicht gestartet wurde und man "skip" drückt,
                // könnte man es als "starte mit der zweiten Übung" interpretieren,
                // oder einfach das Workout normal starten.
                // Für Einfachheit starten wir es normal oder machen nichts.
                // Alternativ:
                // if (currentExercises.isNotEmpty()) {
                //     currentExerciseIndex = 0 // Stelle sicher, dass wir am Anfang sind
                //     startWorkout(_currentWorkoutSet.value?.id ?: "") // Starte das Workout
                // }
                // Oder spezifischer, direkt die erste Übung überspringen:
                // if (_currentWorkoutSet.value != null && currentExercises.isNotEmpty()) {
                //    exercisesCompletedInSet = 1 // Erste Übung als "erledigt" markieren
                //    currentExerciseIndex = 1    // Zum Index der zweiten Übung
                //    startNextPhase()
                // }
                // Für den Moment: Keine Aktion bei INITIAL, da "Start Workout" passender ist.
            }
            WorkoutState.PAUSED -> {
                // Was soll passieren, wenn pausiert ist und geskippt wird?
                // Option 1: Fehler/Keine Aktion.
                // Option 2: Behandle es, als wäre die aktuelle Phase (vor der Pause) übersprungen.
                // Das würde mehr Zustandsmanagement erfordern (welche Phase war vor der Pause aktiv?).
                // Für den Moment: Keine spezielle Aktion, oder behandle es wie einen normalen Skip der zugrundeliegenden Phase
                // (benötigt Info über die Phase vor der Pause).
            }
            WorkoutState.FINISHED -> {
                // Keine Aktion, wenn das Workout bereits beendet ist.
            }
            null -> {
                // Sollte nicht passieren
            }
        }
    }

    // Pausenlogik ist noch nicht implementiert
    // private var timeRemainingWhenPaused: Long = 0
    open fun togglePauseResume() {
        when (_workoutState.value) {
            WorkoutState.EXERCISE, WorkoutState.BREAK -> {
                // timer?.cancel()
                // timeRemainingWhenPaused = _timeLeftInSeconds.value?.toLong() ?: 0
                // _workoutState.value = WorkoutState.PAUSED
                // TODO: Timer anhalten und Zustand für Fortsetzung speichern
                // Diese Funktion ist noch nicht implementiert
            }
            WorkoutState.PAUSED -> {
                // TODO: Timer fortsetzen mit timeRemainingWhenPaused
                // if (war_vorher_exercise) startExerciseTimer(timeRemainingWhenPaused)
                // else startBreakTimer(timeRemainingWhenPaused)
            }
            else -> { /* Tue nichts */ }
        }
    }


    private fun playSound(soundResId: Int) {
        try {
            mediaPlayer?.release() // Vorherigen Player freigeben
            mediaPlayer = MediaPlayer.create(getApplication(), soundResId)
            mediaPlayer?.setOnCompletionListener { mp -> mp.release() } // Freigeben nach Abspielen
            mediaPlayer?.start()
        } catch (e: Exception) {
            // Fehler beim Abspielen des Sounds protokollieren
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}