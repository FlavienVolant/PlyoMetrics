package com.example.plyometrics.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun ExportButton(onClick: () -> Unit) = Button(onClick = onClick) {
    Text("Export Session")
}


@Preview(showBackground = true)
@Composable
fun ExportButtonPreview() {
    PlyoMetricsTheme {
        ExportButton(
            onClick = {}
        )
    }
}