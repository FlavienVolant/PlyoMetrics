package com.example.plyometrics.model

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

/**
 * Acceleration vector measured by the phone accelerometer
 */
@Serializable
data class Acceleration(
    val x: Float,
    val y: Float,
    val z: Float
) {
    val magnitude: Float get() = sqrt(x * x + y * y + z * z)
}
