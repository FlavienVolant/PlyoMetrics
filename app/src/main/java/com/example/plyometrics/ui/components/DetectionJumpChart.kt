package com.example.plyometrics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.visualization.DetectionChartPoint

@Composable
fun DetectionJumpChart(points: List<DetectionChartPoint>, result: JumpResult?, modifier: Modifier = Modifier) {
    val graphColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.outline
    val zeroColor = MaterialTheme.colorScheme.outlineVariant

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Vertical acceleration",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Acceleration (m/s²)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            if (points.isEmpty()) return@Canvas

            val leftPadding = 50f
            val rightPadding = 20f
            val topPadding = 20f
            val bottomPadding = 30f

            val graphWidth = size.width - leftPadding - rightPadding
            val graphHeight = size.height - topPadding - bottomPadding

            // Use relative timestamps to avoid precision issues with large nanosecond values.
            val minTimestamp = points.minOf { it.timestamp }
            val maxTimestamp = points.maxOf { it.timestamp }

            val timeRange = (
                    (maxTimestamp - minTimestamp) / 1_000_000_000f
                    ).coerceAtLeast(0.001f)

            val minAcceleration = minOf(
                points.minOf { it.acceleration },
                0f
            )
            val maxAcceleration = maxOf(
                points.maxOf { it.acceleration },
                0f
            )

            val accelerationRange = (
                    maxAcceleration - minAcceleration
                    ).coerceAtLeast(0.001f)

            // Add a little vertical space around the curve.
            val verticalPadding = accelerationRange * 0.1f
            val yMin = minAcceleration - verticalPadding
            val yMax = maxAcceleration + verticalPadding
            val yRange = yMax - yMin

            fun x(timestamp: Long): Float {
                val time = (timestamp - minTimestamp) / 1_000_000_000f
                return leftPadding + (time / timeRange) * graphWidth
            }

            fun y(acceleration: Float): Float {
                return topPadding + graphHeight -
                        ((acceleration - yMin) / yRange) * graphHeight
            }

            result?.let { jump ->
                val takeOffX = x(jump.takeOffTime)
                val landingX = x(jump.landingTime)

                // Background of the flight phase
                drawRect(
                    color = graphColor.copy(alpha = 0.15f),
                    topLeft = Offset(takeOffX, topPadding),
                    size = Size(
                        width = landingX - takeOffX,
                        height = graphHeight
                    )
                )

                // Take-off line
                drawLine(
                    color = graphColor,
                    start = Offset(takeOffX, topPadding),
                    end = Offset(takeOffX, topPadding + graphHeight),
                    strokeWidth = 2f
                )

                // Landing line
                drawLine(
                    color = graphColor,
                    start = Offset(landingX, topPadding),
                    end = Offset(landingX, topPadding + graphHeight),
                    strokeWidth = 2f
                )
            }

            // Zero acceleration reference line.
            val zeroY = y(0f)

            drawLine(
                color = zeroColor,
                start = Offset(leftPadding, zeroY),
                end = Offset(size.width - rightPadding, zeroY),
                strokeWidth = 1f
            )

            // Draw acceleration curve.
            val path = Path()

            points.forEachIndexed { index, point ->
                val px = x(point.timestamp)
                val py = y(point.acceleration)

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

            // X axis.
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding + graphHeight),
                end = Offset(size.width - rightPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )

            // Y axis.
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )
        }

        Text(
            text = "Time (s)",
            modifier = Modifier.align(Alignment.End),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetectionJumpChartPreview() {
    val points = List(101) { index ->
        val time = index * 10_000_000L // 10 ms

        val acceleration = when (index) {
            in 0..15 -> index * 0.8f
            in 16..65 -> 0.5f
            in 66..75 -> 15f - (index - 66) * 1.5f
            else -> -5f
        }

        DetectionChartPoint(
            timestamp = time,
            acceleration = acceleration
        )
    }

    val result = JumpResult(
        takeOffTime = 300_000_000L,
        landingTime = 700_000_000L,
        height = 0.35f
    )

    MaterialTheme {
        DetectionJumpChart(
            points = points,
            result = result,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetectionJumpChartEmptyPreview() {
    MaterialTheme {
        DetectionJumpChart(
            points = emptyList(),
            result = null,
            modifier = Modifier.padding(16.dp)
        )
    }
}
