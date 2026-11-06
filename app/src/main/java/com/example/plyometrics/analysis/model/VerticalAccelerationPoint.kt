package com.example.plyometrics.analysis.model

/**
 * Vertical acceleration at a specific point in time.
 *
 * @property timestamp Timestamp in ns, using the same time base as Android SensorEvent timestamps.
 * @property value Vertical acceleration in m/s².
 */
@Deprecated("Use TransformedSensorPoint")
data class VerticalAccelerationPoint(
    val timestamp: Long,
    val value: Float
)