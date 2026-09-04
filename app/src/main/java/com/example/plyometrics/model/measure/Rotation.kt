package com.example.plyometrics.model.measure

import kotlinx.serialization.Serializable

@Serializable
data class Rotation(
    val qx: Float,
    val qy: Float,
    val qz: Float,
    val qw: Float
)