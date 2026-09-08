package com.example.plyometrics.analysis

/**
 * Vertical acceleration at a specific point in time.
 *
 * @property timestamp Timestamp in ns, using the same time base as Android SensorEvent timestamps.
 * @property value Vertical acceleration in m/s².
 */
data class VerticalAccelerationPoint(
    val timestamp: Long,
    val value: Float
)