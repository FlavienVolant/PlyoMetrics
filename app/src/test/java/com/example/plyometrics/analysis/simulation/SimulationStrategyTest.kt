package com.example.plyometrics.analysis.simulation

import com.example.plyometrics.analysis.JumpAnalysisStrategy
import com.example.plyometrics.analysis.JumpAnalysisStrategyTest
import com.example.plyometrics.analysis.filter.MovingAverageFilter
import com.example.plyometrics.analysis.transform.WorldFrameTransformer

class SimulationStrategyTest: JumpAnalysisStrategyTest() {
    override fun jumpAnalysisStrategy(): JumpAnalysisStrategy {
        return SimulationStrategy(
            transformer = WorldFrameTransformer(),
            filter = MovingAverageFilter(),
            simulator = MotionSimulator()
        )
    }
}