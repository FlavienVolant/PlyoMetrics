package com.example.plyometrics.ui.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.plyometrics.analysis.JumpAnalysisMode

@Composable
fun AnalysisModeSelector(
    selectedMode: JumpAnalysisMode,
    enabled: Boolean,
    onModeChange: (JumpAnalysisMode) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = selectedMode == JumpAnalysisMode.DETECTION,
            onClick = {
                onModeChange(JumpAnalysisMode.DETECTION)
            },
            enabled = enabled,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Detection")
        }

        SegmentedButton(
            selected = selectedMode == JumpAnalysisMode.SIMULATION,
            onClick = {
                onModeChange(JumpAnalysisMode.SIMULATION)
            },
            enabled = enabled,
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Simulation")
        }
    }
}