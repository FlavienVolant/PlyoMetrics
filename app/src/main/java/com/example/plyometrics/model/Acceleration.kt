package com.example.plyometrics.model

import kotlin.math.sqrt

data class Acceleration(
    val x: Float,
    val y: Float,
    val z: Float
) {
    val magnitude: Float get() = sqrt(x * x + y * y + z * z)
}
