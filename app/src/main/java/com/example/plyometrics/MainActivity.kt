package com.example.plyometrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.plyometrics.ui.screen.SensorScreen
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

class MainActivity : ComponentActivity() {

    private val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlyoMetricsTheme {
                SensorScreen(sensorViewModel)
            }
        }
    }
}
