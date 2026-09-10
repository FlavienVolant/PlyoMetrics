package com.example.plyometrics.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import com.example.plyometrics.ui.components.JumpItem
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun JumpsScreen(viewModel: SensorViewModel, onJumpClicked: (AnalyzedJump) -> Unit = {}) {

    val jumps by viewModel.jumps.collectAsState()

    JumpsScreen(jumps, onJumpClicked)
}

@Composable
fun JumpsScreen(jumps: List<AnalyzedJump>, onJumpClicked: (AnalyzedJump) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Jumps",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (jumps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No jumps register")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(jumps) { _, analyzedJump ->
                    JumpItem(
                        analyzedJump = analyzedJump,
                        modifier = Modifier.clickable{
                            onJumpClicked(analyzedJump)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionsScreenPreview() {
    PlyoMetricsTheme {
        JumpsScreen(previewJumps())
    }
}

private fun previewJumps(): List<AnalyzedJump> {
    return listOf(
        AnalyzedJump(
            RawJump(
                points = List(100) { index ->
                    RawSensorPoint(
                        timestamp = index * 5_000_000L,
                        acceleration = Acceleration(
                            x = 0f,
                            y = 0f,
                            z = 9.81f
                        ),
                        rotation = Rotation(
                            qx = 0f,
                            qy = 0f,
                            qz = 0f,
                            qw = 1f
                        )
                    )
                }
            ),
            result = null
        ),
        AnalyzedJump(
            RawJump(
                points = List(250) { index ->
                    RawSensorPoint(
                        timestamp = index * 5_000_000L,
                        acceleration = Acceleration(
                            x = 0f,
                            y = 0f,
                            z = 9.81f + kotlin.math.sin(index * 0.1).toFloat() * 2f
                        ),
                        rotation = Rotation(
                            qx = 0f,
                            qy = 0f,
                            qz = 0f,
                            qw = 1f
                        )
                    )
                }
            ),
            result = JumpResult(0L, 1_000_000L)
        )
    )
}
