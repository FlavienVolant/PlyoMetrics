package com.example.plyometrics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import java.text.SimpleDateFormat

@Composable
fun JumpItem(
    analyzedJump: AnalyzedJump,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]

    val dateFormatter = remember(locale) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", locale)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 12.dp,
                horizontal = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dateFormatter.format(analyzedJump.rawJump.date),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = analyzedJump.result?.let {
                String.format(
                    locale,
                    "%.2f m",
                    it.height
                )
            } ?: "Jump not detected",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JumpItemPreview() {
    PlyoMetricsTheme {
        JumpItem(
            analyzedJump = AnalyzedJump(
                RawJump(points = emptyList()),
                JumpResult(0L, 1_000L))
        )
    }
}