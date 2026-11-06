package com.example.plyometrics.analysis

/**
 * Result of a detected vertical jump.
 *
 * @property takeOffTime Take-off timestamp in ns.
 * @property landingTime Landing timestamp in ns.
 */
data class JumpResult(
    val takeOffTime: Long,
    val landingTime: Long,
    val height: Float
) {
    /**
     * Duration of the flight phase in ns.
     */
    val flightTime: Long get() = landingTime - takeOffTime
}