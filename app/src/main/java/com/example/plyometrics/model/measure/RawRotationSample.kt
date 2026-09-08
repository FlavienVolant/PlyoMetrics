package com.example.plyometrics.model.measure

/**
 * Phone rotation measured at a specific point in time.
 *
 * @property timestamp Timestamp in ns, using the same time base as Android SensorEvent timestamps.
 * @property rotation Phone rotation represented as a quaternion.
 */
data class RawRotationSample(
    val timestamp: Long,
    val rotation: Rotation
)