package com.example.workoutapp.data

import com.example.workoutapp.model.Exercise
import com.example.workoutapp.model.WorkoutSet

// ausgelagerte Workout-Sets
object WorkoutDataRepository {
    private val exercisesSet1 = listOf(
        Exercise(101, "Jumping Jacks", "Ganzkörper-Aufwärmübung.", 30, "jumping_jacks_image"),
        Exercise(102, "Liegestütze", "Klassische Liegestütze.", 20, "push_up_image"),
        Exercise(103, "Kniebeugen", "Tiefe Kniebeugen mit Fokus auf Form.", 45, "squats_image"),
        Exercise(104, "Plank", "Unterarmstütz halten.", 30, "plank_image"),
        Exercise(105, "Ausfallschritte", "Pro Bein abwechselnd.", 40, "lunges_image")
    )

    private val exercisesSet2 = listOf(
        Exercise(201, "Burpees", "Herausfordernde Ganzkörperübung.", 45, "burpees_image"),
        Exercise(202, "Mountain Climbers", "Kardio und Core.", 30, "mountain_climbers_image"),
        Exercise(203, "Crunches", "Bauchmuskelübung.", 30, "crunches_image")
    )

    val workoutSets = mapOf(
        "full_body_beginner" to WorkoutSet(
            "full_body_beginner",
            "Anfänger Ganzkörper",
            exercisesSet1,
            15 // 15 Sekunden Pause
        ),
        "hiit_advanced" to WorkoutSet(
            "hiit_advanced",
            "HIIT für Fortgeschrittene",
            exercisesSet2,
            10 // 10 Sekunden Pause
        )
    )


    //Lädt das WorkoutSet aus dem WorkoutDataRepository
    //Setzt currentExercises, currentExerciseIndex und exercisesCompletedInSet zurück
    // Ruft updateUIForCurrentPhase(false) auf, um die UI mit den Daten der ersten Übung zu initialisieren
    fun getWorkoutSetById(id: String): WorkoutSet? {
        return workoutSets[id]
    }
}