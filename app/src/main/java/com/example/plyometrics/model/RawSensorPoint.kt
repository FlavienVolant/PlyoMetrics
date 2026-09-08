package com.example.plyometrics.model

import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import kotlinx.serialization.Serializable

/**
 * Rotation and acceleration measured by the phone at a specific point in time.
 *
 * @property timestamp Timestamp in ns, using the same time base as Android SensorEvent timestamps.
 * @property acceleration Acceleration vector in m/s².
 * @property rotation Phone rotation represented as a quaternion.
 */
@Serializable
data class RawSensorPoint(
    var timestamp: Long,
    val acceleration: Acceleration,
    val rotation: Rotation
)
