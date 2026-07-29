package com.example.plyometrics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.ui.theme.PlyoMetricsTheme

@Composable
fun JumpResultCard(
    result: JumpResult
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%.1f cm".format(result.height * 100)
        )

        Spacer(Modifier.height(8.dp))

        Text("Flight time")
        Text("${result.flightTime} ms")
    }
}

@Preview(showBackground = true)
@Composable
private fun JumpResultCardPreview() {
    PlyoMetricsTheme {
        JumpResultCard(
            JumpResult(
                takeOffTime = 0,
                landingTime = 620
            )
        )
    }
}