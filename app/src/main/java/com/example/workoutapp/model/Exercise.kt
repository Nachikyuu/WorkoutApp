package com.example.workoutapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises") // Für Room, damit Übungen in Datenbank gespeichert werden
data class Exercise(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val durationInSeconds: Int,
    val imageResName: String? = null // Name der Bildressource im drawable Ordner
    // oder imageUrl: String? = null // falls Bilder aus dem Netz geladen werden
)