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
import com.example.plyometrics.analysis.JumpAnalysisMode
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.ui.components.AnalysisModeSelector
import com.example.plyometrics.ui.components.EmptyState
import com.example.plyometrics.ui.components.JumpResultCard
import com.example.plyometrics.ui.components.RecordingCard
import com.example.plyometrics.ui.components.StartButton
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun RecordScreen(viewModel: SensorViewModel) {
    val isRunning by viewModel.isRunning.collectAsState()
    val result by viewModel.jumpResult.collectAsState()
    val analysisMode by viewModel.analysisMode.collectAsState()

    RecordScreen(
        isRunning = isRunning,
        result = result,
        analysisMode = analysisMode,
        onAnalysisModeChange = viewModel::selectAnalysisMode,
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
private fun RecordScreen(
    isRunning: Boolean,
    result: JumpResult?,
    onStartStop: () -> Unit,
    analysisMode: JumpAnalysisMode,
    onAnalysisModeChange: (JumpAnalysisMode) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when {
            isRunning -> RecordingCard()
            result != null -> {JumpResultCard(result)}
            else -> EmptyState()
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnalysisModeSelector(
            selectedMode = analysisMode,
            enabled = !isRunning,
            onModeChange = onAnalysisModeChange
        )

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
        RecordScreen(
            isRunning = false,
            result = null,
            onStartStop = {},
            analysisMode = JumpAnalysisMode.SIMULATION,
            onAnalysisModeChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenReadyRunningPreview() {
    PlyoMetricsTheme {
        RecordScreen(
            isRunning = true,
            result = null,
            onStartStop = {},
            analysisMode = JumpAnalysisMode.SIMULATION,
            onAnalysisModeChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenReadyWithResultPreview() {
    PlyoMetricsTheme {
        RecordScreen(
            isRunning = false,
            result = JumpResult(
                takeOffTime = 0,
                landingTime = 620,
                height = 5.5f,
            ),
            onStartStop = {},
            analysisMode = JumpAnalysisMode.SIMULATION,
            onAnalysisModeChange = {}
        )
    }
}