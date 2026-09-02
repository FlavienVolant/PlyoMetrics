package com.example.plyometrics.model

/**
 * This class is the height acceleration after the rotation to the world frame
 */
data class JumpPoint(
    var timestamp: Long,
    val accelerationZ: Float
)