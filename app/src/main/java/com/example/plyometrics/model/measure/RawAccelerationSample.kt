package com.example.plyometrics.model.measure

/**
 * Acceleration measured by the phone at a specific point in time.
 * @property timestamp Timestamp in ns, using the same time base as Android SensorEvent timestamps.
 * @property acceleration Acceleration vector in m/s².
 */
data class RawAccelerationSample(
    val timestamp: Long,
    val acceleration: Acceleration
)