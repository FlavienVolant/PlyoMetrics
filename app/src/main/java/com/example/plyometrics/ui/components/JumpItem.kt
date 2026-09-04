package com.example.plyometrics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JumpItem(rawJump: RawJump, modifier: Modifier = Modifier) {

    val jumpResult = JumpDetector().analyze(rawJump.points)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 12.dp,
                horizontal = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(rawJump.date),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = String.format(Locale.getDefault(), "%.2f m",jumpResult?.height),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JumpItemPreview() {
    PlyoMetricsTheme {
        JumpItem(
            rawJump = RawJump(
                points = List(200) { index ->

                    val time = index * 5_000_000L // 5 ms

                    val acceleration = when (index) {
                        in 0..39 -> {
                            9.81f
                        }

                        in 40..59 -> {
                            9.81f + (index - 40) * 0.8f
                        }

                        in 60..69 -> {
                            25.8f - (index - 60) * 1.6f
                        }

                        in 70..119 -> {
                            0.2f
                        }

                        in 120..129 -> {
                            0.2f + (index - 120) * 2.5f
                        }

                        in 130..159 -> {
                            22f - (index - 130) * 0.4f
                        }

                        else -> {
                            9.81f
                        }
                    }

                    RawSensorPoint(
                        timestamp = time,
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
        )
    }
}