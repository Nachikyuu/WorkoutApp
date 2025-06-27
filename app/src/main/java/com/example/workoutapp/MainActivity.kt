package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workoutapp.navigation.AppDestinations
import com.example.workoutapp.ui.screens.ProgressScreen
import com.example.workoutapp.ui.screens.WorkoutListScreen
import com.example.workoutapp.ui.screens.WorkoutPlayerScreen
import com.example.workoutapp.ui.theme.WorkoutAppTheme // Dein Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Navigation durch die Screens
                    WorkoutAppNavigation()
                }
            }
        }
    }
}

@Composable
fun WorkoutAppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.WORKOUT_LIST_SCREEN
    ) {
        composable(AppDestinations.WORKOUT_LIST_SCREEN) {
            WorkoutListScreen(
                onWorkoutSelected = { workoutSetId ->
                    navController.navigate(
                        AppDestinations.WORKOUT_PLAYER_SCREEN.replace(
                            "{${AppDestinations.WORKOUT_PLAYER_ARG_ID}}",
                            workoutSetId
                        )
                    )
                },
                onShowProgressClicked = {
                    navController.navigate(AppDestinations.PROGRESS_SCREEN)
                }
            )
        }

        composable(
            route = AppDestinations.WORKOUT_PLAYER_SCREEN,
            arguments = listOf(navArgument(AppDestinations.WORKOUT_PLAYER_ARG_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutSetId = backStackEntry.arguments?.getString(AppDestinations.WORKOUT_PLAYER_ARG_ID)
            requireNotNull(workoutSetId) { "workoutSetId parameter wasn't found. Please make sure it's set!" }
            WorkoutPlayerScreen(
                workoutSetId = workoutSetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.PROGRESS_SCREEN) {
            ProgressScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}