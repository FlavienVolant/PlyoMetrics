package com.example.plyometrics.analysis.filters

import com.example.plyometrics.analysis.VerticalAccelerationPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SavitzkyGolayFilterTest {

    @Test
    fun `should preserve constant signal`() {
        val points = pointsOf(
            5f, 5f, 5f, 5f, 5f,
            5f, 5f, 5f, 5f, 5f
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        result.forEach {
            assertEquals(5f, it.value, 0.001f)
        }
    }

    @Test
    fun `should preserve linear signal`() {
        val points = pointsOf(
            0f, 1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // The filter should preserve a linear signal.
        for (i in 2 until points.size - 2) {
            assertEquals(
                points[i].value,
                result[i].value,
                0.001f
            )
        }
    }

    @Test
    fun `should preserve quadratic signal`() {
        val points = pointsOf(
            0f, 1f, 4f, 9f, 16f,
            25f, 36f, 49f, 64f
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // A polynomial of degree 2 should be preserved
        // by a second-order Savitzky-Golay filter.
        for (i in 2 until points.size - 2) {
            assertEquals(
                points[i].value,
                result[i].value,
                0.001f
            )
        }
    }

    @Test
    fun `should preserve timestamps`() {
        val points = listOf(
            VerticalAccelerationPoint(100L, 1f),
            VerticalAccelerationPoint(200L, 2f),
            VerticalAccelerationPoint(300L, 3f),
            VerticalAccelerationPoint(400L, 4f),
            VerticalAccelerationPoint(500L, 5f),
            VerticalAccelerationPoint(600L, 6f),
            VerticalAccelerationPoint(700L, 7f)
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        assertEquals(
            points.map { it.timestamp },
            result.map { it.timestamp }
        )
    }

    @Test
    fun `should keep boundary points unchanged`() {
        val points = pointsOf(
            1f, 2f, 3f, 4f, 5f,
            6f, 7f
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // With a window size of 5, two points on each
        // side cannot be filtered.
        assertEquals(1f, result[0].value, 0.001f)
        assertEquals(2f, result[1].value, 0.001f)

        assertEquals(6f, result[5].value, 0.001f)
        assertEquals(7f, result[6].value, 0.001f)
    }

    @Test
    fun `should return original points when input is smaller than window`() {
        val points = pointsOf(
            1f, 2f, 3f
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        assertEquals(points, result)
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