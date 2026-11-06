package com.example.plyometrics.analysis.visualization

sealed interface JumpChartData {
    data class Simulation(val points: List<SimulationChartPoint>): JumpChartData
    data class Detection(val points: List<DetectionChartPoint>): JumpChartData
}