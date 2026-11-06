package com.example.plyometrics.analysis

import com.example.plyometrics.analysis.visualization.JumpChartData
import com.example.plyometrics.measurement.RawJump

data class AnalyzedJump (
    val rawJump: RawJump,
    val result: JumpResult?,
    val chartData: JumpChartData?
)