package com.example.plyometrics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun RecordingCard() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("● Recording...")
        Text("Jump when ready")
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordingCardPreview() {
    PlyoMetricsTheme {
        RecordingCard()
    }
}