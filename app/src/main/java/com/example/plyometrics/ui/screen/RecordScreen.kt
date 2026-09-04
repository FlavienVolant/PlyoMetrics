package com.example.plyometrics.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.ui.components.EmptyState
import com.example.plyometrics.ui.components.ExportButton
import com.example.plyometrics.ui.components.JumpResultCard
import com.example.plyometrics.ui.components.RecordingCard
import com.example.plyometrics.ui.components.StartButton
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun RecordScreen(viewModel: SensorViewModel) {
    val context = LocalContext.current

    val isRunning by viewModel.isRunning.collectAsState()
    val result by viewModel.jumpResult.collectAsState()

    RecordScreen(
        isRunning = isRunning,
        result = result,
        onStartStop = {
            if (isRunning) {
                viewModel.stop()
            } else {
                viewModel.start()
            }
        },
        onExportSession = {
            val json = viewModel.exportSession()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_TEXT, json)
            }

            try {
                context.startActivity(
                    Intent.createChooser(intent, "Export Session")
                )
            }catch (e: ActivityNotFoundException) {
                Log.e("SensorScreen", e.toString())
            }
        }
    )
}

@Composable
private fun RecordScreen(
    isRunning: Boolean,
    result: JumpResult?,
    onStartStop: () -> Unit,
    onExportSession: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when {
            isRunning -> RecordingCard()

            result != null -> {
                JumpResultCard(result)
                Spacer(modifier = Modifier.height(16.dp))
                ExportButton(onClick = onExportSession)
            }

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
        RecordScreen(
            isRunning = false,
            result = null,
            onStartStop = {},
            onExportSession = {}
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
            onExportSession = {}
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
                landingTime = 620
            ),
            onStartStop = {},
            onExportSession = {}
        )
    }
}