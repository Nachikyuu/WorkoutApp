package com.example.workoutapp.navigation

// definiert die Routen für die verschiedenen Screens

object AppDestinations {
    const val WORKOUT_LIST_SCREEN = "workoutList"
    const val WORKOUT_PLAYER_SCREEN =
        "workoutPlayer/{workoutSetId}" // Mit Argument für die Workout-ID, siehe WorkoutPlayerScreen
    const val WORKOUT_PLAYER_ARG_ID = "workoutSetId"
    const val PROGRESS_SCREEN = "progress"
}