package com.example.plyometrics.model

import kotlinx.serialization.Serializable

/**
 * m / s
 */
@Serializable
data class Velocity(
    val x: Float,
    val y: Float,
    val z: Float
)
