package com.example.plyometrics.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.SensorFrameTransformer
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun JumpDetailsScreen(
    viewModel: SensorViewModel,
    modifier: Modifier = Modifier
) {
    val rawJump by viewModel.selectedJump.collectAsState()

    if (rawJump != null) {
        JumpDetailsScreen(
            rawJump = rawJump!!,
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
fun JumpDetailsScreen(rawJump: RawJump, modifier: Modifier = Modifier) {

    val graphColor = MaterialTheme.colorScheme.primary
    val gravityColor = MaterialTheme.colorScheme.secondary
    val axisColor = MaterialTheme.colorScheme.outline
    val zeroColor = MaterialTheme.colorScheme.outlineVariant

    val verticalAccelerationPoints = SensorFrameTransformer().toWorldFrame(rawJump.points)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val leftPadding = 50f
        val rightPadding = 20f
        val topPadding = 20f
        val bottomPadding = 30f

        val graphWidth = size.width - leftPadding - rightPadding
        val graphHeight = size.height - topPadding - bottomPadding

        // Temps en secondes
        val minTime = verticalAccelerationPoints.first().timestamp / 1_000_000_000f
        val maxTime = verticalAccelerationPoints.last().timestamp / 1_000_000_000f

        // Accélérations
        val minAcceleration = minOf(
            verticalAccelerationPoints.minOf { it.value },
            0f
        )

        val maxAcceleration = maxOf(
            verticalAccelerationPoints.maxOf { it.value },
            9.81f
        )

        val timeRange = (maxTime - minTime).coerceAtLeast(0.001f)
        val accelerationRange =
            (maxAcceleration - minAcceleration).coerceAtLeast(0.001f)

        fun x(timestamp: Long): Float {
            val time = timestamp / 1_000_000_000f

            return leftPadding +
                    ((time - minTime) / timeRange) * graphWidth
        }

        fun y(acceleration: Float): Float {
            return topPadding +
                    graphHeight -
                    ((acceleration - minAcceleration) /
                            accelerationRange) * graphHeight
        }

        // Ligne g
        val gravityY = y(9.81f)

        drawLine(
            color = gravityColor,
            start = Offset(leftPadding, gravityY),
            end = Offset(size.width - rightPadding, gravityY),
            strokeWidth = 1f
        )

        // Ligne zéro
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

}

@Preview(showBackground = true)
@Composable
fun JumpDetailsScreenPreview() {
    PlyoMetricsTheme {
        JumpDetailsScreen(
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
            )
        )
    }
}