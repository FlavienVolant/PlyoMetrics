package com.example.plyometrics.analysis

data class JumpResult(
    val takeOffTime: Long,
    val landingTime: Long
) {
    val flightTime: Long get() = landingTime - takeOffTime
    val height: Double get() {
        val timeSeconds = flightTime / 1000.0
        return 9.81 * timeSeconds * timeSeconds / 8
    }
}
