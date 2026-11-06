package com.example.plyometrics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
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
import com.example.plyometrics.analysis.visualization.SimulationChartPoint
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun SimulationJumpChart(
    points: List<SimulationChartPoint>,
    result: JumpResult?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SimulationLineChart(
            title = "Height",
            unit = "m",
            points = points,
            result = result,
            valueSelector = { it.height }
        )

        SimulationLineChart(
            title = "Vertical velocity",
            unit = "m/s",
            points = points,
            result = result,
            valueSelector = { it.velocity }
        )

        Text(
            text = "Time (s)",
            modifier = Modifier.align(Alignment.End),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SimulationLineChart(
    title: String,
    unit: String,
    points: List<SimulationChartPoint>,
    result: JumpResult?,
    valueSelector: (SimulationChartPoint) -> Float
) {
    val graphColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.outline
    val zeroColor = MaterialTheme.colorScheme.outlineVariant

    Column {
        Text(
            text = "$title ($unit)",
            style = MaterialTheme.typography.titleMedium
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            if (points.isEmpty()) return@Canvas

            val leftPadding = 50f
            val rightPadding = 20f
            val topPadding = 20f
            val bottomPadding = 20f

            val graphWidth = size.width - leftPadding - rightPadding
            val graphHeight = size.height - topPadding - bottomPadding

            val minTimestamp = points.first().timestamp
            val maxTimestamp = points.last().timestamp

            val timeRange = (
                    (maxTimestamp - minTimestamp) / 1_000_000_000f
                    ).coerceAtLeast(0.001f)

            val minValue = minOf(
                points.minOf(valueSelector),
                0f
            )

            val maxValue = maxOf(
                points.maxOf(valueSelector),
                0f
            )

            val valueRange = (maxValue - minValue).coerceAtLeast(0.001f)

            val verticalPadding = valueRange * 0.1f
            val yMin = minValue - verticalPadding
            val yMax = maxValue + verticalPadding
            val yRange = yMax - yMin

            fun x(timestamp: Long): Float {
                val time = (timestamp - minTimestamp) / 1_000_000_000f

                return leftPadding +
                        (time / timeRange) * graphWidth
            }

            fun y(value: Float): Float {
                return topPadding + graphHeight -
                        ((value - yMin) / yRange) * graphHeight
            }

            // Flight phase
            result?.let { jump ->
                val takeOffX = x(jump.takeOffTime)
                    .coerceIn(leftPadding, leftPadding + graphWidth)

                val landingX = x(jump.landingTime)
                    .coerceIn(leftPadding, leftPadding + graphWidth)

                if (landingX > takeOffX) {
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
            }

            // Zero line
            val zeroY = y(0f)

            drawLine(
                color = zeroColor,
                start = Offset(leftPadding, zeroY),
                end = Offset(leftPadding + graphWidth, zeroY),
                strokeWidth = 1f
            )

            // Data path
            val path = Path()

            points.forEachIndexed { index, point ->
                val px = x(point.timestamp)
                val py = y(valueSelector(point))

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

            // X axis
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding + graphHeight),
                end = Offset(leftPadding + graphWidth, topPadding + graphHeight),
                strokeWidth = 2f
            )

            // Y axis
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SimulationJumpChartPreview() {
    val points = List(101) { index ->
        val timestamp = index * 10_000_000L
        val time = index * 0.01f

        val flightTime = time - 0.2f

        val (height, velocity) = when {
            time !in 0.2f..0.8f -> {
                0f to 0f
            }

            else -> {
                val velocity = 2.94f - 9.81f * flightTime
                val height = 2.94f * flightTime -
                        0.5f * 9.81f * flightTime * flightTime

                height to velocity
            }
        }

        SimulationChartPoint(
            timestamp = timestamp,
            height = height,
            velocity = velocity
        )
    }

    val result = JumpResult(
        takeOffTime = 200_000_000L,
        landingTime = 800_000_000L,
        height = 0.44f
    )

    PlyoMetricsTheme {
        SimulationJumpChart(
            points = points,
            result = result,
            modifier = Modifier.padding(16.dp)
        )
    }
}