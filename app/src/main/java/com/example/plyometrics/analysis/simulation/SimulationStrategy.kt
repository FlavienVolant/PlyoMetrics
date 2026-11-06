package com.example.plyometrics.analysis.simulation

import android.util.Log
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpAnalysisStrategy
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.filter.AccelerationFilter
import com.example.plyometrics.analysis.filter.NotAFilter
import com.example.plyometrics.analysis.transform.SensorDataTransformer
import com.example.plyometrics.analysis.transform.WorldFrameTransformer
import com.example.plyometrics.analysis.visualization.JumpChartData
import com.example.plyometrics.analysis.visualization.SimulationChartPoint
import com.example.plyometrics.measurement.RawJump

class SimulationStrategy(
    private val transformer: SensorDataTransformer = WorldFrameTransformer(),
    private val filter: AccelerationFilter = NotAFilter(),
    private val simulator: MotionSimulator = MotionSimulator()
): JumpAnalysisStrategy {
    override fun analyze(rawJump: RawJump): AnalyzedJump {

        if (rawJump.points.isEmpty())
            return AnalyzedJump(rawJump, null, null)

        val gravity = 9.67f // trial and error value
        val transformedPoints = transformer.transform(rawJump.points)

        val linearAccelerationPoints = transformedPoints.map { point ->
            point.copy(
                acceleration = point.acceleration.copy(
                    z = point.acceleration.z - gravity
                )
            )
        }

        Log.d("Simulation", "Compensated: ${linearAccelerationPoints.take(20)}")

        val filteredPoints = filter.filter(linearAccelerationPoints)
        val motionPoints = simulator.simulate(filteredPoints)

        if (motionPoints.isEmpty())
            return AnalyzedJump(rawJump, null, null)

        val chartData = JumpChartData.Simulation(
            motionPoints.map {
                SimulationChartPoint(
                    it.timestamp,
                    it.position.z,
                    it.velocity.z
                )
            }
        )

        val highestPoint = motionPoints.maxBy { it.position.z }

        val height = highestPoint.position.z

        if (height <= 0f)
            return AnalyzedJump(rawJump, null, chartData)

        val ascentTimeSeconds = kotlin.math.sqrt(2f * height / gravity)

        val ascentTimeNs = (ascentTimeSeconds * 1_000_000_000f).toLong()

        val takeOffTime = highestPoint.timestamp - ascentTimeNs
        val landingTime = highestPoint.timestamp + ascentTimeNs

        return AnalyzedJump(
            rawJump,
            JumpResult(takeOffTime, landingTime, height),
            chartData
        )
    }
}