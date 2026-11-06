package com.example.plyometrics.measurement

import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.AccelerationPoint
import com.example.plyometrics.model.Rotation
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
    override val timestamp: Long,
    override val acceleration: Acceleration,
    val rotation: Rotation
) : AccelerationPoint