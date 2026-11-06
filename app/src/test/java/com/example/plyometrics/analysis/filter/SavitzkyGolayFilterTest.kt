package com.example.plyometrics.analysis.filter

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.model.Acceleration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SavitzkyGolayFilterTest {

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
    fun `should preserve constant signal`() {
        val points = (0L until 10L).map {
            point(it, 5f, 10f, 15f)
        }

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        result.forEach {
            assertEquals(5f, it.acceleration.x, 0.001f)
            assertEquals(10f, it.acceleration.y, 0.001f)
            assertEquals(15f, it.acceleration.z, 0.001f)
        }
    }

    @Test
    fun `should preserve linear signal`() {
        val points = (0L until 9L).map {
            val value = it.toFloat()
            point(it, value, value * 2f, value * 3f)
        }

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // The filter should preserve a linear signal.
        for (i in 2 until points.size - 2) {
            assertEquals(
                points[i].acceleration.x,
                result[i].acceleration.x,
                0.001f
            )
            assertEquals(
                points[i].acceleration.y,
                result[i].acceleration.y,
                0.001f
            )
            assertEquals(
                points[i].acceleration.z,
                result[i].acceleration.z,
                0.001f
            )
        }
    }

    @Test
    fun `should preserve quadratic signal`() {
        val points = (0L until 9L).map {
            val value = it.toFloat()
            val square = value * value

            point(it, square, square * 2f, square * 3f)
        }

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // A polynomial of degree 2 should be preserved
        // by a second-order Savitzky-Golay filter.
        for (i in 2 until points.size - 2) {
            assertEquals(
                points[i].acceleration.x,
                result[i].acceleration.x,
                0.001f
            )
            assertEquals(
                points[i].acceleration.y,
                result[i].acceleration.y,
                0.001f
            )
            assertEquals(
                points[i].acceleration.z,
                result[i].acceleration.z,
                0.001f
            )
        }
    }

    @Test
    fun `should preserve timestamps`() {
        val points = (0L until 7L).map {
            val value = it.toFloat()

            point(
                timestamp = (it + 1L) * 100L,
                x = value,
                y = value * 2f,
                z = value * 3f
            )
        }

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
        val points = (0L until 7L).map {
            val value = (it + 1L).toFloat()

            point(it, value, value * 2f, value * 3f)
        }

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        // With a window size of 5, two points on each
        // side cannot be filtered.
        for (i in listOf(0, 1, 5, 6)) {
            assertEquals(points[i], result[i])
        }
    }

    @Test
    fun `should return original points when input is smaller than window`() {
        val points = listOf(
            point(0L, 1f, 2f, 3f),
            point(1L, 4f, 5f, 6f),
            point(2L, 7f, 8f, 9f)
        )

        val filter = SavitzkyGolayFilter(
            windowSize = 5,
            polynomialOrder = 2
        )

        val result = filter.filter(points)

        assertEquals(points, result)
    }
}
