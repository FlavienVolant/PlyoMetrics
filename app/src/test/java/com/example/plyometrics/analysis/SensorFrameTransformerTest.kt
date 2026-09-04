package com.example.plyometrics.analysis

import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SensorFrameTransformerTest {

    private val transformer = SensorFrameTransformer()

    @Test
    fun `identity quaternion does not change acceleration`() {
        val point = RawSensorPoint(
            timestamp = 0,
            acceleration = Acceleration(
                x = 1f,
                y = 2f,
                z = 3f
            ),
            rotation = Rotation(
                0f, 0f, 0f, 1f
            )
        )

        val result = transformer.toWorldFrame(point)

        assertEquals(3f, result.value, 0.0001f)
    }

    @Test
    fun `rotation of 90 degrees around Z transforms X into Y`() {
        val point = RawSensorPoint(
            timestamp = 0,
            acceleration = Acceleration(
                x = 1f,
                y = 0f,
                z = 0f
            ),
            rotation = Rotation(
                0f,
                0f,
                0.70710677f,
                0.70710677f
            )
        )

        val result = transformer.toWorldFrame(point)

        assertEquals(0f, result.value, 0.0001f)
    }
}