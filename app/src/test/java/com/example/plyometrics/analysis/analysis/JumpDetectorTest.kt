package com.example.plyometrics.analysis.analysis

import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.VerticalAccelerationPoint
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class JumpDetectorTest {

    private lateinit var detector: JumpDetector

    @BeforeEach
    fun setUp() {
        detector = JumpDetector(peakConfirmationPoints = 2)
    }

    @Test
    fun `findImpulse returns first peak`() {

        val points = listOf(
            VerticalAccelerationPoint(0, 9f),
            VerticalAccelerationPoint(1, 12f),
            VerticalAccelerationPoint(2, 15f),
            VerticalAccelerationPoint(3, 20f),
            VerticalAccelerationPoint(4, 18f),
            VerticalAccelerationPoint(5, 19f)
        )

        val impulse = detector.findImpulse(points)

        assertNotNull(impulse)
        Assertions.assertEquals(3L, impulse.timestamp)
        Assertions.assertEquals(20f, impulse.value)
    }

    @Test
    fun `findImpulse returns null when no peak exists`() {

        val points = listOf(
            VerticalAccelerationPoint(0, 9f),
            VerticalAccelerationPoint(20, 10f),
            VerticalAccelerationPoint(40, 10f),
            VerticalAccelerationPoint(60, 9f)
        )

        assertNull(detector.findImpulse(points))
    }

    @Test
    fun `findTakeOff returns first free fall point`() {

        val points = listOf(
            VerticalAccelerationPoint(0, 9f),
            VerticalAccelerationPoint(1, 15f),
            VerticalAccelerationPoint(2, 25f),
            VerticalAccelerationPoint(3, 36.7f),
            VerticalAccelerationPoint(4, 34f),
            VerticalAccelerationPoint(5, 27f),
            VerticalAccelerationPoint(6, 15f),
            VerticalAccelerationPoint(7, 7f),
            VerticalAccelerationPoint(8, 4.8f),
            VerticalAccelerationPoint(9, 4.1f),
            VerticalAccelerationPoint(10, 3.2f)
        )

        val impulse = points[3]
        val takeOff = detector.findTakeOff(points, impulse)

        Assertions.assertEquals(8L, takeOff?.timestamp)
        Assertions.assertEquals(4.8f, takeOff?.value)
    }

    @Test
    fun `findLanding returns first point before the start of landing peak`() {

        val points = listOf(
            VerticalAccelerationPoint(0, 4.0f),
            VerticalAccelerationPoint(1, 4.7f),
            VerticalAccelerationPoint(2, 4.3f),
            VerticalAccelerationPoint(3, 4.9f),
            VerticalAccelerationPoint(4, 10f),
            VerticalAccelerationPoint(5, 25f),
            VerticalAccelerationPoint(6, 40f),
            VerticalAccelerationPoint(7, 60f),
            VerticalAccelerationPoint(8, 71f),
            VerticalAccelerationPoint(9, 55f),
            VerticalAccelerationPoint(10, 40f)
        )

        val takeOff = points[0]
        val landing = detector.findLanding(points, takeOff)

        Assertions.assertEquals(3L, landing?.timestamp)
        Assertions.assertEquals(4.9f, landing?.value)
    }

    @Test
    fun `analyze returns jump result`() {

        val points = listOf(
            VerticalAccelerationPoint(0L, 9f),
            VerticalAccelerationPoint(10L, 15f),
            VerticalAccelerationPoint(20L, 25f),
            VerticalAccelerationPoint(30L, 36.7f), // impulse
            VerticalAccelerationPoint(40L, 34f),
            VerticalAccelerationPoint(50L, 27f),
            VerticalAccelerationPoint(60L, 15f),
            VerticalAccelerationPoint(70L, 7f),
            VerticalAccelerationPoint(80L, 4.8f), // take-off
            VerticalAccelerationPoint(90L, 4.1f),
            VerticalAccelerationPoint(100L, 3.2f),
            VerticalAccelerationPoint(110L, 4.0f),
            VerticalAccelerationPoint(120L, 4.7f), // last point below threshold
            VerticalAccelerationPoint(130L, 5.3f),
            VerticalAccelerationPoint(140L, 7f),
            VerticalAccelerationPoint(150L, 12f),
            VerticalAccelerationPoint(160L, 25f),
            VerticalAccelerationPoint(170L, 40f),
            VerticalAccelerationPoint(180L, 60f),
            VerticalAccelerationPoint(190L, 71f), // landing peak
            VerticalAccelerationPoint(200L, 55f),
            VerticalAccelerationPoint(210L, 40f)
        )

        val jump = detector.analyzeVerticalPoints(points)

        assertNotNull(jump)

        Assertions.assertEquals(80L, jump.takeOffTime)
        Assertions.assertEquals(120L, jump.landingTime)
        Assertions.assertEquals(120L - 80L, jump.flightTime)
    }
}