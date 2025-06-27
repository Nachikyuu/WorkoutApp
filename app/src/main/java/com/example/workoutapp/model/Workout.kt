package com.example.workoutapp.model

// Repräsentiert eine Abfolge von Übungen
    data class WorkoutSet(
        val id: String,
        val name: String,
        val exercises: List<Exercise>,
        val breakDurationInSeconds: Int // Einheitliche Pause zwischen den Übungen dieses Sets
)