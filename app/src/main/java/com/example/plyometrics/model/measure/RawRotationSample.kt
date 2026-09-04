package com.example.plyometrics.model.measure

/**
 * Phone rotation at a timestamp
 */
data class RawRotationSample(
    val timestamp: Long,
    val rotation: Rotation
)