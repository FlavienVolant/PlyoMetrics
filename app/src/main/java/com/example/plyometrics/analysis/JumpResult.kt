package com.example.plyometrics.analysis

/**
 * Result of a detected vertical jump.
 *
 * @property takeOffTime Take-off timestamp in ns.
 * @property landingTime Landing timestamp in ns.
 */
data class JumpResult(
    val takeOffTime: Long,
    val landingTime: Long
) {
    /**
     * Duration of the flight phase in ns.
     */
    val flightTime: Long get() = landingTime - takeOffTime

    /**
     * Estimated jump height in meter, computed from the flight time.
     */
    val height: Double get() {
        val timeSeconds = flightTime / 1_000_000_000.0
        return 9.81 * timeSeconds * timeSeconds / 8
    }
}
