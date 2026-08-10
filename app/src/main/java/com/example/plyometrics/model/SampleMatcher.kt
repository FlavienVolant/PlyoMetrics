package com.example.plyometrics.model

import kotlin.math.abs

object SampleMatcher {

    /**
     * For each acceleration, find the rotation sample
     * with the closest timestamp.
     *
     * O(n * m)
     */
    fun match(
        accelerations: List<AccelerationSample>,
        rotations: List<RotationSample>
    ): List<SensorPoint> {

        val sensorPoints = accelerations.mapNotNull { acceleration ->

            val closestRotation = rotations.minByOrNull {
                abs(it.timestamp - acceleration.timestamp)
            }

            if (closestRotation == null) {
                return@mapNotNull null
            }

            SensorPoint(
                timestamp = acceleration.timestamp,
                acceleration = acceleration.acceleration,
                rotationVector = closestRotation.rotationVector
            )
        }

        return sensorPoints
    }
}
