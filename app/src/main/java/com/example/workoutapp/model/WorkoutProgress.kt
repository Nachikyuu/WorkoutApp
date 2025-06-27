package com.example.workoutapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// Für die Room Datenbank, um den Fortschritt zu speichern

@Entity(tableName = "workout_progress")
data class WorkoutProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutSetId: String, // ID des WorkoutSets, das gemacht wurde
    val dateCompleted: Long, // Zeitstempel des Abschlusses
    val exercisesCompleted: Int,
    val totalExercises: Int
)