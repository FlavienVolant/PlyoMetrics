package com.example.plyometrics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

@Composable
fun SessionItem(rawJump: RawJump) {

    val graphColor = MaterialTheme.colorScheme.primary
    val gravityColor = MaterialTheme.colorScheme.secondary
    val axisColor = MaterialTheme.colorScheme.outline
    val zeroColor = MaterialTheme.colorScheme.outlineVariant

    val verticalAccelerationPoints = SensorFrameTransformer().toWorldFrame(rawJump.points)

    Canvas(
        modifier = Modifier
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

        // Courbe
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
fun SessionItemPreview() {
    PlyoMetricsTheme {
        SessionItem(
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