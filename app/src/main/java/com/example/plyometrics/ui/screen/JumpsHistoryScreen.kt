package com.example.plyometrics.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel
import java.util.Date

@Composable
fun JumpsHistoryScreen(viewModel: SensorViewModel, modifier: Modifier = Modifier) {

    val jumps by viewModel.jumps.collectAsState()

    JumpsHistoryScreen(jumps, modifier)
}

@Composable
fun JumpsHistoryScreen(jumps: List<AnalyzedJump>, modifier: Modifier = Modifier) {


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

            val graphColor = MaterialTheme.colorScheme.primary
            val axisColor = MaterialTheme.colorScheme.outline
            val gridColor = MaterialTheme.colorScheme.outlineVariant

            Canvas(
                modifier = modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {

                val validJumps = jumps.mapNotNull { jump ->
                    jump.result?.let { result ->
                        jump to result
                    }
                }

                if (validJumps.isEmpty())
                    return@Canvas

                val leftPadding = 50f
                val rightPadding = 20f
                val topPadding = 20f
                val bottomPadding = 30f

                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding

                val heights = validJumps.map { (_, result) ->
                    result.height
                }

                val minHeight = 0.0
                val maxHeight = heights.max()

                val heightRange =
                    (maxHeight - minHeight).coerceAtLeast(0.001)

                val minDate = validJumps.minOf { it.first.rawJump.date.time }
                val maxDate = validJumps.maxOf { it.first.rawJump.date.time }

                val dateRange =
                    (maxDate - minDate).coerceAtLeast(1L)

                fun x(date: Date): Float {
                    return leftPadding +
                            ((date.time - minDate).toFloat() / dateRange) *
                            graphWidth
                }

                fun y(height: Double): Double {
                    return topPadding + graphHeight -
                            ((height - minHeight) / heightRange) *
                            graphHeight
                }

                // Horizontal grid lines
                val numberOfGridLines = 5

                for (i in 0..numberOfGridLines) {
                    val value =
                        minHeight +
                                (maxHeight - minHeight) *
                                i / numberOfGridLines

                    val y = y(value).toFloat()

                    drawLine(
                        color = gridColor,
                        start = Offset(leftPadding, y),
                        end = Offset(size.width - rightPadding, y),
                        strokeWidth = 1f
                    )
                }

                // Y axis
                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding, topPadding),
                    end = Offset(leftPadding, topPadding + graphHeight),
                    strokeWidth = 2f
                )

                // X axis
                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding, topPadding + graphHeight),
                    end = Offset(size.width - rightPadding, topPadding + graphHeight),
                    strokeWidth = 2f
                )

                // Curve
                val path = Path()

                validJumps.forEachIndexed { index, (jump, result) ->
                    val px = x(jump.rawJump.date)
                    val py = y(result.height)

                    if (index == 0) {
                        path.moveTo(px, py.toFloat())
                    } else {
                        path.lineTo(px, py.toFloat())
                    }
                }

                drawPath(
                    color = graphColor,
                    path = path,
                    style = Stroke(width = 3f)
                )

                // Points
                validJumps.forEach { (jump, result) ->
                    drawCircle(
                        color = graphColor,
                        radius = 5f,
                        center = Offset(
                            x(jump.rawJump.date),
                            y(result.height).toFloat()
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JumpsHistoryScreenPreview() {
    val now = Date()
    val flightTimes = listOf(
        0.585,
        0.606,
        0.594,
        0.626,
        0.648
    )

    val jumps = flightTimes.mapIndexed { index, flightTime ->
        val takeOffTime = 1_000_000_000L
        val landingTime =
            takeOffTime + (flightTime * 1_000_000_000L).toLong()

        AnalyzedJump(
            rawJump = RawJump(
                id = "jump-$index",
                date = Date(
                    now.time -
                            (5 - index) * 24 * 60 * 60 * 1000L
                ),
                points = emptyList()
            ),
            result = JumpResult(
                takeOffTime = takeOffTime,
                landingTime = landingTime
            )
        )
    }

    PlyoMetricsTheme {
        JumpsHistoryScreen(jumps)
    }
}