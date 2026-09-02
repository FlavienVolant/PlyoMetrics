package com.example.plyometrics.model

/**
 * Phone rotation at a timestamp
 */
data class RotationSample(
    val timestamp: Long,
    val rotation: Rotation
)