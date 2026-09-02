package com.example.plyometrics.model

import kotlinx.serialization.Serializable

/**
 * Rotation and acceleration at a timestamp
 */
@Serializable
data class SensorPoint(
    var timestamp: Long,
    val acceleration: Acceleration,
    val rotation: Rotation
)
