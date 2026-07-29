package com.example.plyometrics.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.ui.components.EmptyState
import com.example.plyometrics.ui.components.JumpResultCard
import com.example.plyometrics.ui.components.RecordingCard
import com.example.plyometrics.ui.components.StartButton
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun SensorScreen(viewModel: SensorViewModel) {
    val isRunning by viewModel.isRunning.collectAsState()
    val result by viewModel.jumpResult.collectAsState()

    SensorScreen(
        isRunning = isRunning,
        result = result,
        onStartStop = {
            if (isRunning) {
                viewModel.stop()
            } else {
                viewModel.start()
            }
        }
    )
}

@Composable
private fun SensorScreen(isRunning: Boolean, result: JumpResult?, onStartStop: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when {
            isRunning -> RecordingCard()

            result != null -> JumpResultCard(result)

            else -> EmptyState()
        }

        Spacer(modifier = Modifier.height(32.dp))

        StartButton(
            isRunning = isRunning,
            onClick = onStartStop
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenReadyNotRunningPreview() {
    PlyoMetricsTheme {
        SensorScreen(
            isRunning = false,
            result = null,
            onStartStop = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenReadyRunningPreview() {
    PlyoMetricsTheme {
        SensorScreen(
            isRunning = true,
            result = null,
            onStartStop = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenReadyWithResultPreview() {
    PlyoMetricsTheme {
        SensorScreen(
            isRunning = false,
            result = JumpResult(
                takeOffTime = 0,
                landingTime = 620
            ),
            onStartStop = {}
        )
    }
}