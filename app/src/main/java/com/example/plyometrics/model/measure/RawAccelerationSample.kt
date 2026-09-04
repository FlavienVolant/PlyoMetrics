package com.example.plyometrics.model.measure

/**
 * An acceleration with its timestamp
 */
data class RawAccelerationSample(
    val timestamp: Long,
    val acceleration: Acceleration
)