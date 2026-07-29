package com.example.plyometrics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ready")
        Text("Press Start and perform a jump.")
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    PlyoMetricsTheme {
        EmptyState()
    }
}