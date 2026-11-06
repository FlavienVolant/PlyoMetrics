package com.example.plyometrics.analysis

import com.example.plyometrics.measurement.RawJump
import com.example.plyometrics.measurement.RawSensorPoint
import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Rotation
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

abstract class JumpAnalysisStrategyTest {
    abstract fun jumpAnalysisStrategy(): JumpAnalysisStrategy

    @Test
    fun `analyze return null when RawJump points is empty`(){
        val analysisStrategy = jumpAnalysisStrategy()
        val rawJump = RawJump(points = emptyList())

        assertNull(analysisStrategy.analyze(rawJump).result)
        assertNull(analysisStrategy.analyze(rawJump).chartData)
    }

    @Disabled
    @Test
    fun `analyze returns null when no jump is detected`() {
        val analysisStrategy = jumpAnalysisStrategy()

        val rawJump = RawJump(
            points = (0L..20L).map { timestamp ->
                RawSensorPoint(
                    timestamp = timestamp * 20_000_000L,
                    acceleration = Acceleration(0f, 0f, 0f),
                    rotation = Rotation(
                        qx = 0f,
                        qy = 0f,
                        qz = 0f,
                        qw = 1f
                    )
                )
            }
        )

        assertNull(analysisStrategy.analyze(rawJump).result)
        assertNotNull(analysisStrategy.analyze(rawJump).chartData)
    }

    @Disabled
    @Test
    fun `analyze returns null when no point is above initial position`() {
        val analysisStrategy = jumpAnalysisStrategy()

        val rawJump = RawJump(
            points = (0L..10L).map { timestamp ->
                RawSensorPoint(
                    timestamp = timestamp * 20_000_000L,
                    acceleration = Acceleration(0f, 0f, 0f),
                    rotation = Rotation(0f, 0f, 0f, 1f)
                )
            }
        )

        assertNull(analysisStrategy.analyze(rawJump).result)
        assertNotNull(analysisStrategy.analyze(rawJump).chartData)
    }
}
