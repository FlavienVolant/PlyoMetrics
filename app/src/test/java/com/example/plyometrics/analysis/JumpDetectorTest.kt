package com.example.plyometrics.analysis

import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.SensorPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test

class JumpDetectorTest {

    private lateinit var detector: JumpDetector

    @Before
    fun setUp() {
        detector = JumpDetector()
    }

    private fun point(time: Long, magnitude: Float): SensorPoint =
        SensorPoint(time, Acceleration(0f, 0f, magnitude))

    @Test
    fun `findImpulse returns first peak`() {

        val session = listOf(
            point(0, 9.8f),
            point(20, 10f),
            point(40, 18f),
            point(60, 23f),
            point(80, 15f)
        )

        val impulse = detector.findImpulse(session)

        assertNotNull(impulse)
        assertEquals(60L, impulse!!.time)
    }

    @Test
    fun `findImpulse returns null when no peak exists`() {

        val session = listOf(
            point(0, 9.8f),
            point(20, 10f),
            point(40, 10.2f),
            point(60, 9.9f)
        )

        assertNull(detector.findImpulse(session))
    }

    @Test
    fun `findTakeOff returns first free fall point`() {

        val session = listOf(
            point(0, 9.8f),
            point(20, 20f),   // impulsion
            point(40, 15f),
            point(60, 2f),    // lift off
            point(80, 0.5f)
        )

        val impulse = detector.findImpulse(session)!!

        val takeOff = detector.findTakeOff(session, impulse)

        assertEquals(60L, takeOff!!.time)
    }

    @Test
    fun `findLanding returns first landing peak`() {

        val session = listOf(
            point(0, 9.8f),
            point(20, 20f),
            point(40, 2f),
            point(60, 0.5f),
            point(80, 1f),
            point(100, 18f),
            point(120, 24f)
        )

        val takeOff = detector.findTakeOff(
                session,
                detector.findImpulse(session)!!
            )!!

        val landing = detector.findLanding(session, takeOff)

        assertEquals(120L, landing!!.time)
    }

    @Test
    fun `analyze returns jump result`() {

        val session = listOf(
            point(0, 9.8f),
            point(20, 20f),
            point(40, 15f),
            point(60, 2f),
            point(80, 0.5f),
            point(100, 0.8f),
            point(120, 18f),
            point(140, 24f)
        )

        val jump = detector.analyze(session)

        assertNotNull(jump)

        assertEquals(60L, jump!!.takeOffTime)
        assertEquals(140L, jump.landingTime)
        assertEquals(80L, jump.flightTime)
    }
}