package com.example.plyometrics.analysis.simulation

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Position
import com.example.plyometrics.model.Velocity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MotionSimulatorTest {

    val simulator = MotionSimulator()

    @Test
    fun `empty input should return empty list`() {
        val result = simulator.simulate(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `first point should initialize motion state`() {
        val data = listOf(
            TransformedSensorPoint(
                timestamp = 1_000_000_000L,
                acceleration = Acceleration(2f, 3f, 4f)
            )
        )

        val result = simulator.simulate(data)

        assertEquals(1, result.size)

        assertEquals(Velocity(0f, 0f, 0f), result[0].velocity)
        assertEquals(Position(0f, 0f, 0f), result[0].position)
    }

    @Test
    fun `should correctly integrate constant acceleration over multiple intervals`() {
        val data = listOf(
            TransformedSensorPoint(
                timestamp = 0L,
                acceleration = Acceleration(2f, 0f, 0f)
            ),
            TransformedSensorPoint(
                timestamp = 1_000_000_000L,
                acceleration = Acceleration(2f, 0f, 0f)
            ),
            TransformedSensorPoint(
                timestamp = 2_000_000_000L,
                acceleration = Acceleration(2f, 0f, 0f)
            )
        )

        val result = simulator.simulate(data)

        assertEquals(2f, result[1].velocity.x, 0.0001f)
        assertEquals(1f, result[1].position.x, 0.0001f)

        assertEquals(4f, result[2].velocity.x, 0.0001f)
        assertEquals(4f, result[2].position.x, 0.0001f)
    }

    @Test
    fun `should use actual timestamps for integration`() {
        val data = listOf(
            TransformedSensorPoint(
                timestamp = 0L,
                acceleration = Acceleration(2f, 0f, 0f)
            ),
            TransformedSensorPoint(
                timestamp = 500_000_000L,
                acceleration = Acceleration(2f, 0f, 0f)
            ),
            TransformedSensorPoint(
                timestamp = 2_000_000_000L,
                acceleration = Acceleration(2f, 0f, 0f)
            )
        )

        val result = simulator.simulate(data)

        assertEquals(1f, result[1].velocity.x, 0.0001f)
        assertEquals(0.25f, result[1].position.x, 0.0001f)

        assertEquals(4f, result[2].velocity.x, 0.0001f)
        assertEquals(4f, result[2].position.x, 0.0001f)
    }
}