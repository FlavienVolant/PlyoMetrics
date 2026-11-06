package com.example.plyometrics.model

import kotlinx.serialization.Serializable

/**
 * Acceleration vector measured by the phone accelerometer.
 *
 * The accelerations are expressed in m/s²
 */
@Serializable
data class Acceleration(
    val x: Float,
    val y: Float,
    val z: Float
)