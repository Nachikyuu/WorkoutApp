package com.example.workoutapp.viewmodel


//definiert die verschiedenen Zustände, in denen sich der Workout-Player befinden kann
enum class WorkoutState {
    INITIAL,  // Vor Start des Workouts
    EXERCISE, // Während einer Übung
    BREAK,    // Während einer Pause
    PAUSED,   // Workout ist pausiert (optional, wenn später implementiert)
    FINISHED  // Workout ist komplett abgeschlossen
}