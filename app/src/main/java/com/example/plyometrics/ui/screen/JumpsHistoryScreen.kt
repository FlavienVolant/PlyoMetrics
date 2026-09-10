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
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

            val textMeasurer = rememberTextMeasurer()
            val textStyle = MaterialTheme.typography.labelSmall
            val textColor = MaterialTheme.colorScheme.onBackground

            Canvas(
                modifier = modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {

                val validJumps = jumps.mapNotNull { jump ->
                    jump.result?.let { result ->
                        jump to result
                    }
                }

                if (validJumps.isEmpty())
                    return@Canvas

                val leftPadding = 70f
                val rightPadding = 20f
                val topPadding = 20f
                val bottomPadding = 45f

                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding

                val heights = validJumps.map { (_, result) ->
                    result.height
                }

                val minHeight = 0.0
                val maxHeight = heights.max()

                val heightRange =
                    (maxHeight - minHeight).coerceAtLeast(0.001)

                fun x(index: Int): Float {

                    if (validJumps.size == 1) {
                        return leftPadding + graphWidth / 2
                    }

                    return leftPadding +
                            index.toFloat() / (validJumps.size - 1) *
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

                    val label = "${(value * 100).toInt()} cm"
                    val text = textMeasurer.measure(
                        text = label,
                        style = textStyle
                    )

                    drawText(
                        textLayoutResult = text,
                        topLeft = Offset(
                            leftPadding - text.size.width - 8f,
                            y - text.size.height / 2f
                        ),
                        color = textColor
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

                // X labels
                validJumps.forEachIndexed { index, (jump, _) ->
                    val px = x(index)
                    val py = topPadding + graphHeight

                    val dateFormatter = SimpleDateFormat(
                        "dd/MM",
                        Locale.getDefault()
                    )

                    val label = dateFormatter.format(jump.rawJump.date)

                    val text = textMeasurer.measure(
                        text = label,
                        style = textStyle
                    )

                    drawText(
                        textLayoutResult = text,
                        topLeft = Offset(
                            px - text.size.width / 2f,
                            py + 8f
                        ),
                        color = textColor
                    )
                }

                // Curve
                val path = Path()

                validJumps.forEachIndexed { index, (_, result) ->
                    val px = x(index)
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
                validJumps.forEachIndexed { index, (_, result) ->
                    drawCircle(
                        color = graphColor,
                        radius = 5f,
                        center = Offset(
                            x(index),
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