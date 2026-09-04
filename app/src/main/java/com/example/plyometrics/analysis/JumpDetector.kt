package com.example.plyometrics.analysis

import com.example.plyometrics.model.RawSensorPoint

class JumpDetector {

    private val IMPULSE_THRESHOLD = 15f
    private val TAKE_OFF_THRESHOLD = 3f
    private val LANDING_THRESHOLD = 15f

    /**
     * Return the jump of a sensor session
     *
     * A jump if detected by finding:
     * - the impulse
     * - the take-off
     * - the landing
     *
     * Returns a [JumpResult] if all events are found, null otherwise
     */
    fun analyze(points: List<RawSensorPoint>): JumpResult? {

        val impulse = findImpulse(points) ?: return null

        val takeOff = findTakeOff(points, impulse) ?: return null

        val landing = findLanding(points, takeOff) ?: return null

        return JumpResult(
            takeOffTime = takeOff.timestamp / 1_000_000,
            landingTime = landing.timestamp / 1_000_000
        )
    }

    /**
     * Finds the [RawSensorPoint] corresponding to the impulse of the jump
     *
     * The impulse is the first acceleration peak
     *
     * Returns null if no impulse is found
     */
    fun findImpulse(points: List<RawSensorPoint>): RawSensorPoint? {

        for (i in 1 until points.size - 1) {

            val previous = points[i - 1]
            val current = points[i]
            val next = points[i + 1]

            val currentValue = current.acceleration.magnitude

            if (currentValue > IMPULSE_THRESHOLD &&
                currentValue > previous.acceleration.magnitude &&
                currentValue > next.acceleration.magnitude
            ) {
                return current
            }
        }

        return null
    }

    /**
     * Finds the [RawSensorPoint] corresponding to the take-off of the jump
     *
     * The search starts after the impulse, the take-off is detected when the
     * acceleration becomes close to zero
     *
     * Returns null if no take-off is found
     */
    fun findTakeOff(points: List<RawSensorPoint>, impulse: RawSensorPoint): RawSensorPoint? {

        val startIndex = points.indexOf(impulse)

        if (startIndex == -1)
            return null

        for (i in startIndex until points.size) {

            val point = points[i]

            if (point.acceleration.magnitude < TAKE_OFF_THRESHOLD)
                return point
        }

        return null
    }

    /**
     * Finds the [RawSensorPoint] corresponding to the landing of the jump
     *
     * The search starts after the take-off, the landing is detected as the
     * first acceleration peak
     *
     * Returns null if no landing is found
     */
    fun findLanding(points: List<RawSensorPoint>, takeOff: RawSensorPoint): RawSensorPoint? {

        val startIndex = points.indexOf(takeOff)

        if (startIndex == -1)
            return null

        var highest: RawSensorPoint? = null

        for (i in startIndex until points.size) {

            val point = points[i]
            val value = point.acceleration.magnitude

            if (value > LANDING_THRESHOLD &&
                (highest == null || value > highest.acceleration.magnitude)
            ) {
                highest = point
            }
        }

        return highest
    }
}