package com.example.plyometrics.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun StartButton(
    isRunning: Boolean,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(if (isRunning) "Stop" else "Start")
    }
}

@Preview(showBackground = true)
@Composable
private fun StartButtonPreview() {
    PlyoMetricsTheme {
        StartButton(
            isRunning = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StopButtonPreview() {
    PlyoMetricsTheme {
        StartButton(
            isRunning = true,
            onClick = {}
        )
    }
}