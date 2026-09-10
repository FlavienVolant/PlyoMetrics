package com.example.plyometrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plyometrics.ui.screen.JumpDetailsScreen
import com.example.plyometrics.ui.screen.JumpsHistoryScreen
import com.example.plyometrics.ui.screen.JumpsScreen
import com.example.plyometrics.ui.screen.RecordScreen
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

class MainActivity : ComponentActivity() {

    private val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlyoMetricsTheme {
                AppNavigation(sensorViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: SensorViewModel) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == "record",
                    onClick = {
                        navController.navigate("record") {
                            popUpTo("record") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Record"
                        )
                    },
                    label = {
                        Text("Record")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "jumps" || currentRoute == "details",
                    onClick = {
                        navController.navigate("jumps") {
                            popUpTo("jumps") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Jumps"
                        )
                    },
                    label = {
                        Text("Jumps")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = {
                        navController.navigate("history") {
                            popUpTo("history") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "History")
                    },
                    label = {
                        Text("History")
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "record",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("record") {
                RecordScreen(viewModel)
            }

            composable("jumps") {
                JumpsScreen(
                    viewModel,
                    onJumpClicked = { analyzedJump ->
                        viewModel.selectedJump(analyzedJump)
                        navController.navigate("details")
                    }
                )
            }

            composable("details") {
                JumpDetailsScreen(viewModel)
            }

            composable("history") {
                JumpsHistoryScreen(viewModel)
            }
        }
    }
}
