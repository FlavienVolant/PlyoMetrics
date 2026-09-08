package com.example.plyometrics.model.measure

import kotlinx.serialization.Serializable

/**
 * Phone rotation represented as a quaternion
 */
@Serializable
data class Rotation(
    val qx: Float,
    val qy: Float,
    val qz: Float,
    val qw: Float
)