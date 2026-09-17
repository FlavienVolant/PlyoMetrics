package com.example.plyometrics.analysis.filters

import com.example.plyometrics.analysis.VerticalAccelerationPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MovingAverageFilterTest {

    @Test
    fun `should return original points when input is smaller than window`() {
        val points = pointsOf(1f, 2f, 3f)

        val filter = MovingAverageFilter(windowSize = 5)

        val result = filter.filter(points)

        assertEquals(points, result)
    }

    @Test
    fun `should calculate moving average`() {
        val points = pointsOf(
            0f, 0f, 9f, 0f, 0f
        )

        val filter = MovingAverageFilter(windowSize = 3)

        val result = filter.filter(points)

        assertEquals(0f, result[0].value, 0.001f)
        assertEquals(3f, result[1].value, 0.001f)
        assertEquals(3f, result[2].value, 0.001f)
        assertEquals(3f, result[3].value, 0.001f)
        assertEquals(0f, result[4].value, 0.001f)
    }

    @Test
    fun `should preserve timestamps`() {
        val points = listOf(
            VerticalAccelerationPoint(100L, 1f),
            VerticalAccelerationPoint(200L, 2f),
            VerticalAccelerationPoint(300L, 3f),
            VerticalAccelerationPoint(400L, 4f),
            VerticalAccelerationPoint(500L, 5f)
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
        val points = pointsOf(
            1f, 2f, 3f, 4f, 5f
        )

        val filter = MovingAverageFilter(windowSize = 3)

        val result = filter.filter(points)

        // First point:
        // (1 + 2) / 2 = 1.5
        //
        // Last point:
        // (4 + 5) / 2 = 4.5

        assertEquals(1.5f, result[0].value, 0.001f)
        assertEquals(4.5f, result[4].value, 0.001f)
    }

    @Test
    fun `should preserve constant signal`() {
        val points = pointsOf(
            5f, 5f, 5f, 5f, 5f, 5f, 5f
        )

        val filter = MovingAverageFilter(windowSize = 5)

        val result = filter.filter(points)

        result.forEach {
            assertEquals(5f, it.value, 0.001f)
        }
    }

    private fun pointsOf(vararg values: Float): List<VerticalAccelerationPoint> {
        return values.mapIndexed { index, value ->
            VerticalAccelerationPoint(
                timestamp = index.toLong(),
                value = value
            )
        }
    }
}