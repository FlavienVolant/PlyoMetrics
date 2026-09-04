package com.example.plyometrics.model

import com.example.plyometrics.model.measure.RawAccelerationSample
import com.example.plyometrics.model.measure.RawRotationSample
import kotlin.math.abs

object SampleMatcher {

    /**
     * For each rawAccelerationSample, find the rawRotationSample
     * with the closest timestamp.
     *
     * O(n * m)
     */
    fun match(
        accelerations: List<RawAccelerationSample>,
        rotations: List<RawRotationSample>
    ): List<RawSensorPoint> {

        val sensorPoints = accelerations.mapNotNull { acceleration ->

            val closestRotation = rotations.minByOrNull {
                abs(it.timestamp - acceleration.timestamp)
            }

            if (closestRotation == null) {
                return@mapNotNull null
            }

            RawSensorPoint(
                timestamp = acceleration.timestamp,
                acceleration = acceleration.acceleration,
                rotation = closestRotation.rotation
            )
        }

        return sensorPoints
    }
}
