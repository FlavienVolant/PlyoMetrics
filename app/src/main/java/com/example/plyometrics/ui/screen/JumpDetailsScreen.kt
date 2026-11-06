package com.example.plyometrics.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.visualization.DetectionChartPoint
import com.example.plyometrics.analysis.visualization.JumpChartData
import com.example.plyometrics.measurement.RawJump
import com.example.plyometrics.measurement.RawSensorPoint
import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Rotation
import com.example.plyometrics.ui.components.DetectionJumpChart
import com.example.plyometrics.ui.components.SimulationJumpChart
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun JumpDetailsScreen(viewModel: SensorViewModel, modifier: Modifier = Modifier) {
    val analyzedJump by viewModel.selectedJump.collectAsState()
    val context = LocalContext.current

    if (analyzedJump != null) {
        JumpDetailsScreen(
            analyzedJump = analyzedJump!!,
            onExport = { analyzedJump: AnalyzedJump ->
                val json = viewModel.exportSession(analyzedJump.rawJump)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TEXT, json)
                }

                try {
                    context.startActivity(
                        Intent.createChooser(intent, "Export Session")
                    )
                } catch (e: ActivityNotFoundException) {
                    Log.e("JumpDetailsScreen", e.toString())
                }
            },
            onDelete = viewModel::deleteJump,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No jump selected",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun JumpDetailsScreen(
    analyzedJump: AnalyzedJump,
    onExport: (AnalyzedJump) -> Unit,
    onDelete: (RawJump) -> Unit,
    modifier: Modifier = Modifier)
{

    val chartData = analyzedJump.chartData
    val result = analyzedJump.result

    Column(modifier = modifier.fillMaxWidth()) {

        when(chartData) {
            is JumpChartData.Detection -> {
                DetectionJumpChart(
                    points = chartData.points,
                    result = analyzedJump.result,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            is JumpChartData.Simulation -> {
                SimulationJumpChart(
                    points = chartData.points,
                    result = analyzedJump.result,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            null -> {
                Text("No chart data available", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (result != null) {
            val recordingStartTime =
                analyzedJump.rawJump.points.firstOrNull()?.timestamp ?: 0L

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Take-off time: ${
                        (result.takeOffTime - recordingStartTime) / 1_000_000
                    } ms"
                )

                Text(
                    "Landing time: ${
                        (result.landingTime - recordingStartTime) / 1_000_000
                    } ms"
                )

                Text("Flight time: ${result.flightTime / 1_000_000} ms")

                Text("Height: %.2f cm".format(result.height * 100))
            }
        } else {
            Text(
                text = "Jump not detected",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onExport(analyzedJump) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Export Jump")
            }

            IconButton(
                onClick = { onDelete(analyzedJump.rawJump) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete jump"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JumpDetailsScreenPreview() {
    PlyoMetricsTheme {
        val rawJump = RawJump(
            points = List(200) { index ->
                val acceleration = when (index) {
                    in 0..39 -> 9.81f
                    in 40..59 -> 9.81f + (index - 40) * 0.8f
                    in 60..69 -> 25.8f - (index - 60) * 1.6f
                    in 70..119 -> 0.2f
                    in 120..129 -> 0.2f + (index - 120) * 2.5f
                    in 130..159 -> 22f - (index - 130) * 0.4f
                    else -> 9.81f
                }

                RawSensorPoint(
                    timestamp = index * 10_000_000L,
                    acceleration = Acceleration(
                        x = 0f,
                        y = 0f,
                        z = acceleration
                    ),
                    rotation = Rotation(
                        qx = 0f,
                        qy = 0f,
                        qz = 0f,
                        qw = 1f
                    )
                )
            }
        )

        val chartPoints = List(200) { index ->
            val acceleration = when (index) {
                in 0..39 -> 0.1f
                in 40..59 -> (index - 40) * 0.8f
                in 60..69 -> 15f - (index - 60) * 1.5f
                in 70..119 -> 0.2f
                in 120..129 -> 0.2f + (index - 120) * 2.5f
                in 130..159 -> 22f - (index - 130) * 0.4f
                else -> 0f
            }

            DetectionChartPoint(
                timestamp = index * 10_000_000L,
                acceleration = acceleration
            )
        }

        val analyzedJump = AnalyzedJump(
            rawJump = rawJump,
            result = JumpResult(
                takeOffTime = 400_000_000L,
                landingTime = 1_200_000_000L,
                height = 0.5f
            ),
            chartData = JumpChartData.Detection(chartPoints)
        )

        JumpDetailsScreen(
            analyzedJump = analyzedJump,
            onExport = {},
            onDelete = {}
        )
    }
}