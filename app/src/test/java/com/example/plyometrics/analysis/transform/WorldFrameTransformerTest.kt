package com.example.plyometrics.analysis.transform

import com.example.plyometrics.measurement.RawSensorPoint
import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Rotation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class WorldFrameTransformerTest {

    private val transformer = WorldFrameTransformer()

    @Test
    fun `identity rotation should not change acceleration`() {
        val point = RawSensorPoint(
            timestamp = 1000L,
            acceleration = Acceleration(1f, 2f, 3f),
            rotation = Rotation(0f, 0f, 0f, 1f)
        )

        val result = transformer.transform(listOf(point))

        assertEquals(1f, result[0].acceleration.x, 0.0001f)
        assertEquals(2f, result[0].acceleration.y, 0.0001f)
        assertEquals(3f, result[0].acceleration.z, 0.0001f)
    }

    @Test
    fun `90 degree rotation around Z axis`(){
        val point = RawSensorPoint(
            timestamp = 1000L,
            acceleration = Acceleration(1f, 0f, 0f),
            rotation = Rotation(0f, 0f, sqrt(0.5f), sqrt(0.5f))
        )

        val result = transformer.transform(listOf(point))

        assertEquals(0f, result[0].acceleration.x, 0.0001f)
        assertEquals(1f, result[0].acceleration.y, 0.0001f)
        assertEquals(0f, result[0].acceleration.z, 0.0001f)
    }

    @Test
    fun `90 degree rotation around X axis`(){
        val point = RawSensorPoint(
            timestamp = 1000L,
            acceleration = Acceleration(0f, 1f, 0f),
            rotation = Rotation(sqrt(0.5f), 0f, 0f, sqrt(0.5f))
        )

        val result = transformer.transform(listOf(point))

        assertEquals(0f, result[0].acceleration.x, 0.0001f)
        assertEquals(0f, result[0].acceleration.y, 0.0001f)
        assertEquals(1f, result[0].acceleration.z, 0.0001f)
    }

    @Test
    fun `transform should preserve timestamp`() {
        val point = RawSensorPoint(
            timestamp = 123456789L,
            acceleration = Acceleration(1f, 2f, 3f),
            rotation = Rotation(0f, 0f, 0f, 1f)
        )

        val result = transformer.transform(listOf(point))

        assertEquals(123456789L, result[0].timestamp)
    }

    @Test
    fun `rotation should preserve acceleration magnitude`() {
        val acceleration = Acceleration(2f, -3f, 4f)

        val point = RawSensorPoint(
            timestamp = 1000L,
            acceleration = acceleration,
            rotation = Rotation(
                qx = 0.2f,
                qy = 0.3f,
                qz = 0.4f,
                qw = sqrt(0.71f)
            )
        )

        val result = transformer.transform(listOf(point))

        val transformed = result[0].acceleration

        val originalMagnitude = sqrt(
            acceleration.x * acceleration.x +
                    acceleration.y * acceleration.y +
                    acceleration.z * acceleration.z
        )

        val transformedMagnitude = sqrt(
            transformed.x * transformed.x +
                    transformed.y * transformed.y +
                    transformed.z * transformed.z
        )

        assertEquals(originalMagnitude, transformedMagnitude, 0.0001f)
    }
}