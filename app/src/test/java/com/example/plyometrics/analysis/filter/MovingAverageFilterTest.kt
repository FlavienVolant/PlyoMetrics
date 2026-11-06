package com.example.plyometrics.analysis.filter

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.model.Acceleration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MovingAverageFilterTest {

    private fun point(
        timestamp: Long,
        x: Float,
        y: Float,
        z: Float
    ) = TransformedSensorPoint(
        timestamp = timestamp,
        acceleration = Acceleration(x, y, z)
    )

    @Test
    fun `should calculate moving average on all axes independently`() {
        val points = listOf(
            point(0L, 0f, 0f, 0f),
            point(1L, 0f, 0f, 0f),
            point(2L, 9f, 18f, 27f),
            point(3L, 0f, 0f, 0f),
            point(4L, 0f, 0f, 0f)
        )

        val filter = MovingAverageFilter(windowSize = 3)

        val result = filter.filter(points)

        assertEquals(0f, result[0].acceleration.x, 0.001f)
        assertEquals(3f, result[1].acceleration.x, 0.001f)
        assertEquals(3f, result[2].acceleration.x, 0.001f)
        assertEquals(3f, result[3].acceleration.x, 0.001f)
        assertEquals(0f, result[4].acceleration.x, 0.001f)

        assertEquals(6f, result[1].acceleration.y, 0.001f)
        assertEquals(6f, result[2].acceleration.y, 0.001f)
        assertEquals(6f, result[3].acceleration.y, 0.001f)

        assertEquals(9f, result[1].acceleration.z, 0.001f)
        assertEquals(9f, result[2].acceleration.z, 0.001f)
        assertEquals(9f, result[3].acceleration.z, 0.001f)
    }

    @Test
    fun `should preserve timestamps`() {
        val points = listOf(
            point(100L, 1f, 10f, 100f),
            point(200L, 2f, 20f, 200f),
            point(300L, 3f, 30f, 300f),
            point(400L, 4f, 40f, 400f),
            point(500L, 5f, 50f, 500f)
        )

        val filter = MovingAverageFilter(windowSize = 3)

        val result = filter.filter(points)

        assertEquals(
            points.map { it.timestamp },
            result.map { it.timestamp }
        )
    }

    @Test
    fun `should use truncated windows at boundaries`() {
        val points = listOf(
            point(0L, 1f, 1f, 1f),
            point(1L, 2f, 2f, 2f),
            point(2L, 3f, 3f, 3f),
            point(3L, 4f, 4f, 4f),
            point(4L, 5f, 5f, 5f)
        )

        val filter = MovingAverageFilter(windowSize = 3)

        val result = filter.filter(points)

        // First point: (1 + 2) / 2 = 1.5
        // Last point: (4 + 5) / 2 = 4.5
        assertEquals(1.5f, result[0].acceleration.x, 0.001f)
        assertEquals(4.5f, result[4].acceleration.x, 0.001f)
    }

    @Test
    fun `should preserve constant signal`() {
        val points = (0L until 7L).map {
            point(it, 5f, 10f, 15f)
        }

        val filter = MovingAverageFilter(windowSize = 5)

        val result = filter.filter(points)

        result.forEach {
            assertEquals(5f, it.acceleration.x, 0.001f)
            assertEquals(10f, it.acceleration.y, 0.001f)
            assertEquals(15f, it.acceleration.z, 0.001f)
        }
    }

    @Test
    fun `should handle a single point`() {
        val points = listOf(
            point(100L, 1f, 2f, 3f)
        )

        val filter = MovingAverageFilter(windowSize = 5)

        val result = filter.filter(points)

        assertEquals(points, result)
    }

    @Test
    fun `should normalize invalid window size`() {
        val filter = MovingAverageFilter(windowSize = 0)

        val points = listOf(
            point(0L, 1f, 2f, 3f),
            point(1L, 4f, 5f, 6f)
        )

        val result = filter.filter(points)

        assertEquals(points, result)
    }
}