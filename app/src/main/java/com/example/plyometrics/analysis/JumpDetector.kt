package com.example.plyometrics.analysis

import com.example.plyometrics.model.SensorPoint

class JumpDetector {

    private val IMPULSE_THRESHOLD = 15f
    private val TAKE_OFF_THRESHOLD = 3f
    private val LANDING_THRESHOLD = 15f

    fun analyze(points: List<SensorPoint>): JumpResult? {

        val impulse = findImpulse(points) ?: return null

        val takeOff = findTakeOff(points, impulse) ?: return null

        val landing = findLanding(points, takeOff) ?: return null

        return JumpResult(
            takeOffTime = takeOff.time,
            landingTime = landing.time
        )
    }

    fun findImpulse(points: List<SensorPoint>): SensorPoint? {

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

    fun findTakeOff(points: List<SensorPoint>, impulse: SensorPoint): SensorPoint? {

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

    fun findLanding(points: List<SensorPoint>, takeOff: SensorPoint): SensorPoint? {

        val startIndex = points.indexOf(takeOff)

        if (startIndex == -1)
            return null

        var highest: SensorPoint? = null

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