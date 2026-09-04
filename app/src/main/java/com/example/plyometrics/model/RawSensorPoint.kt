package com.example.plyometrics.model

import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import kotlinx.serialization.Serializable

/**
 * Rotation and acceleration at a timestamp
 */
@Serializable
data class RawSensorPoint(
    var timestamp: Long,
    val acceleration: Acceleration,
    val rotation: Rotation
)
