package com.example.plyometrics.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.SensorFrameTransformer
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun JumpDetailsScreen(viewModel: SensorViewModel, modifier: Modifier = Modifier) {
    val rawJump by viewModel.selectedJump.collectAsState()
    val context = LocalContext.current

    if (rawJump != null) {
        JumpDetailsScreen(
            rawJump = rawJump!!,
            onExport = { rawJump: RawJump ->
                val json = viewModel.exportSession(rawJump)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
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
fun JumpDetailsScreen(rawJump: RawJump, onExport: (RawJump) -> Unit, modifier: Modifier = Modifier) {

    val graphColor = MaterialTheme.colorScheme.primary
    val gravityColor = MaterialTheme.colorScheme.secondary
    val axisColor = MaterialTheme.colorScheme.outline
    val zeroColor = MaterialTheme.colorScheme.outlineVariant

    val verticalAccelerationPoints = SensorFrameTransformer().toWorldFrame(rawJump.points)
    val jumpResult = JumpDetector().analyze(rawJump.points)

    Column(modifier = modifier.fillMaxWidth()) {

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {

            if (verticalAccelerationPoints.isEmpty()) {
                return@Canvas
            }

            val leftPadding = 50f
            val rightPadding = 20f
            val topPadding = 20f
            val bottomPadding = 30f

            val graphWidth = size.width - leftPadding - rightPadding
            val graphHeight = size.height - topPadding - bottomPadding

            // Time in s
            val minTime = verticalAccelerationPoints.first().timestamp / 1_000_000_000f
            val maxTime = verticalAccelerationPoints.last().timestamp / 1_000_000_000f

            // Accelerations
            val minAcceleration = minOf(
                verticalAccelerationPoints.minOf { it.value },
                0f
            )

            val maxAcceleration = maxOf(
                verticalAccelerationPoints.maxOf { it.value },
                9.81f
            )

            val timeRange = (maxTime - minTime).coerceAtLeast(0.001f)
            val accelerationRange = (maxAcceleration - minAcceleration).coerceAtLeast(0.001f)

            fun x(timestamp: Long): Float {
                val time = timestamp / 1_000_000_000f

                return leftPadding +
                        ((time - minTime) / timeRange) * graphWidth
            }

            fun y(acceleration: Float): Float {
                return topPadding + graphHeight -
                        ((acceleration - minAcceleration) / accelerationRange) * graphHeight
            }

            /*
             * Flight time area
             *
             * JumpResult stores timestamps in milliseconds,
             * while the graph uses nanoseconds.
             */
            jumpResult?.let { result ->

                val takeOffX =
                    x(result.takeOffTime * 1_000_000L)

                val landingX =
                    x(result.landingTime * 1_000_000L)

                // Background of the flight phase
                drawRect(
                    color = graphColor.copy(alpha = 0.15f),
                    topLeft = Offset(
                        takeOffX,
                        topPadding
                    ),
                    size = Size(
                        width = landingX - takeOffX,
                        height = graphHeight
                    )
                )

                // Take-off line
                drawLine(
                    color = graphColor,
                    start = Offset(
                        takeOffX,
                        topPadding
                    ),
                    end = Offset(
                        takeOffX,
                        topPadding + graphHeight
                    ),
                    strokeWidth = 2f
                )

                // Landing line
                drawLine(
                    color = graphColor,
                    start = Offset(
                        landingX,
                        topPadding
                    ),
                    end = Offset(
                        landingX,
                        topPadding + graphHeight
                    ),
                    strokeWidth = 2f
                )
            }

            // Ligne g
            val gravityY = y(9.81f)

            drawLine(
                color = gravityColor,
                start = Offset(leftPadding, gravityY),
                end = Offset(size.width - rightPadding, gravityY),
                strokeWidth = 1f
            )

            // Ligne zero
            val zeroY = y(0f)

            drawLine(
                color = zeroColor,
                start = Offset(leftPadding, zeroY),
                end = Offset(size.width - rightPadding, zeroY),
                strokeWidth = 1f
            )

            // draw path
            val path = Path()

            verticalAccelerationPoints.forEachIndexed { index, point ->

                val px = x(point.timestamp)
                val py = y(point.value)

                if (index == 0) {
                    path.moveTo(px, py)
                } else {
                    path.lineTo(px, py)
                }
            }

            drawPath(
                color = graphColor,
                path = path,
                style = Stroke(width = 3f)
            )

            // Axe X
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding + graphHeight),
                end = Offset(size.width - rightPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )

            // Axe Y
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information
        if (jumpResult != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Take-off time : ${jumpResult.takeOffTime} ms")
                Text("Landing time : ${jumpResult.landingTime} ms")
                Text("Flight time : ${jumpResult.flightTime} ms")
                Text(
                    "Height : %.1f cm".format(
                        jumpResult.height * 100
                    )
                )
            }
        } else {
            Text(
                text = "Jump not detected",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {onExport(rawJump)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Export Jump")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JumpDetailsScreenPreview() {
    PlyoMetricsTheme {
        JumpDetailsScreen(
            RawJump(
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
            ),
            onExport = {}
        )
    }
}