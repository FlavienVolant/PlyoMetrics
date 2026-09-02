package com.example.plyometrics.model

/**
 * An acceleration with its timestamp
 */
data class AccelerationSample(
    val timestamp: Long,
    val acceleration: Acceleration
)