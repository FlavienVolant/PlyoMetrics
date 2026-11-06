package com.example.plyometrics.sensor

import com.example.plyometrics.measurement.RawSensorPoint
import com.example.plyometrics.sensor.model.RawAccelerationSample
import com.example.plyometrics.sensor.model.RawRotationSample
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