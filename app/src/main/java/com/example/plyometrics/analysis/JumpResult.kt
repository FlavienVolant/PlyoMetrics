package com.example.plyometrics.analysis

data class JumpResult(
    val takeOffTime: Long,
    val landingTime: Long
) {
    val flightTime: Long get() = landingTime - takeOffTime
}
